package org.signature;

import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.signature.ui.audioPlayer.AudioPlayer;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WelcomeScreenController implements Initializable {

    private static final DoubleProperty workDone = new SimpleDoubleProperty(0.0);

    @FXML
    private JFXProgressBar appLoadProgress;

    public static void updateProgress(double progress) {
        workDone.set(workDone.add(progress).doubleValue());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final Stage stage = new Stage();
        Stage loaderStage = App.getStage();
        final Task<Void> loadAppTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                App.setStage(stage);

                loadAudioPlayerData();

                Parent root = FXMLLoader.load(WelcomeScreenController.class.getResource("ui/MainWindow.fxml"));
                WelcomeScreenController.updateProgress(10.0);
                Thread.sleep(600);

                Platform.runLater(() -> stage.setScene(new Scene(root)));
                Thread.sleep(600);

                WelcomeScreenController.updateProgress(10.0);
                stage.setMaximized(true);
                stage.setResizable(false);
                stage.setTitle("Media Player");
                WelcomeScreenController.updateProgress(5.0);
                Thread.sleep(600);
                return null;
            }
        };

        loadAppTask.setOnSucceeded(event -> {
            stage.show();
            loaderStage.close();
            stage.toFront();
        });

        appLoadProgress.progressProperty().bind(workDone.divide(100.0));

        ScheduledExecutorService scheduledLoadService = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });
        scheduledLoadService.schedule(() -> {
            Thread thread = new Thread(loadAppTask);
            thread.setDaemon(true);
            thread.setName("Loading Media Player");
            thread.start();
            scheduledLoadService.shutdown();
        }, 1, TimeUnit.SECONDS);
    }

    private void loadAudioPlayerData() {
        WelcomeScreenController.updateProgress(5.0);
        AudioPlayer.getInstance().loadLibraries();
        WelcomeScreenController.updateProgress(5.0);
        AudioPlayer.getInstance().loadMusic();
        AudioPlayer.getInstance().loadRecentlyPlayed();
        AudioPlayer.getInstance().loadFavourites();
        WelcomeScreenController.updateProgress(10.0);
    }
}
