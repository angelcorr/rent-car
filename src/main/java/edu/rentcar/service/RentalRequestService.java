package edu.rentcar.service;

import edu.rentcar.model.RentalRequest;
import edu.rentcar.pricing.RentService;

import java.time.LocalDate;

public class RentalRequestService {

    public void validate(RentalRequest request) {
        requireText(request.getCustomerId(), "Customer id");
        requireText(request.getVehicleId(), "Vehicle id");
        requireText(request.getModality(), "Modality");
        validateDates(request.getStartDate(), request.getEndDate());

        // validate modality
        ModalityFactorySelector.factoryFor(request.getModality(), 0, 0);

    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date are required.");
        }
        if (!startDate.isBefore(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date.");
        }
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
    }
}
