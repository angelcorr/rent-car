package edu.rentcar.pricing.decorator;

import edu.rentcar.pricing.RentService;

import java.util.Objects;

public class ExtraServiceDecorator extends ServiceDecorator {
  private final RentService extra;

  protected ExtraServiceDecorator(RentService wrapped, RentService extra) {
    super(wrapped);
    this.extra = Objects.requireNonNull(extra, "Extra service is required.");
  }

  @Override
  protected double getExtraCost() {
    return extra.getCost();
  }

  @Override
  protected String getExtraDescription() {
    return extra.getDescription();
  }
}
