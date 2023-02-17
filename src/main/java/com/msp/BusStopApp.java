package com.msp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BusStopApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = null;
        try {
            fxmlLoader = new FXMLLoader(Class.forName("gachon.algorithm.BusStopApp").getResource("start.fxml"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        stage.setTitle("LadyBug");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}