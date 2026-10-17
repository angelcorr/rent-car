package factory;

import edu.rentcar.interfaces.Assistance;
import edu.rentcar.interfaces.Coverage;
import edu.rentcar.interfaces.RentalModalityFactory;
import edu.rentcar.interfaces.TariffPolicy;

public class EconomicFactory implements RentalModalityFactory {
    @Override
    public TariffPolicy createTariffPolicy() {
        return new  EconomicTariffPolicy();
    }

    @Override
    public Assistance createAssistance() {
        return new EconomicAssistance();
    }

    @Override
    public Coverage createCoverage() {
        return new EconomicCoverage();
    }
}
