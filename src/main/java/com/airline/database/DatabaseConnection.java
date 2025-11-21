package com.airline.database;

import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Grace@2007";

    private static boolean driverLoaded = false;

    static {
        try {
            Class.forName("org.postgresql.Driver");
            driverLoaded = true;
            System.out.println("PostgreSQL JDBC Driver Registered!");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found!");
            System.err.println("Please add PostgreSQL JDBC driver to your project.");
            driverLoaded = false;
        }
    }

    public static Connection getConnection() throws SQLException {
        if (!driverLoaded) {
            throw new SQLException("PostgreSQL JDBC Driver not available");
        }

        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            return conn;
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            throw e;
        }
    }

    public static boolean testConnection() {
        if (!driverLoaded) {
            System.err.println("Cannot test connection - JDBC Driver not available");
            return false;
        }

        try (Connection conn = getConnection()) {
            System.out.println("Database connection test: SUCCESS");
            return true;
        } catch (SQLException e) {
            System.err.println("Database connection test: FAILED");
            return false;
        }
    }

    public static void initializeDatabase() {
        if (!driverLoaded) {
            System.err.println("Cannot initialize database - JDBC Driver not available");
            return;
        }

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // Create customer_details table
            String createCustomerTable = "CREATE TABLE IF NOT EXISTS customer_details (" +
                    "cust_code SERIAL PRIMARY KEY, " +
                    "t_date DATE DEFAULT CURRENT_DATE, " +
                    "cust_name VARCHAR(100) NOT NULL, " +
                    "father_name VARCHAR(100), " +
                    "gender VARCHAR(10), " +
                    "d_o_b DATE, " +
                    "address TEXT, " +
                    "tel_no VARCHAR(15), " +
                    "profession VARCHAR(50), " +
                    "concession VARCHAR(50)" +
                    ")";
            stmt.execute(createCustomerTable);

            // Create flight_information table with fare columns
            String createFlightTable = "CREATE TABLE IF NOT EXISTS flight_information (" +
                    "f_code VARCHAR(10) PRIMARY KEY, " +
                    "f_name VARCHAR(100), " +
                    "t_exe_seatno INTEGER DEFAULT 0, " +
                    "t_eco_seatno INTEGER DEFAULT 0, " +
                    "f_exe_fare DECIMAL(10,2) DEFAULT 5000.00, " +
                    "f_eco_fare DECIMAL(10,2) DEFAULT 2000.00" +
                    ")";
            stmt.execute(createFlightTable);

            // Create reservations table
            String createReservationsTable = "CREATE TABLE IF NOT EXISTS reservations (" +
                    "reservation_id SERIAL PRIMARY KEY, " +
                    "flight_code VARCHAR(10) NOT NULL, " +
                    "customer_code INTEGER NOT NULL, " +
                    "seat_class VARCHAR(10) NOT NULL, " +
                    "seat_number INTEGER NOT NULL, " +
                    "travel_date DATE NOT NULL, " +
                    "status VARCHAR(20) NOT NULL, " +
                    "fare DECIMAL(10,2) NOT NULL, " +
                    "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(createReservationsTable);

            // Create refunds table
            String createRefundsTable = "CREATE TABLE IF NOT EXISTS refunds (" +
                    "refund_id SERIAL PRIMARY KEY, " +
                    "reservation_id INTEGER NOT NULL, " +
                    "refund_amount DECIMAL(10,2) NOT NULL, " +
                    "refund_status VARCHAR(20) DEFAULT 'PENDING', " +
                    "refund_reason TEXT, " +
                    "processed_date TIMESTAMP, " +
                    "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(createRefundsTable);

            System.out.println("All database tables initialized successfully!");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("Driver loaded: " + driverLoaded);
        testConnection();
    }
}