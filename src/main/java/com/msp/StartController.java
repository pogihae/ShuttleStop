package com.msp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class StartController implements Initializable {
    @FXML private Button btnMap;
    @FXML private Button btnFile;
    @FXML private Button btnHelp;

    public static String imageUrl;
    public static double height;
    public static double width;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnMap.setOnAction(e -> handleBtnMap());
        btnFile.setOnAction(e -> handleBtnFile());
        btnHelp.setOnAction(e -> handleBtnHelp());
    }

    public void handleBtnHelp(){
        try {
            Parent help = FXMLLoader.load(Objects.requireNonNull(Class.forName("gachon.algorithm.StartController").getResource("help.fxml")));
            Scene scene = new Scene(help);
            Stage primaryStage = (Stage) btnHelp.getScene().getWindow();
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleBtnMap() {
        Stage stage = new Stage();
        try {
            Parent canvas = FXMLLoader.load(Objects
                    .requireNonNull(Class.forName("gachon.algorithm.StartController").getResource("placename.fxml")));
            Scene scene = new Scene(canvas,600,600);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            File file = new File(WebMapController.placeName+".png");
            if(!file.exists()) return;
            imageUrl = file.toURI().toString();
        } catch (NullPointerException ne) {
            return;
        }
        canvasStart();
    }

    public void handleBtnFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Background image");
        try {
            File file = fileChooser.showOpenDialog(btnFile.getScene().getWindow());
            if(!file.exists()) return;
            imageUrl = file.toURI().toString();
        } catch (NullPointerException ne) {
            return;
        }
        canvasStart();
    }

    private void canvasStart() {
        Image image = new Image(imageUrl);
        height = image.getHeight();
        width = image.getWidth();
        try {
            Parent canvas = FXMLLoader.load(Objects.requireNonNull(Class.forName("gachon.algorithm.StartController").getResource("canvas.fxml")));
            Scene scene = new Scene(canvas);
            Stage primaryStage = (Stage) btnFile.getScene().getWindow();
            primaryStage.setHeight(height+210);
            primaryStage.setWidth(width+280);
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
