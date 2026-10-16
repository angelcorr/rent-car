package edu.rentcar.data;

import edu.rentcar.model.Customer;
import edu.rentcar.model.Vehicle;

import java.util.List;

public final class SampleData {
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
}
