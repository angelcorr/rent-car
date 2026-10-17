package edu.rentcar.service;

import edu.rentcar.model.Reservation;
import edu.rentcar.enums.ReservationStatus;
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
        Reservation reservation = service.createPendingReservation("R1", "C1", "V1", start, end,"Economy");
        assertEquals(ReservationStatus.PENDING, reservation.getStatus());
    }

    @Test
    void endDateBeforeStartDateIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createPendingReservation("R1", "C1", "V1", end, start,"Economy"));
    }

    @Test
    void sameStartAndEndDateIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createPendingReservation("R1", "C1", "V1", start, start,"Economy"));
    }

    @Test
    void blankCustomerIdIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createPendingReservation("R1", "   ", "V1", start, end,"Economy"));
    }

    @Test
    void duplicatedIdIsRejected() {
        service.createPendingReservation("R1", "C1", "V1", start, end,"Economy");

        assertThrows(IllegalStateException.class,
                () -> service.createPendingReservation("R1", "C2", "V2", start, end,"Economy"));
    }

    @Test
    void pendingReservationCanBeConfirmed() {
        service.createPendingReservation("R1", "C1", "V1", start, end,"Economy");

        Reservation confirmed = service.confirm("R1");

        assertEquals(ReservationStatus.COMPLETED, confirmed.getStatus());
    }

    @Test
    void confirmedReservationCannotFail() {
        service.createPendingReservation("R1", "C1", "V1", start, end,"Economy");
        service.confirm("R1");

        assertThrows(IllegalStateException.class, () -> service.fail("R1"));
    }
}