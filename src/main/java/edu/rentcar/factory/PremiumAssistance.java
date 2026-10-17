package edu.rentcar.factory;

import edu.rentcar.interfaces.Assistance;

public class PremiumAssistance implements Assistance {
    private final double cost;

    public PremiumAssistance(double cost) {
        this.cost = cost;
    }

    @Override
    public double getCost() {
        return cost;
    }
}