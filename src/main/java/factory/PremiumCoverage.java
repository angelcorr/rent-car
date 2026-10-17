package factory;

import edu.rentcar.interfaces.Coverage;

public class PremiumCoverage implements Coverage {
    private final double cost;

    public PremiumCoverage(double cost) {
        this.cost = cost;
    }

    @Override
    public double getCost() {
        return cost;
    }
}