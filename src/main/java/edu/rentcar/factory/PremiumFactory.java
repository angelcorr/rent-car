package edu.rentcar.factory;

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
        return new FixedCoverage(coverageCost);
    }

    @Override
    public Assistance createAssistance() {
        return new FixedAssistance(assistanceCost);
    }
}
