package edu.rentcar.data;

import edu.rentcar.model.Customer;
import edu.rentcar.model.Vehicle;
import edu.rentcar.pricing.IndividualService;
import edu.rentcar.pricing.RentService;
import edu.rentcar.pricing.ServicePackage;

import java.util.ArrayList;
import java.util.List;

public final class SampleData {
    private static final IndividualService GPS =
        new IndividualService("GPS", 15_000, "GPS");
    private static final IndividualService CHILD_SEAT =
        new IndividualService("Child seat", 20_000, "Child seat");
    private static final IndividualService ADDITIONAL_DRIVER =
        new IndividualService("Additional driver", 25_000, "Additional driver");
    private static final IndividualService WIFI =
        new IndividualService("WiFi", 10_000, "WiFi");

    private SampleData() {
    }

    public static List<Customer> customers() {
        return List.of(
                new Customer("CUS-1001", "Sofia Martinez", "sofia.martinez@example.com", true),
                new Customer("CUS-1002", "Daniel Rojas", "daniel.rojas@example.com", true),
                new Customer("CUS-1003", "Valentina Torres", "valentina.torres@example.com", false)
        );
    }

    public static List<Vehicle> vehicles() {
        return List.of(
                new Vehicle.Builder()
                    .id("VEH-1001")
                    .brand("Toyota")
                    .model("Corolla")
                    .category("Economy")
                    .modelYear(2024)
                    .dailyRate(150000)
                    .status("Available")
                    .build(),
                new Vehicle.Builder()
                    .id("VEH-1002")
                    .brand("Mazda")
                    .model("CX-5")
                    .category("Executive")
                    .modelYear(2025)
                    .dailyRate(230000)
                    .status("Available")
                    .build(),
                new Vehicle.Builder()
                    .id("VEH-1003")
                    .brand("BMW")
                    .model("320i")
                    .category("Premium")
                    .modelYear(2024)
                    .dailyRate(380000)
                    .status("Available")
                    .build(),
                new Vehicle.Builder()
                    .id("VEH-1004")
                    .brand("Renault")
                    .model("Duster")
                    .category("Executive")
                    .modelYear(2023)
                    .dailyRate(210000)
                    .status("Available")
                    .build()
        );
    }

    public static List<IndividualService> additionalServices() {
        return List.of(GPS, CHILD_SEAT, ADDITIONAL_DRIVER, WIFI);
    }

    public static List<ServicePackage> servicePackages() {
        ServicePackage family = new ServicePackage("Family pack", "For trips with children");
        family.add(CHILD_SEAT);
        family.add(ADDITIONAL_DRIVER);

        ServicePackage travel = new ServicePackage("Travel pack", "For long trips");
        travel.add(GPS);
        travel.add(WIFI);

        ServicePackage complete = new ServicePackage("Complete pack", "Everything included");
        complete.add(family);
        complete.add(travel);

        List<ServicePackage> packages = new ArrayList<>();
        packages.add(family);
        packages.add(travel);
        packages.add(complete);
        return packages;
    }

    public static List<RentService> allOfferedServices() {
        List<RentService> offered = new ArrayList<>();
        offered.addAll(additionalServices());
        offered.addAll(servicePackages());
        return offered;
    }
}
