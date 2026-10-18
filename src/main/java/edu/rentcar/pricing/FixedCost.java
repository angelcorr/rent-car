package edu.rentcar.pricing;

public class FixedCost implements RentService {
  private final double cost;
  private final String description;

  public FixedCost(double cost, String description) {
    this.cost = cost;
    this.description = description;
  }

  @Override
  public double getCost() {
    return cost;
  }

  @Override
  public String getDescription() {
    return description;
  }
}
