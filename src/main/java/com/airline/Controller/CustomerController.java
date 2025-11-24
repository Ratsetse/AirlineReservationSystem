package com.airline.Controller;

import com.airline.database.DatabaseConnection;
import com.airline.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {

    @FXML private TextField custName;
    @FXML private TextField fatherName;
    @FXML private ComboBox<String> gender;
    @FXML private DatePicker dateOfBirth;
    @FXML private DatePicker travelDate;
    @FXML private TextArea address;
    @FXML private TextField telephone;
    @FXML private TextField profession;
    @FXML private ComboBox<String> concession;
    @FXML private Button saveButton;
    @FXML private TableView<Customer> customerTable;

    @FXML private TableColumn<Customer, Integer> colCode;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, String> colProfession;

    @FXML private VBox mainContainer;

    private ObservableList<Customer> customerData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUI();
        initializeComboBoxes();
        setupTableColumns();
        loadCustomers();
        createCustomerTableIfNotExists();
    }

    private void setupUI() {
        if (mainContainer != null) {
            mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");
        }
    }

    private void initializeComboBoxes() {
        gender.getItems().addAll("Male", "Female", "Other");
        concession.getItems().addAll("None", "Student", "Senior Citizen", "Cancer Patient");
        concession.setValue("None");
    }

    private void setupTableColumns() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("custCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("custName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colProfession.setCellValueFactory(new PropertyValueFactory<>("profession"));

        customerTable.setItems(customerData);
    }

    private void createCustomerTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS customer_details (" +
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

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Customer table created/verified successfully!");
        } catch (SQLException e) {
            System.err.println("Error creating customer table: " + e.getMessage());
        }
    }

    @FXML
    private void saveCustomer() {
        try {
            if (validateForm()) {
                Customer customer = new Customer();
                customer.setCustName(custName.getText().trim());
                customer.setFatherName(fatherName.getText().trim());
                customer.setGender(gender.getValue());
                customer.setDateOfBirth(dateOfBirth.getValue());
                customer.setTravelDate(travelDate.getValue());
                customer.setAddress(address.getText().trim());
                customer.setTelephone(telephone.getText().trim());
                customer.setProfession(profession.getText().trim());
                customer.setConcession(concession.getValue());

                int customerCode = saveCustomerToDatabase(customer);
                if (customerCode > 0) {
                    showAlert("Success", "Customer saved successfully! Customer Code: " + customerCode, "SUCCESS");
                    clearForm();
                    loadCustomers();
                } else {
                    showAlert("Error", "Failed to save customer.", "ERROR");
                }
            }
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage(), "ERROR");
            e.printStackTrace();
        }
    }


    @FXML
    private void clearForm() {
        custName.clear();
        fatherName.clear();
        gender.setValue(null);
        dateOfBirth.setValue(null);
        travelDate.setValue(null);
        address.clear();
        telephone.clear();
        profession.clear();
        concession.setValue("None");
    }

    @FXML
    private void searchCustomers() {
        showAlert("Search", "Search functionality will be implemented here", "INFO");
    }

    private int saveCustomerToDatabase(Customer customer) {
        String sql = "INSERT INTO customer_details (t_date, cust_name, father_name, gender, " +
                "d_o_b, address, tel_no, profession, concession) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDate(1, customer.getTravelDate() != null ?
                    Date.valueOf(customer.getTravelDate()) : Date.valueOf(LocalDate.now()));
            pstmt.setString(2, customer.getCustName());
            pstmt.setString(3, customer.getFatherName());
            pstmt.setString(4, customer.getGender());
            pstmt.setDate(5, customer.getDateOfBirth() != null ?
                    Date.valueOf(customer.getDateOfBirth()) : null);
            pstmt.setString(6, customer.getAddress());
            pstmt.setString(7, customer.getTelephone());
            pstmt.setString(8, customer.getProfession());
            pstmt.setString(9, customer.getConcession());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newCustomerCode = generatedKeys.getInt(1);
                        System.out.println("Customer saved with code: " + newCustomerCode);
                        return newCustomerCode;
                    }
                }
            }
            return -1;

        } catch (SQLException e) {
            showAlert("Database Error", "Error saving customer: " + e.getMessage(), "ERROR");
            e.printStackTrace();
            return -1;
        }
    }

    private void loadCustomers() {
        String sql = "SELECT * FROM customer_details ORDER BY cust_code DESC LIMIT 10";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            customerData.clear();
            System.out.println("DEBUG: Loading customers from database...");

            int count = 0;
            while (rs.next()) {
                count++;
                Customer customer = new Customer(
                        rs.getInt("cust_code"),
                        rs.getDate("t_date") != null ? rs.getDate("t_date").toLocalDate() : null,
                        rs.getString("cust_name"),
                        rs.getString("father_name"),
                        rs.getString("gender"),
                        rs.getDate("d_o_b") != null ? rs.getDate("d_o_b").toLocalDate() : null,
                        rs.getString("address"),
                        rs.getString("tel_no"),
                        rs.getString("profession"),
                        "", // security
                        rs.getString("concession")
                );
                customerData.add(customer);
                System.out.println("DEBUG: Loaded customer - Code: " + customer.getCustCode() + ", Name: " + customer.getCustName());
            }

            System.out.println("Total customers loaded: " + count);
            customerTable.refresh();

        } catch (SQLException e) {
            System.err.println("Error loading customers: " + e.getMessage());
            e.printStackTrace();
            showAlert("Database Error", "Failed to load customers: " + e.getMessage(), "ERROR");
        }
    }

    private boolean validateForm() {
        if (custName.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Customer name is required.", "WARNING");
            custName.requestFocus();
            return false;
        }

        if (telephone.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Telephone number is required.", "WARNING");
            telephone.requestFocus();
            return false;
        }

        return true;
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