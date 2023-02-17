package com.msp;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WebMapController implements Initializable {
    @FXML
    Button captureBtn;
    @FXML
    StackPane stackPane;
    @FXML
    WebView webView;

    public static String placeName = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //get place name
        placeName = PlaceNameController.placeName;

        //start search
        WebEngine webEngine = webView.getEngine();
        webEngine.load("http://maps.google.com/?q=" + placeName);

        //capture box
        Rectangle captureRect = new Rectangle(500, 500, 800, 500);
        captureRect.setStroke(Color.RED);
        captureRect.setFill(Color.TRANSPARENT);
        captureRect.setMouseTransparent(true);
        stackPane.getChildren().add(captureRect);

        captureBtn.setOnAction(e -> handleCaptureBtn());
    }

    public void handleCaptureBtn() {
        Bounds bounds = stackPane.getBoundsInLocal();
        Bounds screenBounds = stackPane.localToScreen(bounds);
        int x = (int) ((int) screenBounds.getMinX() + ((screenBounds.getMaxX() - screenBounds.getMinX()) - 800) / 2);
        int y = (int) ((int) screenBounds.getMinY() + ((screenBounds.getMaxY() - screenBounds.getMinY()) - 500) / 2);

        Robot robot = new Robot();
        WritableImage writableImage = robot.getScreenCapture(null, new Rectangle2D(x, y, 800, 500));

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", new File(placeName+".png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Stage stage = (Stage) stackPane.getScene().getWindow();
        stage.close();
    }
}
