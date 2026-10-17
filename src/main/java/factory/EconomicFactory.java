package factory;

import edu.rentcar.interfaces.Assistance;
import edu.rentcar.interfaces.Coverage;
import edu.rentcar.interfaces.RentalModalityFactory;
import edu.rentcar.interfaces.TariffPolicy;

public class EconomicFactory implements RentalModalityFactory {
    private final double coverageCost;
    private final double assistanceCost;

    public EconomicFactory(double coverageCost, double assistanceCost) {
        this.coverageCost = coverageCost;
        this.assistanceCost = assistanceCost;
    }

    @Override
    public TariffPolicy createTariffPolicy() {
        return new EconomicTariffPolicy();
    }

    @Override
    public Assistance createAssistance() {
        return new EconomicAssistance(assistanceCost);
    }

    @Override
    public Coverage createCoverage() {
        return new EconomicCoverage(coverageCost);
    }
}