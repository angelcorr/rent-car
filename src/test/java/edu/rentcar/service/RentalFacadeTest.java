package edu.rentcar.service;

import edu.rentcar.enums.ReservationStatus;
import edu.rentcar.facade.RentalFacade;
import edu.rentcar.facade.RentalResult;
import edu.rentcar.model.RentalRequest;
import edu.rentcar.payment.CustomerVerificationService;
import edu.rentcar.payment.PaymentGateway;
import edu.rentcar.payment.PaymentGatewayProxy;
import edu.rentcar.payment.PaymentStatus;
import edu.rentcar.pricing.IndividualService;
import edu.rentcar.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class RentalFacadeTest {
  static class FakeGateway implements PaymentGateway {
    int calls = 0;
    PaymentStatus result = PaymentStatus.APPROVED;

    @Override
    public PaymentStatus pay(String reservationId, String customerId, double amount, String token) {
      calls++;
      return result;
    }
  }

  private FakeGateway gateway;
  private ReservationService reservationService;
  private RentalFacade facade;

  @BeforeEach
  void setUp() {
    gateway = new FakeGateway();
    reservationService = new ReservationService();
    VehicleService vehicleService = new VehicleService(new VehicleRepository());
    facade = new RentalFacade(vehicleService, reservationService, gateway);
  }

  private RentalRequest.Builder request(String vehicleId, int startDay, int endDay) {
    return new RentalRequest.Builder()
        .customerId("CUS-1001")
        .vehicleId(vehicleId)
        .startDate(LocalDate.of(2026, 11, startDay))
        .endDate(LocalDate.of(2026, 11, endDay))
        .modality("Economy")
        .paymentToken("tok_approved");
  }

  @Test
  void successfulRentalIsConfirmed() {
    RentalResult result = facade.rent(request("VEH-1001", 1, 4).build());

    assertTrue(result.isSuccess());
    assertEquals(ReservationStatus.CONFIRMED, result.getReservation().getStatus());
    assertEquals(1, gateway.calls);
  }

  @Test
  void costIncludesModalityAndExtras() {
    RentalResult result = facade.rent(request("VEH-1001", 1, 4)
        .addService(new IndividualService("GPS", 15_000, "GPS")).build());

    assertEquals(565_000, result.getReservation().getTotalCost(), 0.001);
    assertTrue(result.getReservation().getDescription().contains("GPS"));
  }

  @Test
  void declinedPaymentIsNotConfirmed() {
    gateway.result = PaymentStatus.DECLINED;

    RentalResult result = facade.rent(request("VEH-1001", 1, 4).build());

    assertFalse(result.isSuccess());
    assertEquals(ReservationStatus.FAILED, result.getReservation().getStatus());
  }

  @Test
  void apiErrorIsNotConfirmed() {
    gateway.result = PaymentStatus.ERROR;

    RentalResult result = facade.rent(request("VEH-1001", 1, 4).build());

    assertFalse(result.isSuccess());
    assertEquals(ReservationStatus.FAILED, result.getReservation().getStatus());
  }

  @Test
  void overlappingConfirmedReservationBlocksTheVehicle() {
    facade.rent(request("VEH-1001", 1, 5).build());

    RentalResult second = facade.rent(request("VEH-1001", 3, 7).build());

    assertFalse(second.isSuccess());
    assertNull(second.getReservation());
    assertEquals(1, gateway.calls);   // the second attempt never reached the payment
  }

  @Test
  void adjacentDatesAreAllowed() {
    facade.rent(request("VEH-1001", 1, 5).build());

    RentalResult second = facade.rent(request("VEH-1001", 5, 8).build());

    assertTrue(second.isSuccess());
  }

  @Test
  void failedReservationDoesNotBlockTheVehicle() {
    gateway.result = PaymentStatus.DECLINED;
    facade.rent(request("VEH-1001", 1, 5).build());

    gateway.result = PaymentStatus.APPROVED;
    RentalResult second = facade.rent(request("VEH-1001", 1, 5).build());

    assertTrue(second.isSuccess());
  }

  @Test
  void unknownVehicleIsRejected() {
    RentalResult result = facade.rent(request("VEH-9999", 1, 4).build());

    assertFalse(result.isSuccess());
    assertEquals(0, gateway.calls);
  }

  @Test
  void unauthorizedCustomerNeverCallsTheApi() {
    FakeGateway api = new FakeGateway();
    CustomerVerificationService denyAll = customerId -> false;
    RentalFacade protectedFacade = new RentalFacade(
        new VehicleService(new VehicleRepository()),
        new ReservationService(),
        new PaymentGatewayProxy(api, denyAll));

    RentalResult result = protectedFacade.rent(request("VEH-1001", 1, 4).build());

    assertFalse(result.isSuccess());
    assertEquals(ReservationStatus.FAILED, result.getReservation().getStatus());
    assertEquals(0, api.calls);
  }
}
