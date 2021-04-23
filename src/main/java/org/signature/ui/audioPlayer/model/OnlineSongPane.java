package org.signature.ui.audioPlayer.model;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import org.signature.dataModel.audioPlayer.OnlineSong;
import org.signature.ui.audioPlayer.ConsoleController;
import org.signature.ui.audioPlayer.dialogs.AddToListDialogController;
import org.signature.ui.audioPlayer.dialogs.DownloadsController;
import org.signature.ui.audioPlayer.tabs.BrowseOnlineTabController;
import org.signature.util.Utils;

import java.util.ArrayList;

public class OnlineSongPane extends HBox {
    private final OnlineSong onlineSong;

    private final JFXButton downloadSong;

    public OnlineSongPane(OnlineSong onlineSong) {
        this.onlineSong = onlineSong;

        setAlignment(Pos.CENTER_LEFT);

        ImageView thumbnailImgView = new ImageView(new Image(onlineSong.getThumbnailURL()));
        thumbnailImgView.setFitHeight(90.0);
        thumbnailImgView.setFitWidth(120.0);

        Label songLength;
        if (onlineSong.isPlaylist()) {
            songLength = new Label("");
            songLength.setStyle("-fx-background-color: TRANSPARENT");
        } else {
            songLength = new Label(Utils.getDuration((long) onlineSong.getLength()));
            songLength.setFont(Font.font("Roboto", 10));
            songLength.setStyle("-fx-background-color: BLACK; -fx-text-fill: WHITE");
        }

        AnchorPane thumbnailBox = new AnchorPane(thumbnailImgView, songLength);
        thumbnailBox.setPrefSize(120.0, 90.0);
        AnchorPane.setRightAnchor(songLength, 4.0);
        AnchorPane.setBottomAnchor(songLength, 4.0);

        Label songTitle = new Label(onlineSong.getTitle());
        songTitle.setFont(Font.font("Roboto", 15));

        Label channelName = new Label(onlineSong.getChannelName());
        channelName.setFont(Font.font("Roboto", 15));

        VBox detailBox = new VBox(songTitle, channelName);
        detailBox.setSpacing(5.0);

        HBox videoBox = new HBox(thumbnailBox, detailBox);
        videoBox.setSpacing(10.0);
        videoBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(videoBox, Priority.ALWAYS);

        videoBox.setOnMouseClicked(event -> handlePlayMedia());
        videoBox.setOnKeyPressed(event -> handlePlayMedia());

        JFXButton addToPlaylist = new JFXButton();
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M20 6h-8l-2-2H4c-1.11 0-1.99.89-1.99 2L2 18c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-1 8h-3v3h-2v-3h-3v-2h3V9h2v3h3v2z");
        svgPath.setFill(Color.GREEN);
        addToPlaylist.setStyle("-fx-background-color: TRANSPARENT");
        addToPlaylist.setRipplerFill(Color.LIGHTGRAY);
        addToPlaylist.setGraphic(svgPath);
        addToPlaylist.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        addToPlaylist.setOnAction(event -> handleAddToPlaylist());

        this.downloadSong = new JFXButton();
        svgPath = new SVGPath();
        svgPath.setContent("M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z");
        svgPath.setFill(Color.GREEN);
        this.downloadSong.setStyle("-fx-background-color: TRANSPARENT");
        this.downloadSong.setRipplerFill(Color.GREEN);
        this.downloadSong.setGraphic(svgPath);
        this.downloadSong.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.downloadSong.setOnAction(this::handleDownloadSong);

        VBox controlsBox = new VBox(this.downloadSong, addToPlaylist);
        controlsBox.setAlignment(Pos.CENTER);
        controlsBox.setSpacing(10.0);

        getChildren().setAll(videoBox, controlsBox);

        if (onlineSong.isPlaying()) {
            setStyle("-fx-background-color: rgba(72, 133, 237, 0.4);");
        }
        onlineSong.playingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                setStyle("-fx-background-color: rgba(72, 133, 237, 0.4);");
            } else {
                setStyle(null);
            }
        });
    }

    public OnlineSong getSong() {
        return this.onlineSong;
    }

    public String getTitle() {
        return this.onlineSong.getTitle();
    }

    public void setDownloadDisable(boolean value) {
        this.downloadSong.setDisable(value);
    }

    public boolean isDownloadDisable() {
        return this.downloadSong.isDisable();
    }

    private void handlePlayMedia() {
        if (onlineSong.isPlaylist()) {
            new Thread(() -> {
                try {
                    Platform.runLater(() -> ConsoleController.getInstance().playlistLoadProgress(true));
                    ArrayList<OnlineSong> onlineSongs = new ArrayList<>(BrowseOnlineTabController.getInstance().getSongsInYouTubePublicPlaylist(onlineSong.getURL()));
                    Platform.runLater(() -> {
                        ConsoleController.getInstance().setOnlineSongListLoaded(false);
                        ConsoleController.getInstance().loadOnlineSong(onlineSongs, onlineSongs.get(0));
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }, "Load youtube playlist Thread").start();
        } else {
            if (BrowseOnlineTabController.getInstance().isListUpdated()) {
                ConsoleController.getInstance().setOnlineSongListLoaded(false);
                BrowseOnlineTabController.getInstance().setListUpdated(false);
            }
            ConsoleController.getInstance().loadOnlineSong(BrowseOnlineTabController.getInstance().getOnlineSongSearchResultList(), onlineSong);
        }
    }

    private void handleAddToPlaylist() {
        if (onlineSong.isPlaylist()) {
            BrowseOnlineTabController.getInstance().saveYouTubePlaylistSongsToLocalPlaylist(onlineSong.getURL(), null);
        } else {
            AddToListDialogController.getInstance().load(onlineSong);
        }
    }

    private void handleDownloadSong(ActionEvent actionEvent) {
        DownloadsController.getInstance().addToDownloads(onlineSong, this.downloadSong);
    }
}
