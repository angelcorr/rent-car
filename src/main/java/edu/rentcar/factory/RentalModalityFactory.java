package edu.rentcar.factory;

    public interface RentalModalityFactory {
        TariffPolicy createTariffPolicy();
        Coverage createCoverage();
        Assistance createAssistance();
    }
