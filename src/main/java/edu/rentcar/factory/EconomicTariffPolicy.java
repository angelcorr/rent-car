package edu.rentcar.factory;

public class EconomicTariffPolicy implements TariffPolicy {
    @Override
    public double value(double baseCost) {
        return baseCost;
    }
}