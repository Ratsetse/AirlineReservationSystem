package com.airline.Controller;

import com.airline.database.DatabaseConnection;
import com.airline.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Member1Controller implements Initializable {

    @FXML private VBox mainContainer;
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> colCode;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, String> colGender;
    @FXML private TextField searchField;
    @FXML private Label totalCustomersLabel;
    @FXML private Label todayRegistrationsLabel;
    @FXML private Label activeBookingsLabel;
    @FXML private Label satisfactionLabel;

    private ObservableList<Customer> customerData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadCustomerStatistics();
        loadCustomers();
    }

    private void setupTableColumns() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("custCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("custName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));

        customerTable.setItems(customerData);
    }

    private void loadCustomerStatistics() {
        try {
            // Total customers
            int totalCustomers = getTotalCustomers();
            totalCustomersLabel.setText(String.valueOf(totalCustomers));

            // Today's registrations
            int todayRegistrations = getTodayRegistrations();
            todayRegistrationsLabel.setText(String.valueOf(todayRegistrations));

            // Active bookings
            int activeBookings = getActiveBookings();
            activeBookingsLabel.setText(String.valueOf(activeBookings));

            // Satisfaction rate (mock data for demo)
            satisfactionLabel.setText("94%");

        } catch (SQLException e) {
            showAlert("Error", "Failed to load statistics: " + e.getMessage(), "ERROR");
        }
    }

    private void loadCustomers() {
        String sql = "SELECT * FROM customer_details ORDER BY cust_code DESC LIMIT 20";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            customerData.clear();
            while (rs.next()) {
                // Use the simplified constructor that matches the parameters
                Customer customer = new Customer(
                        rs.getInt("cust_code"),
                        rs.getString("cust_name"),
                        rs.getString("tel_no"),
                        rs.getString("gender")
                );
                customerData.add(customer);
            }
            customerTable.refresh();

        } catch (SQLException e) {
            showAlert("Error", "Failed to load customers: " + e.getMessage(), "ERROR");
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            loadCustomers();
        } else {
            ObservableList<Customer> filtered = customerData.filtered(
                    c -> c.getCustName().toLowerCase().contains(searchText) ||
                            c.getTelephone().contains(searchText) ||
                            String.valueOf(c.getCustCode()).contains(searchText)
            );
            customerTable.setItems(filtered);
        }
    }

    @FXML
    private void handleAddCustomer() {
        loadWindow("/com/airline/view/customer-form.fxml", "Add New Customer");
    }

    @FXML
    private void handleViewAllCustomers() {
        loadCustomers();
        showAlert("Info", "Displaying all customer records", "INFO");
    }

    @FXML
    private void handleGenerateReport() {
        try {
            int totalCustomers = getTotalCustomers();
            int todayRegistrations = getTodayRegistrations();

            String report = String.format(
                    "Customer Management Report\n\n" +
                            "Total Customers: %d\n" +
                            "Today's Registrations: %d\n" +
                            "Active Bookings: %s\n" +
                            "Customer Satisfaction: 94%%\n\n" +
                            "Top Customer Segments:\n" +
                            "• Business Travelers: 45%%\n" +
                            "• Leisure Travelers: 35%%\n" +
                            "• Family Travelers: 20%%",
                    totalCustomers, todayRegistrations, activeBookingsLabel.getText()
            );

            showAlert("Customer Report", report, "INFO");

        } catch (SQLException e) {
            showAlert("Error", "Failed to generate report: " + e.getMessage(), "ERROR");
        }
    }

    @FXML
    private void handleRefresh() {
        loadCustomerStatistics();
        loadCustomers();
        showAlert("Refreshed", "Customer data updated successfully!", "INFO");
    }

    // Database methods
    private int getTotalCustomers() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM customer_details";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("total") : 0;
        }
    }

    private int getTodayRegistrations() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM customer_details WHERE t_date = CURRENT_DATE";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("total") : 0;
        }
    }

    private int getActiveBookings() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM reservations WHERE status = 'CONFIRMED'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("total") : 0;
        }
    }

    @FXML
    private void goBackToMain() {
        try {
            Stage currentStage = (Stage) mainContainer.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/airline/view/main-dashboard.fxml"));
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Airline Reservation System");
        } catch (Exception e) {
            showAlert("Error", "Cannot load main dashboard: " + e.getMessage(), "ERROR");
        }
    }

    private void loadWindow(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Cannot load: " + e.getMessage(), "ERROR");
        }
    }

    private void showAlert(String title, String message, String type) {
        Alert.AlertType alertType = Alert.AlertType.INFORMATION;
        if ("ERROR".equals(type)) alertType = Alert.AlertType.ERROR;

        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}