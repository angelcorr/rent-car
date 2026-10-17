package edu.rentcar.factory;

import edu.rentcar.interfaces.Assistance;

public class EconomicAssistance implements Assistance {
    private final double cost;

    public EconomicAssistance(double cost) {
        this.cost = cost;
    }

    @Override
    public double getCost() {
        return cost;
    }
}


