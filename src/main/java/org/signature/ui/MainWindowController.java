package org.signature.ui;

import com.jfoenix.controls.JFXTabPane;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.App;
import org.signature.ui.audioPlayer.BaseController;
import org.signature.ui.audioPlayer.ConsoleController;
import org.signature.util.Alerts;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    private static MainWindowController instance = null;

    private final Logger LOGGER = LogManager.getLogger(MainWindowController.class);

    @FXML
    private StackPane root;
    @FXML
    private JFXTabPane tabStack;

    private String youtubeExecName = "youtube-dl.exe";
    private boolean isUnix = false;
    private boolean youtubeDlUpdateChecked = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("linux") || osName.contains("mac"))
            isUnix = true;

        if (isUnix) youtubeExecName = "./youtube-dl";
        else youtubeExecName = "youtube-dl.exe";

        try {
            tabStack.tabMinWidthProperty().bind(root.widthProperty().divide(2));
            tabStack.getTabs().get(0).setContent(FXMLLoader.load(BaseController.class.getResource("AudioPlayerBase.fxml")));
            tabStack.getTabs().get(1).setContent(FXMLLoader.load(org.signature.ui.videoPlayer.BaseController.class.getResource("VideoPlayerBase.fxml")));

            App.getStage().showingProperty().addListener((observable, oldValue, newValue) -> {
                if (!youtubeDlUpdateChecked) {
                    try {
                        checkForYouTubeDlUpdate();
                        youtubeDlUpdateChecked = true;
                    } catch (Exception ex) {
                        LOGGER.log(Level.ERROR, ex);
                    }
                }
            });
        } catch (NullPointerException | IOException e) {
            LOGGER.log(Level.ERROR, "Failed to load Media Player ", e);
            Alerts.loadTimeError(e);
        }

        App.updateProgress(3.0);
    }

    public static MainWindowController getInstance() {
        return instance;
    }

    public StackPane getRoot() {
        return root;
    }

    private String getYouTubeDlVersion() throws Exception {
        Process process = Runtime.getRuntime().exec(youtubeExecName + " --version");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String youtubeDLVersion = reader.readLine();
        reader.close();
        process.destroy();

        return youtubeDLVersion;
    }

    private void checkForYouTubeDlUpdate() throws Exception {
        Alert updatingYoutubeDlAlert = new Alert(Alert.AlertType.INFORMATION);
        updatingYoutubeDlAlert.initOwner(App.getStage());

        Task<Boolean> youtubeDlUpdateTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String currentVersion = getYouTubeDlVersion();

                LOGGER.log(Level.INFO, "Checking for youtubeDL update ... [Current version: " + currentVersion + "]");

                Process process = Runtime.getRuntime().exec(youtubeExecName + " -U");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

                while (true) {
                    String line = reader.readLine();

                    if (line == null) break;

                    LOGGER.log(Level.INFO, "'" + line + "'");
                    if (line.startsWith("Updating to version ")) {
                        Platform.runLater(() -> {
                            updatingYoutubeDlAlert.showAndWait();
                            if (ConsoleController.getInstance().activeProperty().get()) {
                                if (ConsoleController.getInstance().playingProperty().get()) {
                                    ConsoleController.getInstance().handlePlayPauseSong(null);
                                }
                            }
                        });

                        updateTitle("Updating youtube-dl");
                        updateMessage("Current version:- " + currentVersion);

                        writer.write("\n");
                        updateMessage("Please wait while updating youtube-dl to new version...");
                    } else if (line.startsWith("ERROR")) {
                        Platform.runLater(() -> Alerts.showErrorAlert("Unable to check for YouTube Dl Update", "Make sure you're connected to the internet\n\nYouTube Dl Output : " + line));
                    } else if (line.startsWith("youtube-dl is up-to-date ")) {
                        writer.write("\n");
                    }
                }

                String updatedVersion;
                while (process.isAlive()) {
                    try {
                        updatedVersion = getYouTubeDlVersion();
                        if (updatedVersion.compareTo(currentVersion) > 0) {
                            updateTitle("Updated youtube-dl");
                            updateMessage("youtube-dl updated\n" +
                                    "Updated version :- " + updatedVersion);
                            LOGGER.log(Level.INFO, "youtube-dl updated version :- " + updatedVersion);

                            Platform.runLater(() -> {
                                if (ConsoleController.getInstance().activeProperty().get()) {
                                    if (!ConsoleController.getInstance().playingProperty().get()) {
                                        ConsoleController.getInstance().handlePlayPauseSong(null);
                                    }
                                }
                            });
                            break;
                        } else if (updatedVersion.compareTo(currentVersion) == 0) {
                            break;
                        }
                        wait(1000, 0);
                    } catch (Exception ignored) {
                    }
                }

                reader.close();
                writer.close();
                process.destroy();

                return true;
            }
        };

        updatingYoutubeDlAlert.titleProperty().bind(youtubeDlUpdateTask.titleProperty());
        updatingYoutubeDlAlert.setHeaderText("Updating youtube downloader (youtube-dl)");
        updatingYoutubeDlAlert.contentTextProperty().bind(youtubeDlUpdateTask.messageProperty());
        updatingYoutubeDlAlert.setOnCloseRequest(event -> {
            if (youtubeDlUpdateTask.isRunning()) {
                event.consume();
            }
        });

        new Thread(youtubeDlUpdateTask, "youtube-dl update Thread").start();
    }

}
