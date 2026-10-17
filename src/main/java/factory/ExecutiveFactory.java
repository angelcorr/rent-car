package factory;

import edu.rentcar.interfaces.Assistance;
import edu.rentcar.interfaces.Coverage;
import edu.rentcar.interfaces.RentalModalityFactory;
import edu.rentcar.interfaces.TariffPolicy;

public class ExecutiveFactory implements RentalModalityFactory {
    private final double coverageCost;
    private final double assistanceCost;

    public ExecutiveFactory(double coverageCost, double assistanceCost) {
        this.coverageCost = coverageCost;
        this.assistanceCost = assistanceCost;
    }

    @Override
    public Coverage createCoverage() {
        return new ExecutiveCoverage(coverageCost);
    }

    @Override
    public Assistance createAssistance() {
        return new ExecutiveAssitance(assistanceCost);
    }

    @Override
    public TariffPolicy createTariffPolicy() {
        return new ExecutiveTariffPolicy();
    }
}