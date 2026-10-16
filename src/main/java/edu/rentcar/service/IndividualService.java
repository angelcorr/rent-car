package edu.rentcar.service;

import edu.rentcar.interfaces.RentService;

public class IndividualService implements RentService {
  private final String name;
  private final double price;
  private final String description;

  public IndividualService(String name, double price, String description) {
    this.name = name;
    this.price = price;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  @Override
  public double getCost() {
    return price;
  }

  @Override
  public String getDescription() {
    return description;
  }
}
