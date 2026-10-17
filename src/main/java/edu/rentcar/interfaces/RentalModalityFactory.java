package edu.rentcar.interfaces;

    public interface RentalModalityFactory {
        TariffPolicy createTariffPolicy();
        Coverage createCoverage();
        Assistance createAssistance();
    }
