package edu.rentcar.service;

import edu.rentcar.model.RentalRequest;
import edu.rentcar.pricing.IndividualService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class RentalRequestTest {
  private RentalRequest.Builder validBuilder() {
    return new RentalRequest.Builder()
        .customerId("CUS-1001")
        .vehicleId("VEH-1001")
        .startDate(LocalDate.of(2026, 11, 1))
        .endDate(LocalDate.of(2026, 11, 4))
        .modality("Economy")
        .paymentToken("tok_approved");
  }

  @Test
  void validRequestIsBuilt() {
    RentalRequest request = validBuilder().build();

    assertEquals("CUS-1001", request.getCustomerId());
    assertEquals("Economy", request.getModality());
    assertTrue(request.getServices().isEmpty());
  }

  @Test
  void servicesAreKept() {
    RentalRequest request = validBuilder()
        .addService(new IndividualService("GPS", 15_000, "GPS"))
        .build();

    assertEquals(1, request.getServices().size());
  }

  @Test
  void missingCustomerIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> validBuilder().customerId(" ").build());
  }

  @Test
  void missingVehicleIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> validBuilder().vehicleId(null).build());
  }

  @Test
  void missingDatesAreRejected() {
    assertThrows(IllegalArgumentException.class, () -> validBuilder().startDate(null).build());
  }

  @Test
  void invertedDatesAreRejected() {
    assertThrows(IllegalArgumentException.class, () -> validBuilder()
        .startDate(LocalDate.of(2026, 11, 10))
        .endDate(LocalDate.of(2026, 11, 5))
        .build());
  }

  @Test
  void sameDayDatesAreRejected() {
    LocalDate day = LocalDate.of(2026, 11, 1);
    assertThrows(IllegalArgumentException.class,
        () -> validBuilder().startDate(day).endDate(day).build());
  }

  @Test
  void missingPaymentTokenIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> validBuilder().paymentToken("").build());
  }
}
