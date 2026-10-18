package edu.rentcar.factory;

public class FixedAssistance implements Assistance {
  private final double assistance;

  public FixedAssistance(double assistance) {
    this.assistance = assistance;
  }

  @Override
  public double getCost() {
    return assistance;
  }
}
