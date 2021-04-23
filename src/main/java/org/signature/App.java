package org.signature;

import javafx.application.Application;
import javafx.application.Preloader;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jnativehook.GlobalScreen;
import org.signature.ui.MainWindowController;
import org.signature.ui.audioPlayer.AudioPlayer;
import org.signature.ui.audioPlayer.ConsoleController;
import org.signature.ui.audioPlayer.Inventory;
import org.signature.ui.audioPlayer.dialogs.DownloadsController;
import org.signature.ui.audioPlayer.tabs.BrowseOnlineTabController;

import java.awt.*;

/**
 * JavaFX App
 */
public class App extends Application {

    private static final double SCREEN_WIDTH = Screen.getPrimary().getVisualBounds().getWidth();
    private static final double SCREEN_HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();

    private static final Logger LOGGER = LogManager.getLogger(App.class);

    private final String APP_ICON = "Media-player-icon-circle.png";

    private static Stage primaryStage;

    private static final DoubleProperty progress = new SimpleDoubleProperty(0.0);

    public static void main(String[] args) {
//        System.setProperty("javafx.sg.warn", "true");
        System.setProperty("javafx.preloader", MediaPlayerPreloader.class.getCanonicalName()); //fully qualified preloader class name
        Application.launch(App.class, args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        progress.addListener((observable, oldValue, newValue) -> notifyPreloader(new Preloader.ProgressNotification(newValue.doubleValue()/100.0)));

        try {
            GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-Italic.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-Bold.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-Thin.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-Medium.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-Black.ttf")));
            graphicsEnvironment.registerFont(Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/Roboto-Light.ttf")));

            AudioPlayer.getInstance().startDatabaseSession();
            updateProgress(2.0);

            loadAudioPlayerData();
            loadVideoPlayerData();

        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "Exception in Application initialize! ", ex);
            this.cleanUp();
            throw new RuntimeException(ex.getCause());
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        try {
            primaryStage = stage;
            stage.setScene(new Scene(FXMLLoader.load(MainWindowController.class.getResource("MainWindow.fxml"))));
            stage.getIcons().add(new Image(App.class.getResourceAsStream(APP_ICON)));

            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setMinWidth(bounds.getWidth());
            stage.setMinHeight(bounds.getHeight());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
            stage.setMaximized(true);
            stage.setTitle("MP34");

            KeyCombination keyCombination = new KeyCodeCombination(KeyCode.J, KeyCombination.CONTROL_DOWN);
            Runnable keyTask = () -> DownloadsController.getInstance().showDownloads();
            App.getStage().getScene().getAccelerators().put(keyCombination, keyTask);

            notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_START));

            stage.show();
            stage.toFront();
        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "Exception in Application start! ", ex);
            this.cleanUp();
            throw new RuntimeException(ex.getCause());
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        cleanUp();
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

    public static void updateProgress(double progress) {
        App.progress.set(App.progress.add(progress).doubleValue());
    }

    private void loadAudioPlayerData() {
        AudioPlayer.getInstance().loadLibraries();
        updateProgress(5.0);
        AudioPlayer.getInstance().loadMusic();
        updateProgress(5.0);
        AudioPlayer.getInstance().loadPlaylists();
        updateProgress(2.5);
        AudioPlayer.getInstance().loadRecentlyPlayed();
        updateProgress(2.5);
    }

    private void loadVideoPlayerData() {
        updateProgress(15.0);
    }

    /*
    * This method will close all existing connection,
    * and will shutdown all thread pools used by application,
    * and unregister any service.
    * */
    private void cleanUp() throws Exception {
        AudioPlayer.getInstance().getMediaPool().shutdown();
        BrowseOnlineTabController.getBrowseOnlineServicePool().shutdown();
        ConsoleController.getMediaPlayerService().shutdown();
        DownloadsController.getDownloadServicePool().shutdown();
        Inventory.getDatabaseServicePool().shutdown();
        while (!Inventory.getDatabaseServicePool().isShutdown() || !DownloadsController.getDownloadServicePool().isShutdown()) {
            AudioPlayer.getInstance().stopDatabaseSession();
        }
        GlobalScreen.unregisterNativeHook();
    }
}