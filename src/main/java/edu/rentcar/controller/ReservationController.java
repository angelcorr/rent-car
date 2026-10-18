package edu.rentcar.controller;

import edu.rentcar.app.ApplicationServices;
import edu.rentcar.data.SampleData;
import edu.rentcar.facade.RentalFacade;
import edu.rentcar.facade.RentalResult;
import edu.rentcar.model.Customer;
import edu.rentcar.model.RentalRequest;
import edu.rentcar.model.Reservation;
import edu.rentcar.model.Vehicle;
import edu.rentcar.pricing.IndividualService;
import edu.rentcar.pricing.RentService;
import edu.rentcar.pricing.ServicePackage;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

public class ReservationController {
    private final RentalFacade facade = ApplicationServices.rentalFacade();

    @FXML private ComboBox<Customer> customerCombo;
    @FXML private ComboBox<Vehicle> vehicleCombo;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> modalityCombo;
    @FXML private ComboBox<String> tokenCombo;
    @FXML private ListView<RentService> servicesList;
    @FXML private Label resultLabel;

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, String> resIdColumn;
    @FXML private TableColumn<Reservation, String> resCustomerColumn;
    @FXML private TableColumn<Reservation, String> resVehicleColumn;
    @FXML private TableColumn<Reservation, String> resModalityColumn;
    @FXML private TableColumn<Reservation, Object> resStatusColumn;
    @FXML private TableColumn<Reservation, Number> resCostColumn;

    @FXML
    public void initialize() {
        customerCombo.getItems().setAll(SampleData.customers());
        customerCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Customer customer) {
                if (customer == null) {
                    return "";
                }
                return customer.getFullName()
                    + (customer.isAuthorized() ? "" : " (not authorized)");
            }

            @Override
            public Customer fromString(String text) {
                return null;
            }
        });

        vehicleCombo.getItems().setAll(ApplicationServices.vehicleService().getAllVehicles());
        vehicleCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Vehicle vehicle) {
                if (vehicle == null) {
                    return "";
                }
                return vehicle.getDisplayName() + " (" + vehicle.getCategory() + ")";
            }

            @Override
            public Vehicle fromString(String text) {
                return null;
            }
        });
        modalityCombo.getItems().setAll("Economy", "Executive", "Premium");
        modalityCombo.getSelectionModel().selectFirst();

        tokenCombo.getItems().setAll("tok_approved", "tok_declined", "tok_api_error");
        tokenCombo.getSelectionModel().selectFirst();

        // Individual services and packages in one list (Composite: used uniformly)
        servicesList.getItems().setAll(SampleData.allOfferedServices());
        servicesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        servicesList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(RentService service, boolean empty) {
                super.updateItem(service, empty);
                setText(empty || service == null
                    ? null
                    : nameOf(service) + "  (+" + service.getCost() + ")");
            }
        });

        resIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        resCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        resVehicleColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        resModalityColumn.setCellValueFactory(new PropertyValueFactory<>("modality"));
        resStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        resCostColumn.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        refreshReservations();
    }

    @FXML
    private void onCreateReservation() {
        Customer customer = customerCombo.getValue();
        Vehicle vehicle = vehicleCombo.getValue();

        if (customer == null || vehicle == null) {
            showResult(false, "Select a customer and a vehicle.");
            return;
        }

        try {
            RentalRequest.Builder builder = new RentalRequest.Builder()
                .customerId(customer.getId())
                .vehicleId(vehicle.getId())
                .startDate(startDatePicker.getValue())
                .endDate(endDatePicker.getValue())
                .modality(modalityCombo.getValue())
                .paymentToken(tokenCombo.getValue());

            for (RentService service : servicesList.getSelectionModel().getSelectedItems()) {
                builder.addService(service);
            }

            RentalResult result = facade.rent(builder.build());
            showResult(result.isSuccess(), buildMessage(result));
            refreshReservations();

            if (result.isSuccess()) {
                clearFields();
            }
        } catch (IllegalArgumentException exception) {
            showResult(false, exception.getMessage());
        }
    }

    @FXML
    private void onClear() {
        clearFields();
        resultLabel.setText("");
    }

    private String buildMessage(RentalResult result) {
        Reservation reservation = result.getReservation();
        if (reservation == null) {
            return result.getMessage();
        }
        return result.getMessage() + "\nReservation " + reservation.getId()
            + " | Status: " + reservation.getStatus()
            + " | Total: " + reservation.getTotalCost()
            + "\n" + reservation.getDescription();
    }

    private void showResult(boolean success, String message) {
        resultLabel.setStyle(success ? "-fx-text-fill: #1b7f3b;" : "-fx-text-fill: #b00020;");
        resultLabel.setText(message);
    }

    private void refreshReservations() {
        reservationTable.getItems().setAll(facade.getReservations());
    }

    private String nameOf(RentService service) {
        if (service instanceof IndividualService individual) {
            return individual.getName();
        }
        if (service instanceof ServicePackage servicePackage) {
            return servicePackage.getName() + " (package)";
        }
        return service.getDescription();
    }

    private void clearFields() {
        customerCombo.getSelectionModel().clearSelection();
        vehicleCombo.getSelectionModel().clearSelection();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        modalityCombo.getSelectionModel().selectFirst();
        tokenCombo.getSelectionModel().selectFirst();
        servicesList.getSelectionModel().clearSelection();
    }
}
