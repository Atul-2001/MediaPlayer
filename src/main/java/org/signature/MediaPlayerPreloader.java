package org.signature;

import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class MediaPlayerPreloader extends Preloader {

    private static final Logger LOGGER = LogManager.getLogger(MediaPlayerPreloader.class);

    private final String APP_ICON = "Media-player-icon-circle.png";

    private Stage preloaderStage;
    private Scene scene;

    @Override
    public void init() throws Exception {
        super.init();
        try {
            scene = new Scene(FXMLLoader.load(WelcomeScreenController.class.getResource("WelcomeScreen.fxml")));
        } catch (IOException ex) {
            LOGGER.log(Level.ERROR, "Exception in Application loader! ", ex);
            throw new RuntimeException(ex.getCause());
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        preloaderStage = stage;
        stage.setScene(scene);
        stage.getIcons().add(new Image(MediaPlayerPreloader.class.getResourceAsStream(APP_ICON)));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("MP34");
        stage.setResizable(false);
        stage.show();
        stage.toFront();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    @Override
    public void handleProgressNotification(ProgressNotification info) {
        WelcomeScreenController.getInstance().updateProgress(-1);
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        //ignore, hide after application signals it is ready
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        if (info instanceof ProgressNotification) {
            WelcomeScreenController.getInstance().updateProgress(((ProgressNotification) info).getProgress());
        } else if (info instanceof StateChangeNotification) {
            preloaderStage.hide();
        }
    }
}
