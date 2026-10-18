package edu.rentcar.model;
import edu.rentcar.pricing.RentService;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class RentalRequest {
    private final String customerId;
    private final String vehicleId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String modality;
    private final String paymentToken;
    private final List<RentService> services;

    private RentalRequest(Builder builder) {
        this.customerId = builder.customerId;
        this.vehicleId = builder.vehicleId;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.modality = builder.modality;
        this.paymentToken = builder.paymentToken;
        this.services = List.copyOf(builder.services);
    }

    // getters
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

    public String getModality() {
        return modality;
    }

    public String getPaymentToken() {
        return paymentToken;
    }

    public List<RentService> getServices() {
        return services;
    }

    // create Class Builder
    public static class Builder {
        private String customerId;
        private String vehicleId;
        private LocalDate startDate;
        private LocalDate endDate;
        private String modality;
        private String paymentToken;
        private final List<RentService> services = new ArrayList<>();


        public Builder customerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder vehicleId(String vehicleId) {
            this.vehicleId = vehicleId;
            return this;
        }

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder modality(String modality) {
            this.modality = modality;
            return this;
        }

        public Builder paymentToken(String paymentToken) {
            this.paymentToken = paymentToken;
            return this;
        }

        public Builder addService(RentService service) {
            this.services.add(service);
            return this;
        }

        public RentalRequest build() {
            requireText(customerId, "Customer id");
            requireText(vehicleId, "Vehicle id");
            requireText(modality, "Modality");
            requireText(paymentToken, "Payment token");
            if (startDate == null || endDate == null) {
                throw new IllegalArgumentException("Start date and end date are required.");
            }
            if (!startDate.isBefore(endDate)) {
                throw new IllegalArgumentException("Start date must be before end date.");
            }
            return new RentalRequest(this);
        }

        private void requireText(String value, String fieldName) {
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException(fieldName + " is required.");
            }
        }
    }
}
