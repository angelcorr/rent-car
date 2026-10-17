package edu.rentcar.service;

import edu.rentcar.pricing.BaseCharge;
import edu.rentcar.enums.ReservationStatus;
import edu.rentcar.model.Reservation;
import edu.rentcar.interfaces.RentalModalityFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.temporal.ChronoUnit;


public class ReservationService {
    private final List<Reservation> reservations = new ArrayList<>();

    //Initialize reservation as pending
    public Reservation createPendingReservation(String id, String customerId, String vehicleId, LocalDate starDate, LocalDate endDate,String modality) {

        String cleanId = requireText(id,"Reservation id");
        String cleanCustomerId = requireText(customerId, "Customer id");
        String cleanVehicleId = requireText(vehicleId, "Vehicle id");
        String cleanModality = requireText(modality, "Modality");
        validateDates(starDate, endDate);

        if (existsReservation(cleanId)) {
            throw new IllegalStateException("A reservation with this id already exists: " + cleanId);
        }

        Reservation reservation = new Reservation.Builder()
                .id(cleanId)
                .customerId(cleanCustomerId)
                .vehicleId(cleanVehicleId)
                .startDate(starDate)
                .endDate(endDate)
                .status(ReservationStatus.PENDING)
                .modality(cleanModality)
                .build();

        reservations.add(reservation);
        return reservation;

    }

    // pending confirmed
    public Reservation confirm(String id){
        return changeStatus(id, ReservationStatus.PENDING,ReservationStatus.COMPLETED);
    }

    // pending confirmed
    public Reservation fail (String id){
        return changeStatus(id, ReservationStatus.PENDING, ReservationStatus.FAILED);
    }


    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

// For each for findReservation
    private Reservation findReservation(String id){
        for (Reservation reservation : reservations){
            if (reservation.getId().equals(id)) {
                return reservation;
            }
        }
        throw new IllegalArgumentException("Reservation not found :" + id);
    }

   //For each for existsReservation
    private boolean existsReservation(String id) {
        for (Reservation reservation : reservations) {
            if (reservation.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    //Create method for validated Reservation
    private Reservation changeStatus(String id, ReservationStatus expected, ReservationStatus next){
        Reservation current = findReservation(id);
        if (current.getStatus() != expected) {
            throw new IllegalStateException("Reservation " + id + " is " + current.getStatus()
                    + ", expected " + expected);
        }
        Reservation updated = new Reservation.Builder()
                .id(current.getId())
                .customerId(current.getCustomerId())
                .vehicleId(current.getVehicleId())
                .startDate(current.getStartDate())
                .endDate(current.getEndDate())
                .status(next)
                .modality(current.getModality())
                .build();

        //replace of list ( reemplazo la lista anterior)
        reservations.set(reservations.indexOf(current), updated);
        return updated;
    }

    //
    // Calcula el costo total: tarifa base por días + modalidad para calcular el costo de las modalidades premium executivo
    public double calculateCost(LocalDate startDate, LocalDate endDate, double dailyRate, String modality, double coverage, double assistance) {

        validateDates(startDate, endDate);

        long days = ChronoUnit.DAYS.between(startDate, endDate);
        double baseCost = new BaseCharge((int) days, dailyRate).getCost();

        RentalModalityFactory factory = ModalityFactorySelector.factoryFor(modality, coverage, assistance);
        return new RentalCostCalculatorServices(factory).calculate(baseCost);
    }

    //create method for validateDate
    public void validateDates(LocalDate startDate, LocalDate endDate){
        if(startDate == null || endDate == null){
            throw new IllegalArgumentException("Start date and end date are required.");
        }

        if (!startDate.isBefore(endDate)){
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }

    //Create method for requireText (pedir datos obligatoriamente)
    private String requireText(String value, String fieldName){
        if(value== null ||value.trim().isEmpty()){
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }
}
