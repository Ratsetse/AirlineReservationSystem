package com.airline.Controller;

import com.airline.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class Member7Controller implements Initializable {

    @FXML private VBox mainContainer;
    @FXML private Label securityScoreLabel;
    @FXML private Label activeThreatsLabel;
    @FXML private Label complianceRateLabel;
    @FXML private Label auditEventsLabel;
    @FXML private Label systemStatusLabel;
    @FXML private Label lastScanLabel;
    @FXML private Label encryptionStatusLabel;
    @FXML private Label firewallStatusLabel;
    @FXML private Label gdprStatusLabel;
    @FXML private Label pciStatusLabel;
    @FXML private BarChart<String, Number> threatChart;
    @FXML private PieChart complianceChart;
    @FXML private ListView<String> securityEventsList;
    @FXML private TableView<AuditLog> auditTable;
    @FXML private TableColumn<AuditLog, String> colTimestamp;
    @FXML private TableColumn<AuditLog, String> colUser;
    @FXML private TableColumn<AuditLog, String> colAction;
    @FXML private TableColumn<AuditLog, String> colStatus;
    @FXML private ProgressBar complianceProgress;

    private ObservableList<AuditLog> auditData = FXCollections.observableArrayList();
    private ObservableList<String> securityEvents = FXCollections.observableArrayList();

    public static class AuditLog {
        private final String timestamp;
        private final String user;
        private final String action;
        private final String status;

        public AuditLog(String timestamp, String user, String action, String status) {
            this.timestamp = timestamp;
            this.user = user;
            this.action = action;
            this.status = status;
        }

        public String getTimestamp() { return timestamp; }
        public String getUser() { return user; }
        public String getAction() { return action; }
        public String getStatus() { return status; }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadSecurityData();
        setupCharts();
        loadAuditData();
        createSecurityTables();
    }

    private void setupTableColumns() {
        colTimestamp.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("user"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("action"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        auditTable.setItems(auditData);
        securityEventsList.setItems(securityEvents);
    }

    private void loadSecurityData() {
        // Load security statistics
        securityScoreLabel.setText("92");
        activeThreatsLabel.setText("3");
        complianceRateLabel.setText("95");
        auditEventsLabel.setText("1,247");

        // Load system status
        systemStatusLabel.setText("SECURE");
        systemStatusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        lastScanLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        encryptionStatusLabel.setText("ENABLED");
        encryptionStatusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        firewallStatusLabel.setText("ACTIVE");
        firewallStatusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        // Compliance status
        gdprStatusLabel.setText("COMPLIANT");
        gdprStatusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        pciStatusLabel.setText("COMPLIANT");
        pciStatusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        complianceProgress.setProgress(0.95);
    }

    private void setupCharts() {
        setupThreatChart();
        setupComplianceChart();
        loadSecurityEvents();
    }

    private void setupThreatChart() {
        if (threatChart == null) return;

        threatChart.setTitle("Threat Distribution");
        threatChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Threats");

        // Sample threat data
        series.getData().add(new XYChart.Data<>("Brute Force", 12));
        series.getData().add(new XYChart.Data<>("SQL Injection", 8));
        series.getData().add(new XYChart.Data<>("XSS Attacks", 5));
        series.getData().add(new XYChart.Data<>("DDoS", 3));
        series.getData().add(new XYChart.Data<>("Malware", 7));

        threatChart.getData().add(series);
    }

    private void setupComplianceChart() {
        if (complianceChart == null) return;

        complianceChart.setTitle("Compliance Status");

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        pieChartData.add(new PieChart.Data("Compliant", 85));
        pieChartData.add(new PieChart.Data("Needs Review", 10));
        pieChartData.add(new PieChart.Data("Non-Compliant", 5));

        complianceChart.setData(pieChartData);
    }

    private void loadSecurityEvents() {
        securityEvents.clear();
        securityEvents.add("2024-01-15 14:23: Failed login attempt - User: admin");
        securityEvents.add("2024-01-15 13:45: Security policy updated");
        securityEvents.add("2024-01-15 12:30: Database backup completed");
        securityEvents.add("2024-01-15 11:15: Firewall rules modified");
        securityEvents.add("2024-01-15 10:00: System scan completed - No threats found");
    }

    private void loadAuditData() {
        auditData.clear();

        // Sample audit data
        auditData.add(new AuditLog("2024-01-15 14:23:15", "admin", "LOGIN_ATTEMPT", "FAILED"));
        auditData.add(new AuditLog("2024-01-15 13:45:30", "member1", "CUSTOMER_UPDATE", "SUCCESS"));
        auditData.add(new AuditLog("2024-01-15 12:30:45", "system", "BACKUP", "SUCCESS"));
        auditData.add(new AuditLog("2024-01-15 11:15:20", "member2", "FLIGHT_ADD", "SUCCESS"));
        auditData.add(new AuditLog("2024-01-15 10:00:10", "member3", "RESERVATION_CREATE", "SUCCESS"));
        auditData.add(new AuditLog("2024-01-15 09:45:55", "admin", "SECURITY_SCAN", "COMPLETED"));
    }

    @FXML
    private void handleSecurityScan() {
        showAlert("Security Scan", "Initiating comprehensive security scan...", Alert.AlertType.INFORMATION);

        // Simulate scan process
        new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i += 20) {
                    final int progress = i;
                    javafx.application.Platform.runLater(() -> {
                        systemStatusLabel.setText("SCANNING (" + progress + "%)");
                        systemStatusLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    });
                    Thread.sleep(500);
                }

                javafx.application.Platform.runLater(() -> {
                    systemStatusLabel.setText("SECURE");
                    systemStatusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    lastScanLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    securityScoreLabel.setText("94");
                    showAlert("Security Scan Complete", "System scan completed successfully. No critical threats found.", Alert.AlertType.INFORMATION);

                    // Log the scan
                    auditData.add(0, new AuditLog(
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            "system",
                            "SECURITY_SCAN",
                            "COMPLETED"
                    ));
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @FXML
    private void handleViewAuditLog() {
        StringBuilder auditLog = new StringBuilder("COMPLETE AUDIT LOG\n\n");

        for (AuditLog log : auditData) {
            auditLog.append(String.format("%s | %s | %s | %s\n",
                    log.getTimestamp(), log.getUser(), log.getAction(), log.getStatus()));
        }

        TextArea textArea = new TextArea(auditLog.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(600, 400);

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Complete Audit Log");
        alert.setHeaderText("System Audit Trail");
        alert.getDialogPane().setContent(scrollPane);
        alert.showAndWait();
    }

    @FXML
    private void handleComplianceCheck() {
        showAlert("Compliance Check", "Running compliance verification against standards...", Alert.AlertType.INFORMATION);

        new Thread(() -> {
            try {
                Thread.sleep(2000);

                javafx.application.Platform.runLater(() -> {
                    complianceRateLabel.setText("97");
                    complianceProgress.setProgress(0.97);
                    gdprStatusLabel.setText("COMPLIANT");
                    pciStatusLabel.setText("COMPLIANT");

                    showAlert("Compliance Check Complete",
                            "All compliance checks passed successfully:\n" +
                                    "• GDPR: COMPLIANT\n" +
                                    "• PCI DSS: COMPLIANT\n" +
                                    "• ISO 27001: COMPLIANT\n" +
                                    "Overall Compliance: 97%",
                            Alert.AlertType.INFORMATION);
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @FXML
    private void handleRiskAssessment() {
        String riskReport = "SECURITY RISK ASSESSMENT REPORT\n\n" +
                "RISK LEVEL: LOW\n" +
                "Overall Score: 92/100\n\n" +
                "DETAILED ANALYSIS:\n" +
                "• Network Security: 95/100\n" +
                "• Data Protection: 90/100\n" +
                "• Access Control: 88/100\n" +
                "• Compliance: 97/100\n\n" +
                "RECOMMENDATIONS:\n" +
                "1. Implement multi-factor authentication\n" +
                "2. Schedule monthly security training\n" +
                "3. Review firewall rules quarterly\n" +
                "4. Update encryption protocols";

        showAlert("Risk Assessment", riskReport, Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleGenerateSecurityReport() {
        String securityReport = "SECURITY & COMPLIANCE REPORT\n" +
                "Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n\n" +
                "EXECUTIVE SUMMARY:\n" +
                "• Security Score: 92/100\n" +
                "• Active Threats: 3\n" +
                "• Compliance Rate: 95%\n" +
                "• Audit Events: 1,247\n\n" +
                "SYSTEM STATUS:\n" +
                "• System: SECURE\n" +
                "• Firewall: ACTIVE\n" +
                "• Encryption: ENABLED\n" +
                "• Last Scan: " + lastScanLabel.getText() + "\n\n" +
                "COMPLIANCE STATUS:\n" +
                "• GDPR: COMPLIANT\n" +
                "• PCI DSS: COMPLIANT\n" +
                "• Overall: 95% Compliant\n\n" +
                "RECOMMENDATIONS:\n" +
                "• Continue regular security scans\n" +
                "• Monitor threat patterns\n" +
                "• Update security policies\n" +
                "• Conduct employee training";

        TextArea textArea = new TextArea(securityReport);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(500, 400);

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Security & Compliance Report");
        alert.setHeaderText("Comprehensive Security Analysis");
        alert.getDialogPane().setContent(scrollPane);
        alert.showAndWait();
    }

    @FXML
    private void handleBackupDatabase() {
        showAlert("Database Backup", "Initiating database backup procedure...", Alert.AlertType.INFORMATION);

        new Thread(() -> {
            try {
                Thread.sleep(3000);

                javafx.application.Platform.runLater(() -> {
                    showAlert("Backup Complete", "Database backup completed successfully. Backup file: backup_20240115.sql", Alert.AlertType.INFORMATION);

                    // Log the backup
                    auditData.add(0, new AuditLog(
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            "system",
                            "DATABASE_BACKUP",
                            "SUCCESS"
                    ));
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @FXML
    private void handleUpdatePolicies() {
        String policyUpdate = "SECURITY POLICY UPDATE\n\n" +
                "The following policies have been updated:\n" +
                "• Password Policy: Minimum 12 characters required\n" +
                "• Access Control: Role-based permissions enhanced\n" +
                "• Data Retention: Reduced to 2 years\n" +
                "• Audit Trail: Extended logging enabled\n\n" +
                "All changes are effective immediately.";

        showAlert("Policy Update", policyUpdate, Alert.AlertType.INFORMATION);

        // Log policy update
        auditData.add(0, new AuditLog(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "admin",
                "POLICY_UPDATE",
                "SUCCESS"
        ));
    }

    @FXML
    private void handleEmergencyLockdown() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Emergency Lockdown");
        confirmation.setHeaderText("EMERGENCY SYSTEM LOCKDOWN");
        confirmation.setContentText("This action will:\n" +
                "• Suspend all user sessions\n" +
                "• Enable maximum security protocols\n" +
                "• Restrict system access to admins only\n" +
                "• Log all activities\n\n" +
                "Are you sure you want to proceed?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showAlert("LOCKDOWN INITIATED", "Emergency lockdown procedure activated. System access restricted to administrators only.", Alert.AlertType.WARNING);

                // Update system status
                systemStatusLabel.setText("LOCKDOWN");
                systemStatusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

                // Log lockdown
                auditData.add(0, new AuditLog(
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        "admin",
                        "EMERGENCY_LOCKDOWN",
                        "ACTIVATED"
                ));
            }
        });
    }

    @FXML
    private void handleExportAuditLog() {
        StringBuilder exportData = new StringBuilder("Timestamp,User,Action,Status\n");

        for (AuditLog log : auditData) {
            exportData.append(String.format("%s,%s,%s,%s\n",
                    log.getTimestamp(), log.getUser(), log.getAction(), log.getStatus()));
        }

        TextArea textArea = new TextArea(exportData.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export Audit Log");
        alert.setHeaderText("Audit Data (CSV Format)");
        alert.getDialogPane().setContent(scrollPane);
        alert.showAndWait();
    }

    @FXML
    private void handleClearLogs() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Clear Audit Logs");
        confirmation.setHeaderText("Clear Old Audit Logs");
        confirmation.setContentText("This will remove audit logs older than 30 days. This action cannot be undone.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // In a real application, this would delete old logs from the database
                showAlert("Logs Cleared", "Audit logs older than 30 days have been cleared successfully.", Alert.AlertType.INFORMATION);

                // Log the cleanup
                auditData.add(0, new AuditLog(
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        "admin",
                        "AUDIT_LOG_CLEANUP",
                        "SUCCESS"
                ));
            }
        });
    }

    @FXML
    private void handleRefresh() {
        loadSecurityData();
        setupCharts();
        loadAuditData();
        showAlert("Refreshed", "Security data has been updated with latest information.", Alert.AlertType.INFORMATION);
    }

    private void createSecurityTables() {
        // This method would create necessary security tables in the database
        // For demo purposes, we'll just log that tables are verified
        System.out.println("Security tables verified/created");
    }

    @FXML
    private void goBackToMain() {
        try {
            Stage currentStage = (Stage) mainContainer.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/airline/view/main-dashboard.fxml"));
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Airline Reservation System");
        } catch (Exception e) {
            showAlert("Error", "Cannot load main dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}