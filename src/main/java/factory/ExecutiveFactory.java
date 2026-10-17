package factory;

import edu.rentcar.interfaces.Assistance;
import edu.rentcar.interfaces.Coverage;
import edu.rentcar.interfaces.RentalModalityFactory;
import edu.rentcar.interfaces.TariffPolicy;

public class ExecutiveFactory implements RentalModalityFactory {
    @Override
    public Coverage createCoverage() {
        return new ExecutiveCoverage();
    }

    @Override
    public Assistance createAssistance() {
        return new ExecutiveAssitance();
    }

    @Override
    public TariffPolicy createTariffPolicy() {
        return new ExecutiveTariffPolicy();
    }
}
