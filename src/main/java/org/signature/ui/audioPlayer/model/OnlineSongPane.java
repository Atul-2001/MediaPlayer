package org.signature.ui.audioPlayer.model;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
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
import org.signature.ui.audioPlayer.dialogs.DownloadsController;
import org.signature.ui.audioPlayer.tabs.BrowseOnlineTabController;

import java.util.ArrayList;

public class OnlineSongPane extends HBox {
    private final OnlineSong onlineSong;

    private ImageView thumbnailImgView;
    private Label songTitle;
    private Label channelName;
    private Label songLength;
    private JFXButton addToPlaylist;
    private JFXButton downloadSong;

    public OnlineSongPane(OnlineSong onlineSong) {
        this.onlineSong = onlineSong;

        setAlignment(Pos.CENTER_LEFT);

        this.thumbnailImgView = new ImageView(new Image(onlineSong.getThumbnailURL()));
        this.thumbnailImgView.setFitHeight(90.0);
        this.thumbnailImgView.setFitWidth(120.0);

        if (onlineSong.isPlaylist()) {
            this.songLength = new Label("");
            this.songLength.setStyle("-fx-background-color: TRANSPARENT");
        } else {
            this.songLength = new Label(onlineSong.getDuration());
            this.songLength.setFont(Font.font("Roboto", 10));
            this.songLength.setStyle("-fx-background-color: BLACK; -fx-text-fill: WHITE");
        }

        AnchorPane thumbnailBox = new AnchorPane(this.thumbnailImgView, this.songLength);
        thumbnailBox.setPrefSize(120.0, 90.0);
        AnchorPane.setRightAnchor(this.songLength, 4.0);
        AnchorPane.setBottomAnchor(this.songLength, 4.0);

        this.songTitle = new Label(onlineSong.getTitle());
        this.songTitle.setFont(Font.font("Roboto", 15));

        this.channelName = new Label(onlineSong.getChannelName());
        this.channelName.setFont(Font.font("Roboto", 15));

        VBox detailBox = new VBox(this.songTitle, this.channelName);
        detailBox.setSpacing(5.0);

        HBox videoBox = new HBox(thumbnailBox, detailBox);
        videoBox.setSpacing(10.0);
        videoBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(videoBox, Priority.ALWAYS);

        videoBox.setOnMouseClicked(event -> handlePlayMedia());
        videoBox.setOnKeyPressed(event -> handlePlayMedia());

        this.addToPlaylist = new JFXButton();
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M20 6h-8l-2-2H4c-1.11 0-1.99.89-1.99 2L2 18c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-1 8h-3v3h-2v-3h-3v-2h3V9h2v3h3v2z");
        svgPath.setFill(Color.GREEN);
        this.addToPlaylist.setStyle("-fx-background-color: TRANSPARENT");
        this.addToPlaylist.setRipplerFill(Color.LIGHTGRAY);
        this.addToPlaylist.setGraphic(svgPath);
        this.addToPlaylist.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.addToPlaylist.setOnAction(event -> handleAddToPlaylist());

        this.downloadSong = new JFXButton();
        svgPath = new SVGPath();
        svgPath.setContent("M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z");
        svgPath.setFill(Color.GREEN);
        this.downloadSong.setStyle("-fx-background-color: TRANSPARENT");
        this.downloadSong.setRipplerFill(Color.GREEN);
        this.downloadSong.setGraphic(svgPath);
        this.downloadSong.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.downloadSong.setOnAction(this::handleDownloadSong);

        VBox controlsBox = new VBox(this.downloadSong, this.addToPlaylist);
        controlsBox.setAlignment(Pos.CENTER);
        controlsBox.setSpacing(10.0);

        getChildren().setAll(videoBox, controlsBox);
    }

    public OnlineSongPane(OnlineSong onlineSong, String type) {
        this.onlineSong = onlineSong;

        setAlignment(Pos.CENTER_LEFT);
        setMinHeight(50.0);
        setPrefHeight(50.0);
        setMaxHeight(50.0);
        getStylesheets().add(SongPane.class.getResource("model-node-style.css").toString());

        this.songTitle = new Label(onlineSong.getTitle());
        this.songTitle.setFont(Font.font("Roboto", 15));
        this.songTitle.setMinWidth(360.0);
        this.songTitle.setPrefWidth(360.0);
        this.songTitle.setMaxWidth(360.0);

        this.channelName = new Label(onlineSong.getChannelName());
        this.channelName.setFont(Font.font("Roboto", 15));

//            this.songLength = new Label(song.getDuration());
//            this.songLength.setFont(Font.font("Roboto", 10));

        HBox detailBox = new HBox(this.songTitle, this.channelName/*, this.songLength*/);
        detailBox.setSpacing(10.0);
        detailBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(detailBox, Priority.ALWAYS);
        HBox.setMargin(detailBox, new Insets(0, 62.0, 0, 0));

        detailBox.setOnMouseClicked(event -> handlePlayMedia());
        detailBox.setOnKeyPressed(event -> handlePlayMedia());

        this.addToPlaylist = new JFXButton();
        SVGPath svgPath = new SVGPath();
        svgPath.setContent("M20 6h-8l-2-2H4c-1.11 0-1.99.89-1.99 2L2 18c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-1 8h-3v3h-2v-3h-3v-2h3V9h2v3h3v2z");
        svgPath.setFill(Color.GREEN);
        this.addToPlaylist.setStyle("-fx-background-color: TRANSPARENT");
        this.addToPlaylist.setRipplerFill(Color.LIGHTGRAY);
        this.addToPlaylist.setGraphic(svgPath);
        this.addToPlaylist.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.addToPlaylist.setOnAction(event -> handleAddToPlaylist());

        this.downloadSong = new JFXButton();
        svgPath = new SVGPath();
        svgPath.setContent("M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z");
        svgPath.setFill(Color.GREEN);
        this.downloadSong.setStyle("-fx-background-color: TRANSPARENT");
        this.downloadSong.setRipplerFill(Color.GREEN);
        this.downloadSong.setGraphic(svgPath);
        this.downloadSong.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.downloadSong.setOnAction(this::handleDownloadSong);

        HBox controlsBox = new HBox(this.downloadSong, this.addToPlaylist);
        controlsBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(controlsBox, Priority.NEVER);
        controlsBox.setSpacing(6.0);

        getChildren().setAll(detailBox, controlsBox);
    }

    public void setDownloadDisable(boolean value) {
        this.downloadSong.setDisable(value);
    }

    public boolean isDownloadDisable() {
        return this.downloadSong.isDisable();
    }

    private void handlePlayMedia() {
        if (onlineSong.isPlaylist()) {
            ArrayList<OnlineSong> onlineSongs = new ArrayList<>();
            try {
                onlineSongs = BrowseOnlineTabController.getInstance().getSongsInYouTubePublicPlaylist(onlineSong.getURL());
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
            ConsoleController.getInstance().setOnlineSongListLoaded(false);
            ConsoleController.getInstance().loadOnlineSong(onlineSongs, onlineSongs.get(0));
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
            new BrowseOnlineTabController().saveYouTubePlaylistSongsToLocalPlaylist(onlineSong.getURL());
        } else {
            ConsoleController.getInstance().addToNowPlaying(onlineSong);
        }
    }

    private void handleDownloadSong(ActionEvent actionEvent) {
        DownloadsController.getInstance().addToDownloads(onlineSong, this.downloadSong);
    }
}
