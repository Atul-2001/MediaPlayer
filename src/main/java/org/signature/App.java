package org.signature;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.ui.audioPlayer.AudioPlayer;

import java.awt.*;
import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static final double SCREEN_WIDTH = Screen.getPrimary().getVisualBounds().getWidth();
    private static final double SCREEN_HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();
    private static final Logger LOGGER = LogManager.getLogger(App.class);
    private static Stage primaryStage;

    public static void main(String[] args) {
        System.setProperty("javafx.sg.warn", "true");
        launch();
    }

    public static double getScreenWidth() {
        return SCREEN_WIDTH;
    }

    public static double getScreenHeight() {
        return SCREEN_HEIGHT;
    }

    public static Stage getStage() {
        return primaryStage;
    }


    public static void setStage(Stage newStage) {
        primaryStage = newStage;
    }

    @Override
    public void init() throws Exception {
        super.init();

        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf")));
        graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-Italic.ttf")));
        graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-Bold.ttf")));
        graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-Thin.ttf")));
        graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-Medium.ttf")));
        graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-Black.ttf")));
        graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-Light.ttf")));

        AudioPlayer.getInstance().startSession();
    }

    @Override
    public void start(Stage stage) {
        try {
            primaryStage = stage;
            Parent root = FXMLLoader.load(App.class.getResource("WelcomeScreen" + ".fxml"));
            stage.setScene(new Scene(root));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Media Player");
            stage.setResizable(false);
            stage.show();
            stage.toFront();
        } catch (NullPointerException | IOException e) {
            LOGGER.log(Level.ERROR, "Failed to load WelcomeScreen.fxml file! " + e.getLocalizedMessage());
            if (Platform.isFxApplicationThread()) {
                Platform.exit();
            } else {
                System.exit(0);
            }
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        AudioPlayer.getInstance().stopSession();
    }
}