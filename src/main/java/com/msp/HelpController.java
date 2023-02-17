package com.msp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class HelpController implements Initializable {
    @FXML private Button btnBack;
    @FXML private Label help1, help2, help3, help4;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnBack.setOnAction(e -> handleBtnBack());
    }

    private void handleBtnBack() {
        try {
            Parent start = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("start.fxml")));
            Scene scene = new Scene(start);
            Stage primaryStage = (Stage) btnBack.getScene().getWindow();
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
