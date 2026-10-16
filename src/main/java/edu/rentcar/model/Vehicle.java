package edu.rentcar.model;

public class Vehicle {
    private final String id;
    private final String brand;
    private final String model;
    private final String category;
    private final int modelYear;
    private final double dailyRate;
    private final String status;

    public Vehicle(Builder builder) {
        this.id = builder.id;
        this.brand = builder.brand;
        this.model = builder.model;
        this.category = builder.category;
        this.modelYear = builder.modelYear;
        this.dailyRate = builder.dailyRate;
        this.status = builder.status;
    }

    public String getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getCategory() {
        return category;
    }

    public int getModelYear() {
        return modelYear;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public String getStatus() {
        return status;
    }

    public String getDisplayName() {
        return brand + " " + model;
    }

    public static class Builder {
        private String id;
        private String brand;
        private String model;
        private String category;
        private int modelYear;
        private double dailyRate;
        private String status;

        public Builder id (String id) {
            this.id = id;
            return this;
        }

        public Builder brand (String brand) {
            this.brand = brand;
            return this;
        }

        public Builder model (String model) {
            this.model = model;
            return this;
        }

        public Builder category (String category) {
            this.category = category;
            return this;
        }

        public Builder modelYear (int modelYear) {
            this.modelYear =modelYear;
            return this;
        }

        public Builder dailyRate (double dailyRate) {
            this.dailyRate = dailyRate;
            return this;
        }

        public Builder status (String status) {
            this.status = status;
            return this;
        }

        public Vehicle build() {
            return new Vehicle(this);
        }
    }
}
