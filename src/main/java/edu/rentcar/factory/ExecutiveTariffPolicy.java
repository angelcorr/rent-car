package edu.rentcar.factory;

public class ExecutiveTariffPolicy implements TariffPolicy {
    @Override
    public double value(double baseCost) {
        return baseCost*1.15; // recargo 15%
    }
}
