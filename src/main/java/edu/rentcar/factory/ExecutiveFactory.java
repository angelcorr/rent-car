package edu.rentcar.factory;

public class ExecutiveFactory implements RentalModalityFactory {
    private final double coverageCost;
    private final double assistanceCost;

    public ExecutiveFactory(double coverageCost, double assistanceCost) {
        this.coverageCost = coverageCost;
        this.assistanceCost = assistanceCost;
    }

    @Override
    public Coverage createCoverage() {
        return new FixedCoverage(coverageCost);
    }

    @Override
    public Assistance createAssistance() {
        return new FixedAssistance(assistanceCost);
    }

    @Override
    public TariffPolicy createTariffPolicy() {
        return new ExecutiveTariffPolicy();
    }
}