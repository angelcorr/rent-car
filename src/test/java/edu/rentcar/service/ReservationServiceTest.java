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
        Reservation reservation = service.createPendingReservation( "C1", "V1", start, end,"Economy");
        assertEquals(ReservationStatus.PENDING, reservation.getStatus());
    }

    @Test
    void endDateBeforeStartDateIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createPendingReservation( "C1", "V1", end, start,"Economy"));
    }

    @Test
    void sameStartAndEndDateIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createPendingReservation( "C1", "V1", start, start,"Economy"));
    }

    @Test
    void blankCustomerIdIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createPendingReservation( "   ", "V1", start, end,"Economy"));
    }

    @Test
    void reservationIdsAreGeneratedInSequence() {
        Reservation first = service.createPendingReservation("C1", "V1", start, end, "Economy");
        Reservation second = service.createPendingReservation("C2", "V2", start, end, "Economy");

        assertEquals("RES-0001", first.getId());
        assertEquals("RES-0002", second.getId());
    }

    @Test
    void pendingReservationCanBeConfirmed() {
        String id = service.createPendingReservation("C1", "V1", start, end, "Economy").getId();

        Reservation confirmed = service.confirm(id);

        assertEquals(ReservationStatus.COMPLETED, confirmed.getStatus());
    }

    @Test
    void confirmedReservationCannotFail() {
        String id= service.createPendingReservation( "C1", "V1", start, end,"Economy").getId();
        service.confirm(id);

        assertThrows(IllegalStateException.class, () -> service.fail(id));
    }
}
