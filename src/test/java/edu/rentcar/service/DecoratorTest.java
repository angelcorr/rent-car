package edu.rentcar.service;

import edu.rentcar.pricing.FixedCost;
import edu.rentcar.pricing.IndividualService;
import edu.rentcar.pricing.RentService;
import edu.rentcar.pricing.ServicePackage;
import edu.rentcar.pricing.decorator.ExtraServiceDecorator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DecoratorTest {
  private static final double DELTA = 0.001;

  private RentService base() {
    return new FixedCost(300_000, "Base rental");
  }

  private IndividualService gps() {
    return new IndividualService("GPS", 50_000, "GPS rental");
  }

  private IndividualService extraDriver() {
    return new IndividualService("Extra driver", 30_000, "Extra driver rental");
  }

  @Test
  void oneExtraAddsItsCostAndDescription() {
    RentService result = new ExtraServiceDecorator(base(), gps());

    assertEquals(350_000, result.getCost(), DELTA);
    assertEquals("Base rental. GPS rental", result.getDescription());
  }

  @Test
  void twoExtrasCombineCostAndDescription() {
    RentService result = new ExtraServiceDecorator(
        new ExtraServiceDecorator(base(), gps()),
        extraDriver());

    assertEquals(380_000, result.getCost(), DELTA);
    assertEquals("Base rental. GPS rental. Extra driver rental", result.getDescription());
  }

  @Test
  void extrasAreAddedNotMultiplied() {
    RentService result = base();
    result = new ExtraServiceDecorator(result, gps());
    result = new ExtraServiceDecorator(result, gps());
    result = new ExtraServiceDecorator(result, gps());

    assertEquals(450_000, result.getCost(), DELTA);
  }

  @Test
  void newServiceNeedsNoNewClass() {
    IndividualService roofRack = new IndividualService("Roof rack", 12_000, "Roof rack rental");

    RentService result = new ExtraServiceDecorator(base(), roofRack);

    assertEquals(312_000, result.getCost(), DELTA);
    assertEquals("Base rental. Roof rack rental", result.getDescription());
  }

  @Test
  void aPackageCanBeUsedAsAnExtra() {
    ServicePackage comfort = new ServicePackage("Comfort", "GPS and driver");
    comfort.add(gps());
    comfort.add(extraDriver());

    RentService result = new ExtraServiceDecorator(base(), comfort);

    assertEquals(380_000, result.getCost(), DELTA);
    assertEquals("Base rental. Comfort (GPS and driver): GPS rental, Extra driver rental",
        result.getDescription());
  }

  @Test
  void nullWrappedServiceIsRejected() {
    assertThrows(NullPointerException.class, () -> new ExtraServiceDecorator(null, gps()));
  }

  @Test
  void nullExtraServiceIsRejected() {
    assertThrows(NullPointerException.class, () -> new ExtraServiceDecorator(base(), null));
  }
}
