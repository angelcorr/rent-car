package edu.rentcar.service;

import edu.rentcar.factory.Assistance;
import edu.rentcar.factory.Coverage;
import edu.rentcar.factory.RentalModalityFactory;
import edu.rentcar.factory.TariffPolicy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ModalityFactoryTest {
  private double costFor(String modality, double baseCost) {
    RentalModalityFactory factory = ModalityFactorySelector.factoryFor(modality);
    return new RentalCostCalculatorServices(factory).calculate(baseCost);
  }

  @Test
  void economyAddsNoSurcharge() {
    // 300000 + 50000 + 50000
    assertEquals(400_000, costFor("Economy", 300_000), 0.001);
  }

  @Test
  void executiveAddsFifteenPercentSurcharge() {
    // 300000 * 1.15 + 75000 + 60000
    assertEquals(480_000, costFor("Executive", 300_000), 0.001);
  }

  @Test
  void premiumAddsTwentyFivePercentSurcharge() {
    // 300000 * 1.25 + 100000 + 80000
    assertEquals(555_000, costFor("Premium", 300_000), 0.001);
  }

  @Test
  void modalitiesHaveDifferentConfigurations() {
    double economy = costFor("Economy", 300_000);
    double executive = costFor("Executive", 300_000);
    double premium = costFor("Premium", 300_000);

    assertNotEquals(economy, executive, 0.001);
    assertNotEquals(executive, premium, 0.001);
  }

  @Test
  void modalityNameIsTrimmed() {
    assertEquals(400_000, costFor("  Economy ", 300_000), 0.001);
  }

  @Test
  void unknownModalityIsRejected() {
    assertThrows(IllegalArgumentException.class,
        () -> ModalityFactorySelector.factoryFor("Luxury"));
  }

  @Test
  void blankOrNullModalityIsRejected() {
    assertThrows(IllegalArgumentException.class, () -> ModalityFactorySelector.factoryFor(" "));
    assertThrows(IllegalArgumentException.class, () -> ModalityFactorySelector.factoryFor(null));
  }

  @Test
  void newModalityWorksWithTheCalculatorWithoutChangingIt() {
    // A new family defined only in this test: 10% discount, fixed coverage and assistance
    RentalModalityFactory discount = new RentalModalityFactory() {
      @Override
      public TariffPolicy createTariffPolicy() {
        return baseCost -> baseCost * 0.9;
      }

      @Override
      public Coverage createCoverage() {
        return () -> 1_000;
      }

      @Override
      public Assistance createAssistance() {
        return () -> 500;
      }
    };

    // 300000 * 0.9 + 1000 + 500
    assertEquals(271_500, new RentalCostCalculatorServices(discount).calculate(300_000), 0.001);
  }
}
