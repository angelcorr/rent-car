package edu.rentcar.repository;

import edu.rentcar.data.SampleData;
import edu.rentcar.model.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class VehicleRepository {
    private final List<Vehicle> vehicles = new ArrayList<>(SampleData.vehicles());
    private int nextId = 1005;

    public List<Vehicle> findAll() {
        return List.copyOf(vehicles);
    }

    public Vehicle save(String brand, String model, String category,
                        int modelYear, double dailyRate) {
        Vehicle vehicle = new Vehicle.Builder()
            .id("VEH-" + nextId++)
            .brand(brand)
            .model(model)
            .category(category)
            .modelYear(modelYear)
            .dailyRate(dailyRate)
            .status("Available")
            .build();
        vehicles.add(vehicle);
        return vehicle;
    }

    public int count() {
        return vehicles.size();
    }
}
