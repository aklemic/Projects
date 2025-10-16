module com.example.filmmanager {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.filmmanager to javafx.fxml;
    exports com.example.filmmanager;
}