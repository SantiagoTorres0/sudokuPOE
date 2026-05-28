module com.example.demo2 {
    requires javafx.controls;
    requires javafx.fxml;

    // Export packages so that javafx.fxml can access the controller
    opens com.example.demo2 to javafx.graphics, javafx.fxml;
    opens com.example.demo2.controller to javafx.fxml;
    opens com.example.demo2.view to javafx.fxml;
    opens com.example.demo2.model to javafx.fxml;
}
