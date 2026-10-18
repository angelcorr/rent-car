package edu.rentcar.service;

import edu.rentcar.factory.Assistance;
import edu.rentcar.factory.Coverage;
import edu.rentcar.factory.RentalModalityFactory;
import edu.rentcar.factory.TariffPolicy;

public class RentalCostCalculatorServices {
    private final TariffPolicy tariff;
    private final Coverage coverage;
    private final Assistance assistance;

    public RentalCostCalculatorServices(RentalModalityFactory factory){
        this.tariff= factory.createTariffPolicy();
        this.coverage = factory.createCoverage();
        this.assistance = factory.createAssistance();
    }
    public double calculate(double baseCost) {
        return tariff.value(baseCost) + coverage.getCost() + assistance.getCost();
    }

}
