module com.airline {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.airline to javafx.fxml;
    opens com.airline.Controller to javafx.fxml;
    opens com.airline.model to javafx.base;

    exports com.airline;
    exports com.airline.Controller;
    exports com.airline.model;
}