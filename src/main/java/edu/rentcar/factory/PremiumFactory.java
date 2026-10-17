package edu.rentcar.factory;

import edu.rentcar.interfaces.Assistance;
import edu.rentcar.interfaces.Coverage;
import edu.rentcar.interfaces.RentalModalityFactory;
import edu.rentcar.interfaces.TariffPolicy;

public class PremiumFactory implements RentalModalityFactory {
    private final double coverageCost;
    private final double assistanceCost;

    public PremiumFactory(double coverageCost, double assistanceCost) {
        this.coverageCost = coverageCost;
        this.assistanceCost = assistanceCost;
    }

    @Override
    public TariffPolicy createTariffPolicy() {
        return new PremiumTariffPolicy();
    }

    @Override
    public Coverage createCoverage() {
        return new PremiumCoverage(coverageCost);
    }

    @Override
    public Assistance createAssistance() {
        return new PremiumAssistance(assistanceCost);
    }
}
