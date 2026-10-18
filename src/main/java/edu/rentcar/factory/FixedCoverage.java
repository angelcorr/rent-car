package edu.rentcar.factory;

public class FixedCoverage implements Coverage {
  private final double cost;

  public FixedCoverage(double cost) {
    this.cost = cost;
  }

  @Override
  public double getCost() {
    return cost;
  }
}
