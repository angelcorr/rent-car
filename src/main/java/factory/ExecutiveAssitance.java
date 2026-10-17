package factory;

import edu.rentcar.interfaces.Assistance;

public class ExecutiveAssitance implements Assistance {
    private final double cost;

    public ExecutiveAssitance(double cost) {
        this.cost = cost;
    }

    @Override
    public double getCost() {
        return cost;
    }
}
