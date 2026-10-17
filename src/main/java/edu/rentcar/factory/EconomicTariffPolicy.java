package edu.rentcar.factory;

import edu.rentcar.interfaces.TariffPolicy;

public class EconomicTariffPolicy implements TariffPolicy {
    @Override
    public double value(double baseCost) {
        return baseCost;
    }
}