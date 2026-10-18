package edu.rentcar.controller;

import edu.rentcar.data.SampleData;
import edu.rentcar.model.Customer;
import edu.rentcar.model.Vehicle;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ReservationController {
    @FXML private ComboBox<String> customerCombo;
    @FXML private ComboBox<String> vehicleCombo;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> modalityCombo;
    @FXML private TextField paymentTokenField;
    @FXML private Label resultLabel;

    @FXML
    public void initialize() {
        customerCombo.getItems().setAll(SampleData.customers().stream().map(Customer::getId).toList());
        vehicleCombo.getItems().setAll(SampleData.vehicles().stream().map(Vehicle::getId).toList());
        modalityCombo.getItems().setAll("Economy", "Executive", "Premium");
    }

    @FXML
    private void onCreateReservation() {
      //angeles aqui conectamos el facade
    }
}