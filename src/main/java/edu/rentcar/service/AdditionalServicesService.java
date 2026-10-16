package edu.rentcar.service;

import edu.rentcar.interfaces.RentService;

public class AdditionalServicesService implements RentService {
  private final RentService wrapped;

  public AdditionalServicesService(RentService wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public double getCost() {
    return wrapped.getCost();
  }

  @Override
  public String getDescription() {
    return wrapped.getDescription();
  }
}
