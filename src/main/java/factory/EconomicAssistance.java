package factory;

import edu.rentcar.interfaces.Assistance;

public class EconomicAssistance implements Assistance {
    @Override
    public double getCost() {
        return 0;
    }
}
