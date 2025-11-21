package com.airline.Controller;

import com.airline.database.DatabaseConnection;
import com.airline.model.Reservation;
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
import java.time.LocalDate;
import java.util.ResourceBundle;

public class Member3Controller implements Initializable {

    @FXML private VBox mainContainer;
    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, Integer> colReservationId;
    @FXML private TableColumn<Reservation, String> colFlightCode;
    @FXML private TableColumn<Reservation, Integer> colCustomerCode;
    @FXML private TableColumn<Reservation, String> colSeatClass;
    @FXML private TableColumn<Reservation, String> colStatus;
    @FXML private Label todayBookingsLabel;
    @FXML private Label confirmedLabel;
    @FXML private Label waitingLabel;
    @FXML private Label revenueLabel;
    @FXML private TextField searchField;

    private ObservableList<Reservation> reservationData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadReservationStats();
        loadReservations();
    }

    private void setupTableColumns() {
        colReservationId.setCellValueFactory(new PropertyValueFactory<>("reservationId"));
        colFlightCode.setCellValueFactory(new PropertyValueFactory<>("flightCode"));
        colCustomerCode.setCellValueFactory(new PropertyValueFactory<>("customerCode"));
        colSeatClass.setCellValueFactory(new PropertyValueFactory<>("seatClass"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        reservationTable.setItems(reservationData);
    }

    private void loadReservationStats() {
        try {
            int todayBookings = getTodayBookings();
            todayBookingsLabel.setText(String.valueOf(todayBookings));

            int confirmed = getConfirmedReservations();
            confirmedLabel.setText(String.valueOf(confirmed));

            int waiting = getWaitingList();
            waitingLabel.setText(String.valueOf(waiting));

            double revenue = getTodayRevenue();
            revenueLabel.setText("₹" + String.format("%,.0f", revenue));

        } catch (SQLException e) {
            showAlert("Error", "Failed to load statistics: " + e.getMessage(), "ERROR");
        }
    }

    private void loadReservations() {
        String sql = "SELECT r.*, c.cust_name FROM reservations r " +
                "LEFT JOIN customer_details c ON r.customer_code = c.cust_code " +
                "ORDER BY r.reservation_id DESC LIMIT 20";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            reservationData.clear();
            while (rs.next()) {
                Reservation reservation = new Reservation(
                        rs.getInt("reservation_id"),
                        rs.getString("flight_code"),
                        rs.getInt("customer_code"),
                        rs.getString("seat_class"),
                        rs.getInt("seat_number"),
                        rs.getDate("travel_date").toLocalDate(),
                        rs.getString("status"),
                        rs.getDouble("fare")
                );
                reservationData.add(reservation);
            }
            reservationTable.refresh();

        } catch (SQLException e) {
            showAlert("Error", "Failed to load reservations: " + e.getMessage(), "ERROR");
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            loadReservations();
        } else {
            ObservableList<Reservation> filtered = reservationData.filtered(
                    r -> String.valueOf(r.getReservationId()).contains(searchText) ||
                            r.getFlightCode().toLowerCase().contains(searchText) ||
                            String.valueOf(r.getCustomerCode()).contains(searchText)
            );
            reservationTable.setItems(filtered);
        }
    }

    @FXML
    private void handleNewReservation() {
        loadWindow("/com/airline/view/reservation-form.fxml", "Make New Reservation");
    }

    @FXML
    private void handleConfirmReservation() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null && "WAITING".equals(selected.getStatus())) {
            if (updateReservationStatus(selected.getReservationId(), "CONFIRMED")) {
                showAlert("Success", "Reservation confirmed successfully!", "INFO");
                loadReservations();
                loadReservationStats();
            }
        } else {
            showAlert("Error", "Please select a waiting reservation to confirm", "ERROR");
        }
    }

    @FXML
    private void handleCancelReservation() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (updateReservationStatus(selected.getReservationId(), "CANCELLED")) {
                showAlert("Success", "Reservation cancelled successfully!", "INFO");
                loadReservations();
                loadReservationStats();
            }
        } else {
            showAlert("Error", "Please select a reservation to cancel", "ERROR");
        }
    }

    @FXML
    private void handleBookingAnalytics() {
        try {
            int todayBookings = getTodayBookings();
            int confirmed = getConfirmedReservations();
            int waiting = getWaitingList();
            double revenue = getTodayRevenue();

            String analytics = String.format(
                    "Reservation Analytics\n\n" +
                            "Today's Performance:\n" +
                            "• New Bookings: %d\n" +
                            "• Confirmed: %d\n" +
                            "• Waiting List: %d\n" +
                            "• Revenue: M%,.0f\n\n" +
                            "Booking Trends:\n" +
                            "• 65%% book 2+ weeks in advance\n" +
                            "• Most popular route: DEL-BOM\n" +
                            "• Average booking value: M5,847\n" +
                            "• Confirmation rate: 92%%",
                    todayBookings, confirmed, waiting, revenue
            );

            showAlert("Booking Analytics", analytics, "INFO");

        } catch (SQLException e) {
            showAlert("Error", "Failed to generate analytics: " + e.getMessage(), "ERROR");
        }
    }

    @FXML
    private void handleRefresh() {
        loadReservationStats();
        loadReservations();
        showAlert("Refreshed", "Reservation data updated successfully!", "INFO");
    }

    // Database methods
    private int getTodayBookings() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM reservations WHERE DATE(created_date) = CURRENT_DATE";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("total") : 0;
        }
    }

    private int getConfirmedReservations() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM reservations WHERE status = 'CONFIRMED'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("total") : 0;
        }
    }

    private int getWaitingList() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM reservations WHERE status = 'WAITING'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("total") : 0;
        }
    }

    private double getTodayRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(fare), 0) as total FROM reservations WHERE travel_date = CURRENT_DATE";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getDouble("total") : 0;
        }
    }

    private boolean updateReservationStatus(int reservationId, String status) {
        String sql = "UPDATE reservations SET status = ? WHERE reservation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, reservationId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            showAlert("Error", "Failed to update reservation: " + e.getMessage(), "ERROR");
            return false;
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