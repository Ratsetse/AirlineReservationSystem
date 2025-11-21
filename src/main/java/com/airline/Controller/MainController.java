package com.airline.Controller;

import com.airline.database.DatabaseConnection;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private VBox mainContainer;
    @FXML private GridPane statsGrid;
    @FXML private VBox flightsContainer;
    @FXML private ProgressBar progressBar;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Button reservationButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupModernUI();
        setupVisualEffects();
        loadRealTimeData();
        testDatabaseConnection();
    }

    private void testDatabaseConnection() {
        new Thread(() -> {
            boolean connected = DatabaseConnection.testConnection();
            if (connected) {
                DatabaseConnection.initializeDatabase();
            } else {
                javafx.application.Platform.runLater(() ->
                        showAlert("Database Error",
                                "Cannot connect to database. Please check:\n" +
                                        "1. PostgreSQL is running\n" +
                                        "2. Database credentials are correct\n" +
                                        "3. Network connection is active", "ERROR"));
            }
        }).start();
    }

    private void setupModernUI() {
        if (mainContainer != null) {
            mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%); -fx-padding: 20;");
        }

        if (reservationButton != null) {
            reservationButton.setStyle("-fx-background-color: linear-gradient(to right, #e74c3c, #c0392b); " +
                    "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; " +
                    "-fx-background-radius: 25; -fx-padding: 15 30; -fx-cursor: hand;");
        }
    }

    private void setupVisualEffects() {
        if (reservationButton != null) {
            DropShadow dropShadow = new DropShadow();
            dropShadow.setColor(Color.rgb(231, 76, 60, 0.4));
            dropShadow.setRadius(15);
            reservationButton.setEffect(dropShadow);

            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), reservationButton);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.7);
            fadeTransition.setCycleCount(Timeline.INDEFINITE);
            fadeTransition.setAutoReverse(true);
            fadeTransition.play();
        }
    }

    private void loadRealTimeData() {
        loadStatistics();
        loadTodayFlights();
    }

    private void loadStatistics() {
        if (statsGrid == null) return;

        statsGrid.getChildren().clear();

        try {
            int totalFlights = getTotalFlights();
            int activeReservations = getActiveReservations();
            double revenueToday = getRevenueToday();
            int availableSeats = getAvailableSeats();

            String[][] statsData = {
                    {String.valueOf(totalFlights), "Total Flights", "#3498db", "FLIGHT"},
                    {String.valueOf(activeReservations), "Active Reservations", "#2ecc71", "TICKET"},
                    {"M" + String.format("%,.0f", revenueToday), "Revenue Today", "#f39c12", "MONEY"},
                    {String.valueOf(availableSeats), "Available Seats", "#9b59b6", "SEAT"}
            };

            for (int i = 0; i < statsData.length; i++) {
                VBox statCard = createStatCard(statsData[i][0], statsData[i][1], statsData[i][2], statsData[i][3]);
                statsGrid.add(statCard, i % 2, i / 2);
            }

        } catch (SQLException e) {
            showDefaultStats();
        }
    }

    private void showDefaultStats() {
        if (statsGrid == null) return;

        String[][] statsData = {
                {"47", "Total Flights", "#3498db", "FLIGHT"},
                {"128", "Active Reservations", "#2ecc71", "TICKET"},
                {"M12,847", "Revenue Today", "#f39c12", "MONEY"},
                {"234", "Available Seats", "#9b59b6", "SEAT"}
        };

        for (int i = 0; i < statsData.length; i++) {
            VBox statCard = createStatCard(statsData[i][0], statsData[i][1], statsData[i][2], statsData[i][3]);
            statsGrid.add(statCard, i % 2, i / 2);
        }
    }

    private VBox createStatCard(String value, String title, String color, String icon) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20, 15, 20, 15));
        card.setPrefSize(150, 120);
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 15; -fx-border-radius: 15;");

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: white; -fx-text-alignment: center;");
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);

        card.getChildren().addAll(iconLabel, valueLabel, titleLabel);
        return card;
    }

    private void loadTodayFlights() {
        if (flightsContainer == null) return;

        flightsContainer.getChildren().clear();

        try {
            String sql = "SELECT f_code, f_name, f_source, f_destination FROM flight_information LIMIT 4";

            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                int i = 0;
                String[][] defaultFlights = {
                        {"SG301", "Delhi to Mumbai", "10:00 - 12:30", "22 seats available", "#27ae60"},
                        {"AI202", "Mumbai to Goa", "14:00 - 16:00", "Fully Booked", "#e74c3c"},
                        {"UK305", "Delhi to Bangalore", "16:30 - 19:00", "15 seats available", "#27ae60"},
                        {"AI101", "Delhi to Goa", "08:00 - 12:00", "45 seats available", "#27ae60"}
                };

                while (rs.next() && i < defaultFlights.length) {
                    String flightCode = rs.getString("f_code");
                    String flightName = rs.getString("f_name");
                    String route = rs.getString("f_source") + " to " + rs.getString("f_destination");
                    HBox flightCard = createFlightCard(flightCode, route,
                            defaultFlights[i][2], defaultFlights[i][3], defaultFlights[i][4]);
                    flightsContainer.getChildren().add(flightCard);
                    i++;
                }

            }
        } catch (SQLException e) {
            showDefaultFlights();
        }
    }

    private void showDefaultFlights() {
        if (flightsContainer == null) return;

        String[][] defaultFlights = {
                {"AI101", "Delhi to Mumbai", "10:00 - 12:30", "22 seats available", "#27ae60"},
                {"SG202", "Mumbai to Goa", "14:00 - 16:00", "Fully Booked", "#e74c3c"},
                {"IG303", "Delhi to Bangalore", "16:30 - 19:00", "15 seats available", "#27ae60"},
                {"UK404", "Delhi to Goa", "08:00 - 12:00", "45 seats available", "#27ae60"}
        };

        for (String[] flight : defaultFlights) {
            HBox flightCard = createFlightCard(flight[0], flight[1], flight[2], flight[3], flight[4]);
            flightsContainer.getChildren().add(flightCard);
        }
    }

    private HBox createFlightCard(String code, String route, String time, String status, String statusColor) {
        HBox flightCard = new HBox(15);
        flightCard.setPadding(new Insets(20));
        flightCard.setAlignment(Pos.CENTER_LEFT);
        flightCard.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 12; -fx-border-radius: 12;");

        VBox codeBox = new VBox(5);
        codeBox.setAlignment(Pos.CENTER_LEFT);
        Label codeLabel = new Label("FLIGHT " + code);
        codeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        codeBox.getChildren().add(codeLabel);

        VBox routeBox = new VBox(5);
        Label routeLabel = new Label(route);
        routeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        routeBox.getChildren().addAll(routeLabel, timeLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusLabel = new Label(status);
        statusLabel.setPadding(new Insets(8, 15, 8, 15));
        statusLabel.setStyle("-fx-background-color: " + statusColor + "; " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; " +
                "-fx-background-radius: 15; -fx-border-radius: 15;");

        flightCard.getChildren().addAll(codeBox, routeBox, spacer, statusLabel);
        return flightCard;
    }

    // Database methods
    private int getTotalFlights() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM flight_information";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("total") : 0;
        }
    }

    private int getActiveReservations() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM reservations WHERE status = 'CONFIRMED'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("total") : 0;
        }
    }

    private double getRevenueToday() throws SQLException {
        String sql = "SELECT COALESCE(SUM(fare), 0) as total FROM reservations WHERE travel_date = CURRENT_DATE";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getDouble("total") : 0;
        }
    }

    private int getAvailableSeats() throws SQLException {
        String sql = "SELECT COALESCE(SUM(t_exe_seatno + t_eco_seatno), 0) as total FROM flight_information";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("total") : 0;
        }
    }

    @FXML
    private void startReservationProcess() {
        if (progressBar == null || progressIndicator == null) return;

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    progressBar.setProgress(0);
                    progressIndicator.setProgress(0);
                    progressBar.setStyle("-fx-accent: #e74c3c;");
                }),
                new KeyFrame(Duration.seconds(1), e -> {
                    progressBar.setProgress(0.3);
                    progressIndicator.setProgress(0.3);
                    progressBar.setStyle("-fx-accent: #f39c12;");
                }),
                new KeyFrame(Duration.seconds(2), e -> {
                    progressBar.setProgress(0.6);
                    progressIndicator.setProgress(0.6);
                    progressBar.setStyle("-fx-accent: #3498db;");
                }),
                new KeyFrame(Duration.seconds(3), e -> {
                    progressBar.setProgress(1.0);
                    progressIndicator.setProgress(1.0);
                    progressBar.setStyle("-fx-accent: #2ecc71;");
                    showReservation();
                })
        );
        timeline.play();
    }

    // Navigation methods
    @FXML
    private void showCustomerManagement() {
        loadWindow("/com/airline/view/customer-form.fxml", "Customer Management");
    }

    @FXML
    private void showFlightDetails() {
        loadWindow("/com/airline/view/flight-details.fxml", "Flight Details");
    }

    @FXML
    private void showReservation() {
        loadWindow("/com/airline/view/reservation-form.fxml", "Make Reservation");
    }

    @FXML
    private void showCancellation() {
        showAlert("Cancellation", "Cancellation feature will be implemented here", "INFO");
    }

    @FXML
    private void showAbout() {
        showAlert("About",
                "Airline Reservation System\n" +
                        "Version 2.0\n" +
                        "Developed for OOP2 Project", "INFO");
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    // Member dashboard navigation methods
    @FXML
    private void showMember1Dashboard() {
        loadMemberWindow("1", "Member 1 - Customer Management");
    }

    @FXML
    private void showMember2Dashboard() {
        loadMemberWindow("2", "Member 2 - Flight Operations");
    }

    @FXML
    private void showMember3Dashboard() {
        loadMemberWindow("3", "Member 3 - Reservations");
    }

    @FXML
    private void showMember4Dashboard() {
        loadMemberWindow("4", "Member 4 - Cancellation and Refunds");
    }

    @FXML
    private void showMember5Dashboard() {
        loadMemberWindow("5", "Member 5 - Fare Management");
    }

    @FXML
    private void showMember6Dashboard() {
        loadMemberWindow("6", "Member 6 - Analytics");
    }

    @FXML
    private void showMember7Dashboard() {
        loadMemberWindow("7", "Member 7 - Security and Compliance");
    }

    /**
     * Fixed method to load member dashboards
     */
    private void loadMemberWindow(String memberNumber, String title) {
        String fxmlPath = "/com/airline/view/member" + memberNumber + "-dashboard.fxml";

        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource != null) {
                System.out.println("Loading FXML from: " + fxmlPath);
                Parent root = FXMLLoader.load(resource);
                Stage stage = new Stage();
                stage.setTitle(title);
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                showAlert("Dashboard Not Available",
                        "The dashboard for Member " + memberNumber + " is not available.\n" +
                                "File path: " + fxmlPath + " not found.\n\n" +
                                "Please make sure the FXML file exists in the correct location.", "INFO");
            }
        } catch (Exception e) {
            showAlert("Error", "Cannot load Member " + memberNumber + " dashboard: " + e.getMessage(), "ERROR");
            e.printStackTrace();
        }
    }

    private void loadWindow(String fxmlPath, String title) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                showAlert("Error", "Cannot find " + fxmlPath, "ERROR");
                return;
            }

            Parent root = FXMLLoader.load(fxmlUrl);
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Cannot load " + title + ": " + e.getMessage(), "ERROR");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, String type) {
        Alert.AlertType alertType = Alert.AlertType.INFORMATION;

        switch (type) {
            case "ERROR":
                alertType = Alert.AlertType.ERROR;
                break;
            case "WARNING":
                alertType = Alert.AlertType.WARNING;
                break;
        }

        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}