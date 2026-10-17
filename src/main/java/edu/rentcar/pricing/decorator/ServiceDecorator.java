package edu.rentcar.pricing.decorator;

import edu.rentcar.pricing.RentService;

import java.util.Objects;

public abstract class ServiceDecorator implements RentService {
  protected final RentService wrapped;

  protected ServiceDecorator(RentService wrapped) {
    this.wrapped = Objects.requireNonNull(wrapped, "Wrapped service is required.");
  }

  @Override
  public double getCost() {
    return wrapped.getCost() + getExtraCost();
  }

  @Override
  public String getDescription() {
    return wrapped.getDescription() + ". " + getExtraDescription();
  }

  protected abstract double getExtraCost();
  protected abstract String getExtraDescription();
}
