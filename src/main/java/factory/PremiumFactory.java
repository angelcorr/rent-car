package factory;

import edu.rentcar.interfaces.Assistance;
import edu.rentcar.interfaces.Coverage;
import edu.rentcar.interfaces.RentalModalityFactory;
import edu.rentcar.interfaces.TariffPolicy;

public class PremiumFactory implements RentalModalityFactory {
    @Override
    public TariffPolicy createTariffPolicy() {
        return new PremiumTariffPolicy();
    }

    @Override
    public Coverage createCoverage() {
        return new PremiumCoverage();
    }

    @Override
    public Assistance createAssistance() {
        return new PremiumAssistance();
    }
}
