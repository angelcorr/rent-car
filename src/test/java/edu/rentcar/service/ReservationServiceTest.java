package edu.rentcar.service;

import edu.rentcar.model.Reservation;
import Enums.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReservationServiceTest {

    private ReservationService service;
    private final LocalDate start = LocalDate.of(2026, 11, 1);
    private final LocalDate end = LocalDate.of(2026, 11, 5);

    @BeforeEach
    void setUp() {
        service = new ReservationService();
    }

    @Test
    void newReservationStartsPending() {
        Reservation reservation = service.createPendingReservation("R1", "C1", "V1", start, end);

        assertEquals(ReservationStatus.PENDING.name(), reservation.getStatus());
    }

    @Test
    void endDateBeforeStartDateIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createPendingReservation("R1", "C1", "V1", end, start));
    }

    @Test
    void sameStartAndEndDateIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createPendingReservation("R1", "C1", "V1", start, start));
    }

    @Test
    void blankCustomerIdIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createPendingReservation("R1", "   ", "V1", start, end));
    }

    @Test
    void duplicatedIdIsRejected() {
        service.createPendingReservation("R1", "C1", "V1", start, end);

        assertThrows(IllegalStateException.class,
                () -> service.createPendingReservation("R1", "C2", "V2", start, end));
    }

    @Test
    void pendingReservationCanBeConfirmed() {
        service.createPendingReservation("R1", "C1", "V1", start, end);

        Reservation confirmed = service.confirm("R1");

        assertEquals(ReservationStatus.COMPLETED.name(), confirmed.getStatus());
    }

    @Test
    void confirmedReservationCannotFail() {
        service.createPendingReservation("R1", "C1", "V1", start, end);
        service.confirm("R1");

        assertThrows(IllegalStateException.class, () -> service.fail("R1"));
    }
}