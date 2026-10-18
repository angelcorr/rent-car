package edu.rentcar.service;

import edu.rentcar.pricing.BaseCharge;
import edu.rentcar.pricing.IndividualService;
import edu.rentcar.pricing.RentService;
import edu.rentcar.pricing.decorator.ExtraServicesApplier;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExtraServicesApplierTest {
  @Test
  void appliesAllExtrasToTheBase() {
    RentService base = new BaseCharge(2, 100_000);
    List<RentService> extras = List.of(
        new IndividualService("GPS", 15_000, "GPS"),
        new IndividualService("Child seat", 20_000, "Child seat"));

    RentService result = ExtraServicesApplier.applyTo(base, extras);

    assertEquals(235_000, result.getCost(), 0.001);
    assertTrue(result.getDescription().contains("GPS"));
    assertTrue(result.getDescription().contains("Child seat"));
  }

  @Test
  void noExtrasReturnsTheBaseUnchanged() {
    RentService base = new BaseCharge(2, 100_000);

    RentService result = ExtraServicesApplier.applyTo(base, List.of());

    assertEquals(200_000, result.getCost(), 0.001);
  }

}
