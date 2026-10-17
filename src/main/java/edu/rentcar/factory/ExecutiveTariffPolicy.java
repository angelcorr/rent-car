package edu.rentcar.factory;

import edu.rentcar.interfaces.TariffPolicy;

public class ExecutiveTariffPolicy implements TariffPolicy {
    @Override
    public double value(double baseCost) {
        return baseCost*1.15; // recargo 15%
    }
}
