package edu.rentcar.service;

import edu.rentcar.factory.RentalModalityFactory;
import edu.rentcar.factory.EconomicFactory;
import edu.rentcar.factory.ExecutiveFactory;
import edu.rentcar.factory.PremiumFactory;

public final class ModalityFactorySelector {
    private ModalityFactorySelector() {
    }

    public static RentalModalityFactory factoryFor(String modality) {
        if (modality == null || modality.isBlank()) {
            throw new IllegalArgumentException("Modality is required.");
        }

        return switch (modality.trim()) {
            case "Economy" -> new EconomicFactory(50000, 50000);
            case "Executive" -> new ExecutiveFactory(75000, 60000);
            case "Premium" -> new PremiumFactory(100000, 80000);
            default -> throw new IllegalArgumentException("Unknown modality: " + modality);
        };
    }
}
