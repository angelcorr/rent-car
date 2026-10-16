package edu.rentcar.service;

import edu.rentcar.interfaces.RentService;

public class BaseCharge implements RentService {
  private int days;
  private double dailyCharge;

  public BaseCharge(int days, double dailyCharge) {
    this.days = days;
    this.dailyCharge = dailyCharge;
  }

  @Override
  public double getCost() {
    return dailyCharge * days;
  }

  @Override
  public String getDescription() {
    return days + " days with a cost of " + dailyCharge;
  }
}
