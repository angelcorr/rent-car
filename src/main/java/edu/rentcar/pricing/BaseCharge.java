package edu.rentcar.pricing;

public class BaseCharge implements RentService {
  private final int days;
  private final double dailyCharge;

  public BaseCharge(int days, double dailyCharge) {
    if (days <= 0) {
      throw new IllegalArgumentException("Days must be greater than zero");
    }
    if (!Double.isFinite(dailyCharge) || dailyCharge <= 0) {
      throw new IllegalArgumentException("Daily charge must be greater than zero");
    }

    this.days = days;
    this.dailyCharge = dailyCharge;
  }

  @Override
  public double getCost() {
    return dailyCharge * days;
  }

  @Override
  public String getDescription() {
    return "Base rental: " + days + " day(s) at " + dailyCharge + " per day";
  }
}
