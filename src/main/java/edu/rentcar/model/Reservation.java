package edu.rentcar.model;

import edu.rentcar.enums.ReservationStatus;

import java.time.LocalDate;

public class Reservation {
    private final String id;
    private final String customerId;
    private final String vehicleId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final ReservationStatus status;
    private final String modality;

    private Reservation(Builder builder){
        this.id = builder.id;
        this.customerId = builder.customerId;
        this.vehicleId = builder.vehicleId;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.status = builder.status;
        this.modality = builder.modality;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public String getModality() {
        return modality;
    }
    //Create Class Builder
    public static class Builder{
        private String id;
        private String customerId;
        private String vehicleId;
        private LocalDate startDate;
        private LocalDate endDate;
        private ReservationStatus status;
        private String modality;

    public Builder id (String id){
        this.id= id;
        return this;
    }

    public Builder customerId (String customerId){
        this.customerId= customerId;
        return this;
    }

    public Builder vehicleId (String vehicleId){
        this.vehicleId= vehicleId;
        return this;
    }

    public Builder startDate (LocalDate startDate){
        this.startDate= startDate;
        return this;
    }

    public Builder endDate (LocalDate endDate){
        this.endDate= endDate;
        return this;
    }

    public Builder status (ReservationStatus status){
        this.status= status;
        return this;
    }
        public Builder modality (String modality){
            this.modality= modality;
            return this;
        }

    public Reservation build(){
        return new Reservation(this);
  }
   }
}
