package com.msp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class PlaceNameController implements Initializable {
    @FXML private Button findBtn;
    @FXML private TextField placeNameField;

    public static String placeName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        findBtn.setOnAction(e -> {
            placeName = placeNameField.getText();
            if(placeName == null) return;
            Stage stage = new Stage();
            try {
                Parent canvas = FXMLLoader.load(Objects.requireNonNull(Class.forName("gachon.algorithm.PlaceNameController").getResource("webmap.fxml")));
                Scene scene = new Scene(canvas);
                stage.setScene(scene);
                stage.showAndWait();
            } catch (Exception i) {
                i.printStackTrace();
            }
            Stage primaryStage = (Stage) findBtn.getScene().getWindow();
            primaryStage.close();
        });
    }
}
