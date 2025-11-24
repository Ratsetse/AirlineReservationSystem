package com.airline.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import java.net.URL;
import java.util.ResourceBundle;

public class FlightController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialization code
    }

    @FXML
    private void handleFirst() {
        showAlert("Info", "First button clicked");
    }

    @FXML
    private void handlePrevious() {
        showAlert("Info", "Previous button clicked");
    }

    @FXML
    private void handleNext() {
        showAlert("Info", "Next button clicked");
    }

    @FXML
    private void handleLast() {
        showAlert("Info", "Last button clicked");
    }

    @FXML
    private void handleAdd() {
        showAlert("Info", "Add button clicked");
    }

    @FXML
    private void handleUpdate() {
        showAlert("Info", "Update button clicked");
    }

    @FXML
    private void handleDelete() {
        showAlert("Info", "Delete button clicked");
    }

    @FXML
    private void handleSave() {
        showAlert("Info", "Save button clicked");
    }

    @FXML
    private void handleShow() {
        showAlert("Info", "Show button clicked");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
