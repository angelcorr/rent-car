package edu.rentcar.factory;

import edu.rentcar.interfaces.Coverage;

public class ExecutiveCoverage implements Coverage {
    private final double cost;

    public ExecutiveCoverage(double cost) {
        this.cost = cost;
    }

    @Override
    public double getCost() {
        return cost;
    }
}