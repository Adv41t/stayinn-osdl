module com.hotel {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.base;

    opens com.hotel to javafx.graphics;
    opens com.hotel.model to javafx.base;
    opens com.hotel.ui to javafx.fxml;
}
