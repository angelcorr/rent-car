package factory;

import edu.rentcar.interfaces.Coverage;

public class EconomicCoverage implements Coverage {
    private final double cost;

    public EconomicCoverage(double cost) {
        this.cost = cost;
    }

    @Override
    public double getCost() {
        return cost;
    }
}