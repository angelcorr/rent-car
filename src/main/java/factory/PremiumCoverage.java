package factory;

import edu.rentcar.interfaces.Coverage;

public class PremiumCoverage implements Coverage {
    @Override
    public double getCost() {
        return 0;
    }
}
