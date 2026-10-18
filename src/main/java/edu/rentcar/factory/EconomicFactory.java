package edu.rentcar.factory;

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
        return new FixedAssistance(assistanceCost);
    }

    @Override
    public Coverage createCoverage() {
        return new FixedCoverage(coverageCost);
    }
}
