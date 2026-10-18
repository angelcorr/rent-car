package edu.rentcar.pricing;

import java.util.ArrayList;
import java.util.List;

public class ServicePackage implements RentService {
  private final String name;
  private final String description;
  private final List<RentService> servicesList = new ArrayList<>();

  public ServicePackage(String name, String description) {
    if(name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Package name is required");
    }

    this.name = name.trim();
    this.description = description == null ? "" : description.trim();
  }

  public void add(RentService rentService) {
    servicesList.add(rentService);
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
    StringBuilder result = new StringBuilder(name + " (" + description + "): ");
    for (int i = 0; i < servicesList.size(); i++) {
      result.append(servicesList.get(i).getDescription());

      if (i < servicesList.size() - 1) {
        result.append(", ");
      }
    }
    return result.toString();
  }
}
