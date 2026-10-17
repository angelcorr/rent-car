package factory;

import edu.rentcar.interfaces.Coverage;

public class EconomicCoverage implements Coverage {

    @Override
    public double getCost() {
        return 0;
    }
}
