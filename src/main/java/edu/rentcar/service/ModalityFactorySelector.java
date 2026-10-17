package edu.rentcar.service;

import edu.rentcar.interfaces.RentalModalityFactory;
import factory.EconomicFactory;
import factory.ExecutiveFactory;
import factory.PremiumFactory;

public final class ModalityFactorySelector {
    private ModalityFactorySelector() {
    }

    public static RentalModalityFactory factoryFor(String modality, double coverage, double assistance) {
        if (modality == null || modality.isBlank()) {
            throw new IllegalArgumentException("Modality is required.");
        }

        return switch (modality.trim()) {
            case "Economy" -> new EconomicFactory(coverage, assistance);
            case "Executive" -> new ExecutiveFactory(coverage, assistance);
            case "Premium" -> new PremiumFactory(coverage, assistance);
            default -> throw new IllegalArgumentException("Unknown modality: " + modality);
        };
    }
}
