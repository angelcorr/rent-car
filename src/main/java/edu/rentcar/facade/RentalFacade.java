package edu.rentcar.facade;

import edu.rentcar.enums.ReservationStatus;
import edu.rentcar.model.RentalRequest;
import edu.rentcar.model.Reservation;
import edu.rentcar.model.Vehicle;
import edu.rentcar.payment.PaymentGateway;
import edu.rentcar.payment.PaymentStatus;
import edu.rentcar.pricing.FixedCost;
import edu.rentcar.pricing.RentService;
import edu.rentcar.pricing.decorator.ExtraServicesApplier;
import edu.rentcar.service.ReservationService;
import edu.rentcar.service.VehicleService;

import java.time.temporal.ChronoUnit;
import java.util.List;

public class RentalFacade {
  private final VehicleService vehicleService;
  private final ReservationService reservationService;
  private final PaymentGateway paymentGateway;

  public RentalFacade(VehicleService vehicleService, ReservationService reservationService, PaymentGateway paymentGateway) {
    this.vehicleService = vehicleService;
    this.reservationService = reservationService;
    this.paymentGateway = paymentGateway;
  }

  public RentalResult rent(RentalRequest request) {
    Vehicle vehicle;
    double totalCost;
    String description;

    try {
      vehicle = vehicleService.findVehicleById(request.getVehicleId());
      double modalityCost = reservationService.calculateCost(request.getStartDate(), request.getEndDate(), vehicle.getDailyRate(), request.getModality());

      long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
      RentService rental = new FixedCost(modalityCost, "Rental " + vehicle.getDisplayName() + " (" + days + " day(s), " + request.getModality() + ")");
      RentService withExtras = ExtraServicesApplier.applyTo(rental, request.getServices());
      totalCost = withExtras.getCost();
      description = withExtras.getDescription();
    } catch (IllegalArgumentException exception) {
      return new RentalResult(false, exception.getMessage(), null);
    }

    Reservation pending;
    try {
      pending = reservationService.createPendingReservation(request.getCustomerId(), request.getVehicleId(), request.getStartDate(), request.getEndDate(), request.getModality(), totalCost, description);
    } catch (IllegalStateException | IllegalArgumentException exception) {
      return new RentalResult(false, exception.getMessage(), null);
    }

    PaymentStatus status = paymentGateway.pay(pending.getId(), request.getCustomerId(), totalCost, request.getPaymentToken());

    if (status == PaymentStatus.APPROVED) {
      Reservation confirmed = reservationService.confirm(pending.getId());
      if (confirmed.getStatus() == ReservationStatus.CONFIRMED) {
        return new RentalResult(true, "Reservation confirmed", confirmed);
      }
      return new RentalResult(false, "Vehicle is no longer available.", confirmed);
    }

    Reservation failed =  reservationService.fail(pending.getId());
    return new RentalResult(false, messageFor(status), failed);
  }

  public List<Reservation> getReservations() {
    return reservationService.getAllReservations();
  }

  private String messageFor(PaymentStatus status) {
    return switch (status) {
      case DECLINED -> "Payment declined or customer is not authorized";
      case ERROR -> "Payment could not be completed. Try again later";
      case APPROVED -> "Payment approved";
    };
  }
}
