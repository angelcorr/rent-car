package edu.rentcar.model;

import edu.rentcar.pricing.RentService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServicePackage implements RentService {
  private final String name;
  private final String description;
  private final List<RentService> servicesList = new ArrayList<>();

  public ServicePackage(String name, String description, RentService... rentService) {
    this.name = name;
    this.description = description;
    servicesList.addAll(Arrays.asList(rentService));
  }

  public String getName () {
    return name;
  }

  @Override
  public double getCost() {
    return servicesList.stream()
        .mapToDouble(RentService::getCost)
        .sum();
  }

  @Override
  public String getDescription() {
    return description;
  }
}
