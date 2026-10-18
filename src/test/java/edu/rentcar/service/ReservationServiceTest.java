package edu.rentcar.service;

import edu.rentcar.enums.ReservationStatus;
import edu.rentcar.model.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationServiceTest {
  private ReservationService service;

  @BeforeEach
  void setUp() {
    service = new ReservationService();
  }

  private Reservation pending(String vehicleId, int startDay, int endDay) {
    return service.createPendingReservation("CUS-1001", vehicleId,
        LocalDate.of(2026, 11, startDay), LocalDate.of(2026, 11, endDay),
        "Economy", 400_000, "Test rental");
  }

  @Test
  void newReservationIsPending() {
    Reservation reservation = pending("VEH-1001", 1, 4);

    assertEquals(ReservationStatus.PENDING, reservation.getStatus());
    assertEquals("RES-0001", reservation.getId());
  }

  @Test
  void confirmChangesStatusAndKeepsCostAndDescription() {
    Reservation reservation = pending("VEH-1001", 1, 4);

    Reservation confirmed = service.confirm(reservation.getId());

    assertEquals(ReservationStatus.CONFIRMED, confirmed.getStatus());
    assertEquals(400_000, confirmed.getTotalCost(), 0.001);
    assertEquals("Test rental", confirmed.getDescription());
  }

  @Test
  void failChangesStatusToFailed() {
    Reservation reservation = pending("VEH-1001", 1, 4);

    assertEquals(ReservationStatus.FAILED, service.fail(reservation.getId()).getStatus());
  }

  @Test
  void confirmedReservationCannotBeChangedAgain() {
    Reservation reservation = pending("VEH-1001", 1, 4);
    service.confirm(reservation.getId());

    assertThrows(IllegalStateException.class, () -> service.fail(reservation.getId()));
  }

  @Test
  void invertedDatesAreRejected() {
    assertThrows(IllegalArgumentException.class, () -> pending("VEH-1001", 5, 2));
  }

  @Test
  void nonPositiveCostIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> service.createPendingReservation(
        "CUS-1001", "VEH-1001", LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 4),
        "Economy", 0, "Test rental"));
  }

  @Test
  void overlappingConfirmedReservationBlocksTheVehicle() {
    service.confirm(pending("VEH-1001", 1, 5).getId());

    assertFalse(service.isAvailable("VEH-1001", LocalDate.of(2026, 11, 3), LocalDate.of(2026, 11, 7)));
    assertThrows(IllegalStateException.class, () -> pending("VEH-1001", 3, 7));
  }

  @Test
  void adjacentDatesAreAvailable() {
    service.confirm(pending("VEH-1001", 1, 5).getId());

    assertTrue(service.isAvailable("VEH-1001", LocalDate.of(2026, 11, 5), LocalDate.of(2026, 11, 8)));
  }

  @Test
  void otherVehicleIsNotAffected() {
    service.confirm(pending("VEH-1001", 1, 5).getId());

    assertTrue(service.isAvailable("VEH-1002", LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 5)));
  }

  @Test
  void pendingAndFailedReservationsDoNotBlockTheVehicle() {
    Reservation first = pending("VEH-1001", 1, 5);
    service.fail(first.getId());
    pending("VEH-1001", 1, 5);   // stays pending

    assertTrue(service.isAvailable("VEH-1001", LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 5)));
  }

  @Test
  void secondPendingFailsToConfirmIfVehicleWasTakenMeanwhile() {
    Reservation a = pending("VEH-1001", 1, 5);
    Reservation b = pending("VEH-1001", 2, 6);   // allowed: nothing confirmed yet

    assertEquals(ReservationStatus.CONFIRMED, service.confirm(a.getId()).getStatus());
    assertEquals(ReservationStatus.FAILED, service.confirm(b.getId()).getStatus());
  }

  @Test
  void reservationsCanBeListed() {
    pending("VEH-1001", 1, 4);
    pending("VEH-1002", 1, 4);

    assertEquals(2, service.getAllReservations().size());
  }
}
