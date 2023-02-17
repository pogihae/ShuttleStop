module gachon.algorithm {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;

    opens gachon.algorithm to javafx.fxml;
    exports gachon.algorithm;
}