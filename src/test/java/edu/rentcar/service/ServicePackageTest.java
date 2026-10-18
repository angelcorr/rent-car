package edu.rentcar.service;

import edu.rentcar.pricing.IndividualService;
import edu.rentcar.pricing.RentService;
import edu.rentcar.pricing.ServicePackage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServicePackageTest {
  private static final double DELTA = 0.001;

  private IndividualService gps() {
    return new IndividualService("GPS", 20_000, "GPS rental");
  }

  private IndividualService childSeat() {
    return new IndividualService("Child seat", 15_000, "Child seat rental");
  }

  private IndividualService extraDriver() {
    return new IndividualService("Extra driver", 30_000, "Extra driver rental");
  }

  @Test
  void simplePackageSumsTheCostOfItsServices() {
    ServicePackage comfort = new ServicePackage("Comfort", "GPS and seat");
    comfort.add(gps());
    comfort.add(childSeat());

    assertEquals(35_000, comfort.getCost(), DELTA);
  }

  @Test
  void simplePackageDescriptionListsItsComponents() {
    ServicePackage comfort = new ServicePackage("Comfort", "GPS and seat");
    comfort.add(gps());
    comfort.add(childSeat());

    assertEquals("Comfort (GPS and seat): GPS rental, Child seat rental",
        comfort.getDescription());
  }

  @Test
  void nestedPackageSumsCostsRecursively() {
    ServicePackage comfort = new ServicePackage("Comfort", "GPS and seat");
    comfort.add(gps());
    comfort.add(childSeat());

    ServicePackage travel = new ServicePackage("Travel", "Full trip");
    travel.add(comfort);
    travel.add(extraDriver());

    assertEquals(65_000, travel.getCost(), DELTA);
  }

  @Test
  void nestedPackageDescriptionIncludesInnerPackage() {
    ServicePackage comfort = new ServicePackage("Comfort", "GPS and seat");
    comfort.add(gps());
    comfort.add(childSeat());

    ServicePackage travel = new ServicePackage("Travel", "Full trip");
    travel.add(comfort);
    travel.add(extraDriver());

    assertEquals(
        "Travel (Full trip): Comfort (GPS and seat): GPS rental, Child seat rental, Extra driver rental",
        travel.getDescription());
  }

  @Test
  void twoLevelNestingKeepsTheCorrectTotal() {
    ServicePackage inner = new ServicePackage("Inner", "Level 1");
    inner.add(gps());

    ServicePackage middle = new ServicePackage("Middle", "Level 2");
    middle.add(inner);
    middle.add(childSeat());

    ServicePackage outer = new ServicePackage("Outer", "Level 3");
    outer.add(middle);
    outer.add(extraDriver());

    assertEquals(65_000, outer.getCost(), DELTA);
  }

  @Test
  void emptyPackageCostsZero() {
    ServicePackage empty = new ServicePackage("Empty", "Nothing inside");

    assertEquals(0, empty.getCost(), DELTA);
    assertEquals("Empty (Nothing inside): ", empty.getDescription());
  }

  @Test
  void nullDescriptionIsTreatedAsEmpty() {
    ServicePackage pkg = new ServicePackage("Basic", null);
    pkg.add(gps());

    assertEquals("Basic (): GPS rental", pkg.getDescription());
  }

  @Test
  void packageAndIndividualServiceAreUsedThroughTheSameAbstraction() {
    ServicePackage pkg = new ServicePackage("Comfort", "GPS and seat");
    pkg.add(gps());
    pkg.add(childSeat());

    RentService single = gps();
    RentService composite = pkg;

    assertEquals(20_000, single.getCost(), DELTA);
    assertEquals(35_000, composite.getCost(), DELTA);
  }

  @Test
  void blankNameIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> new ServicePackage("   ", "desc"));
  }

  @Test
  void nullNameIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> new ServicePackage(null, "desc"));
  }
}
