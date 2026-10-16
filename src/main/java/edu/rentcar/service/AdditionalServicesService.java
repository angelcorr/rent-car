package edu.rentcar.service;

import edu.rentcar.interfaces.RentService;

public class AdditionalServicesService implements RentService {
  private final RentService wrapped;
  private final String name;
  private final double price;

  public AdditionalServicesService(RentService wrapped, String name, double price) {
    this.wrapped = wrapped;
    this.name = name;
    this.price = price;
  }

  @Override
  public double getCost() {
    return wrapped.getCost() * price;
  }

  @Override
  public String getDescription() {
    return wrapped.getDescription() + "+" + name;
  }
}
