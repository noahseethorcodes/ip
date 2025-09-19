package gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import logos.Logos;

/**
 * A GUI for Duke using FXML.
 */
public class Main extends Application {

    private Logos logos = new Logos();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setMinHeight(450);
            stage.setMinWidth(800);

            fxmlLoader.<MainWindow>getController().setLogos(logos);  // inject the Logos instance
            fxmlLoader.<MainWindow>getController().welcome();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
