package com.airline;

import com.airline.database.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println(" Starting Airline Reservation System...");

            // Initialize database
            initializeDatabaseWithRetry();

            System.out.println("Loading login screen...");

            // Load the login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/airline/view/login.fxml"));
            Parent root = loader.load();

            System.out.println("Login screen loaded successfully!");

            // Set up the stage
            primaryStage.setTitle("Skyline Airlines - Login");
            Scene scene = new Scene(root, 500, 700);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Application Error");
            alert.setHeaderText("Failed to load application");
            alert.setContentText("Error: " + e.getMessage() +
                    "\n\nCommon solutions:\n" +
                    "1. Check if PostgreSQL is running\n" +
                    "2. Verify database credentials\n" +
                    "3. Check FXML file locations");
            alert.showAndWait();
        }
    }

    private void initializeDatabaseWithRetry() {
        System.out.println("Testing database connection...");

        boolean connected = DatabaseConnection.testConnection();
        if (connected) {
            System.out.println("Initializing database tables...");
            DatabaseConnection.initializeDatabase();
        } else {
            // Show warning but continue
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Database Warning");
            alert.setHeaderText("Database Connection Failed");
            alert.setContentText("The application will run in DEMO MODE.\n\n" +
                    "Data will not be saved permanently.\n" +
                    "Please check:\n" +
                    "• PostgreSQL is running\n" +
                    "• Database credentials are correct\n" +
                    "• Port 5432 is accessible");
            alert.showAndWait();
        }
    }


    public static void main(String[] args) {
        System.out.println("Starting Airline Reservation System...");
        launch(args);
    }
}