package edu.rentcar.factory;

public class PremiumTariffPolicy implements TariffPolicy {
    @Override
    public double value(double baseCost) {
        return baseCost*1.25;  // para aplicar recargo del 25%
    }
}
