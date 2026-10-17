package factory;

import edu.rentcar.interfaces.Assistance;

public class PremiumAssistance implements Assistance {
    @Override
    public double getCost() {
        return 0;
    }
}
