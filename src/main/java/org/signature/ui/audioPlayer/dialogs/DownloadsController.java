package org.signature.ui.audioPlayer.dialogs;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.Mp3File;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;
import org.signature.App;
import org.signature.dataModel.audioPlayer.OnlineSong;
import org.signature.ui.audioPlayer.AudioPlayer;
import org.signature.ui.audioPlayer.Inventory;
import org.signature.util.Alerts;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DownloadsController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(DownloadsController.class);

    private static DownloadsController instance = null;

    @FXML
    private VBox downloads_dialog;
    @FXML
    private VBox downloadsList;

    private int noOfDownloads = 0;

    private String youtubeExec = "youtube-dl.exe";
    private boolean isUnix = false;

    private static final ExecutorService downloadServicePool = Executors.newCachedThreadPool(new ThreadFactory() {
        final AtomicInteger counter = new AtomicInteger();

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "Download Service Pool--Thread-" + counter.incrementAndGet());
        }
    });

    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("linux") || osName.contains("mac"))
            isUnix = true;

        if (isUnix) youtubeExec = "./youtube-dl";

        this.stage = new Stage();
        this.stage.setScene(new Scene(downloads_dialog));
        this.stage.setTitle("Downloads");

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        this.stage.setX(bounds.getMinX());
        this.stage.setY(bounds.getMinY());
        this.stage.setMinWidth(bounds.getWidth() * 0.75);
        this.stage.setMinHeight(bounds.getHeight() * 0.75);
        this.stage.setWidth(bounds.getWidth() * 0.75);
        this.stage.setHeight(bounds.getHeight() * 0.75);

        App.getStage().setOnCloseRequest(event -> {
            if (this.stage.isShowing()) {
                this.stage.close();
            }
        });
    }

    public static DownloadsController getInstance() {
        return instance;
    }

    public VBox getRoot() {
        return this.downloads_dialog;
    }

    public static ExecutorService getDownloadServicePool() {
        return downloadServicePool;
    }

    public boolean isShowing() {
        return this.stage.isShowing();
    }

    public void showDownloads() {
        if (!this.stage.isShowing()) {
            this.stage.show();
        }
        this.stage.toFront();
    }

    public void addToDownloads(OnlineSong onlineSong, JFXButton button) {
        downloadServicePool.submit(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String refinedTitle = onlineSong.getTitle().replace("|", "&").replace("\\", "&").replace("/", "&").replace(":", "-");

                Platform.runLater(() -> button.setDisable(true));


                ImageView thumbnailImageView = new ImageView();
                if (onlineSong.getThumbnailURL() != null) {
                    thumbnailImageView.setImage(new Image(onlineSong.getThumbnailURL()));
                } else if (onlineSong.getThumbnail() != null) {
                    thumbnailImageView.setImage(SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(onlineSong.getThumbnail())), null));
                }
                thumbnailImageView.setFitHeight(90);
                thumbnailImageView.setFitWidth(120);

                Label titleLabel = new Label(onlineSong.getTitle());
                titleLabel.setFont(Font.font("Roboto", 25));
                titleLabel.setPadding(new Insets(0, 0, 10, 0));
                titleLabel.setMaxWidth(Double.MAX_VALUE);

                Label channelTitleLabel = new Label(onlineSong.getChannelName());
                channelTitleLabel.setFont(Font.font("Roboto", 15));
                channelTitleLabel.setMaxWidth(Double.MAX_VALUE);

                Label statusLabel = new Label("Downloading ...");
                statusLabel.setFont(Font.font("Roboto", 15));

                JFXProgressBar progressBar = new JFXProgressBar(ProgressIndicator.INDETERMINATE_PROGRESS);
                progressBar.setCache(true);
                progressBar.setCacheHint(CacheHint.SPEED);
                progressBar.setMaxWidth(Double.MAX_VALUE);
                progressBar.setPrefHeight(4.0);

                VBox downloadInfoPane = new VBox(titleLabel, channelTitleLabel, statusLabel, progressBar);
                downloadInfoPane.setPadding(new Insets(2, 2, 2, 2));
                downloadInfoPane.setSpacing(5);
                downloadInfoPane.setFillWidth(true);
                downloadInfoPane.setCache(true);
                downloadInfoPane.setCacheHint(CacheHint.SPEED);
                HBox.setHgrow(downloadInfoPane, Priority.ALWAYS);
                downloadInfoPane.setMaxWidth(Double.MAX_VALUE);

                HBox downloadNode = new HBox(thumbnailImageView, downloadInfoPane);
                downloadNode.setSpacing(10);
                downloadNode.setPadding(new Insets(25));
                downloadNode.getStylesheets().add(DownloadsController.class.getResource("dialog-style.css").toString());
                downloadNode.getStyleClass().add("card");
                downloadNode.setMaxHeight(Double.MAX_VALUE);

                Platform.runLater(() -> downloadsList.getChildren().add(downloadNode));

                if (stage.isShowing()) {
                    showDownloads();
                } else {
                    Notifications notificationsBuilder = Notifications.create()
                            .title("Download Started")
                            .text("Downloading file: " + refinedTitle)
                            .graphic(null)
                            .hideAfter(Duration.seconds(20))
                            .position(Pos.TOP_RIGHT)
                            .onAction(event -> showDownloads());
                    Platform.runLater(notificationsBuilder::showInformation);
                }

                try {
                    String output = Inventory.getDefaultMusicFolder().concat(File.separator).concat("audio_download").concat("" + noOfDownloads).concat(".%(ext)s");
                    String cmd = youtubeExec + " -o " + output + " --extract-audio --audio-format mp3 " + onlineSong.getURL();

                    Process process = Runtime.getRuntime().exec(cmd);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    while (true) {
                        String newLine = reader.readLine();
                        if (newLine == null) break;
                        //[download] 100.0%

                        if (newLine.startsWith("[download]")) {
                            String progressStr = newLine.substring(10, 16).replace(" ", "");

                            if (!progressStr.contains("Destination") && !progressStr.contains("%") && !progressStr.contains("Resum")) {
                                try {
                                    double d = Double.parseDouble(progressStr);
                                    Platform.runLater(() -> progressBar.setProgress(d / 100));
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        } else if (newLine.startsWith("[ffmpeg]")) {
                            Platform.runLater(() -> statusLabel.setText("Converting ..."));
                        }
                    }

                    reader.close();

                    Platform.runLater(() -> statusLabel.setText("Finishing Up ..."));

                    String tmpOutputFile = Inventory.getDefaultMusicFolder().concat(File.separator).concat("audio_download").concat("" + noOfDownloads).concat(".mp3");
                    File tmpAudioFile = new File(tmpOutputFile);

                    ID3v2 id3v2Tag = new ID3v24Tag();
                    Mp3File mp3File = new Mp3File(tmpAudioFile);
                    mp3File.setId3v2Tag(id3v2Tag);

                    id3v2Tag.setTitle(onlineSong.getTitle());
                    id3v2Tag.setArtist(onlineSong.getChannelName());
                    id3v2Tag.setAlbum("Youtube");
                    id3v2Tag.setAlbumArtist(onlineSong.getChannelName());
                    id3v2Tag.setGenreDescription("youtube.com");
                    id3v2Tag.setComment("Downloaded via github.com/atul-2001/media_player. Made with Love by Atul. Youtube video url : " + onlineSong.getURL());

                    if (onlineSong.getThumbnailURL() != null) {
                        Image img = new Image(onlineSong.getThumbnailURL());
                        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(img, null);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                        id3v2Tag.setAlbumImage(byteArrayOutputStream.toByteArray(), "YTMusic");
                    } else if (onlineSong.getThumbnail() != null) {
                        id3v2Tag.setAlbumImage(onlineSong.getThumbnail(), "YTMusic");
                    }

                    String outputFile = Inventory.getDefaultMusicFolder() + File.separator + refinedTitle + ".mp3";
                    mp3File.save(outputFile);

                    tmpAudioFile.delete();

                    Notifications notificationsBuilder = Notifications.create()
                            .title("Download Completed")
                            .text("Saved to : " + outputFile)
                            .graphic(null)
                            .hideAfter(Duration.seconds(15))
                            .position(Pos.TOP_RIGHT);
                    
                    Platform.runLater(() -> {
                        progressBar.setProgress(1);
                        statusLabel.setText("Downloaded!");
                        notificationsBuilder.showInformation();
                        
                        try {
                            Path path = Path.of(new File(outputFile).toURI());
                            int libraryID = Inventory.getLibraryId(path.getParent().toString());
                            if (libraryID != 0) {
                                BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                                AudioPlayer.getInstance().addSong(path, attrs, libraryID, Inventory.getCachedAlbums().get(0), Inventory.getCachedArtists().get(0));
                            }
                        } catch (IOException ex) {
                            LOGGER.log(Level.ERROR, "Error while adding downloaded song to the list! ", ex);
                        }
                    });

                } catch (Exception ex) {
                    Notifications notificationsBuilder = Notifications.create()
                            .title("Download Failed")
                            .text("Unable to download! Check Stacktrace!")
                            .graphic(null)
                            .hideAfter(Duration.seconds(15))
                            .position(Pos.TOP_RIGHT);

                    Platform.runLater(() -> {
                        statusLabel.setText("Unable to download! Check Stacktrace!");
                        progressBar.setProgress(1);
                        notificationsBuilder.showError();
                        Alerts.showErrorAlert("Error!", "Unable to download! Check Stacktrace!");
                    });

                    LOGGER.log(Level.ERROR, "Download Failed Error! ", ex);
                    noOfDownloads--;
                } finally {
                    new File(refinedTitle + "_pass_.mp3").delete();
                    new File(refinedTitle + "_pass_.webm").delete();
                }

                return null;
            }
        });

        noOfDownloads++;
    }
}
