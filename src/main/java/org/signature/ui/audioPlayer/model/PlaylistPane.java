package org.signature.ui.audioPlayer.model;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import org.signature.dataModel.audioPlayer.Playlist;
import org.signature.ui.audioPlayer.tabs.PlaylistTabController;
import org.signature.util.Utils;

public class PlaylistPane extends VBox {

    private final AnchorPane playlistArtPane = new AnchorPane();
    private final Pane colorPane1 = new Pane();
    private final Pane colorPane2 = new Pane();
    private final ImageView playlistArt = new ImageView();
    private final JFXButton btn_favourite = new JFXButton();
    private final JFXButton btn_play = new JFXButton();
    private final JFXButton btn_addToNowPlaying = new JFXButton();
    private final Label playlistName = new Label();
    private final Label totalSongs = new Label();

    private boolean isFavourite = false;

    public PlaylistPane(Playlist playlist) {
        setPrefSize(200.0, 226.0);
        setMinSize(200.0, 226.0);
        setMaxSize(200.0, 226.0);
        setAlignment(Pos.TOP_CENTER);
        setSpacing(6.0);
        setFocusTraversable(true);
        getStylesheets().add(PlaylistPane.class.getResource("model-node-style.css").toString());

        playlistArtPane.setFocusTraversable(true);
        playlistArtPane.setMinSize(200.0, 135.0);
        playlistArtPane.setPrefSize(200.0, 135.0);
        playlistArtPane.setMaxSize(200.0, 135.0);
        playlistArtPane.setStyle("-fx-background-color: linear-gradient(to top, Gray, white);");
        PlaylistPane.setVgrow(playlistArtPane, Priority.NEVER);

        colorPane1.setMinSize(66.0, 66.0);
        colorPane1.setPrefSize(66.0, 66.0);
        colorPane1.setMaxSize(66.0, 66.0);
        colorPane1.setStyle("-fx-background-color: DARKGRAY;");
        AnchorPane.setTopAnchor(colorPane1, 17.0);
        AnchorPane.setBottomAnchor(colorPane1, 52.0);
        AnchorPane.setLeftAnchor(colorPane1, 68.0);
        AnchorPane.setRightAnchor(colorPane1, 65.0);

        colorPane2.setMinSize(78.0, 78.0);
        colorPane2.setPrefSize(78.0, 78.0);
        colorPane2.setMaxSize(78.0, 78.0);
        colorPane2.setStyle("-fx-background-color: GRAY;");
        AnchorPane.setTopAnchor(colorPane2, 20.0);
        AnchorPane.setBottomAnchor(colorPane2, 37.0);
        AnchorPane.setLeftAnchor(colorPane2, 62.0);
        AnchorPane.setRightAnchor(colorPane2, 60.0);

        playlistArt.setFitWidth(94.0);
        playlistArt.setFitHeight(94.0);
        playlistArt.setPickOnBounds(true);
        playlistArt.setImage(new Image(PlaylistPane.class.getResourceAsStream("playlist_white.png")));
        AnchorPane.setTopAnchor(playlistArt, 24.0);
        AnchorPane.setBottomAnchor(playlistArt, 18.0);
        AnchorPane.setLeftAnchor(playlistArt, 54.0);
        AnchorPane.setRightAnchor(playlistArt, 52.0);

        btn_favourite.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn_favourite.setMinSize(40.0, 40.0);
        btn_favourite.setPrefSize(40.0, 40.0);
        btn_favourite.setMaxSize(40.0, 40.0);
        btn_favourite.getStyleClass().add("btn-controls");
        btn_favourite.setVisible(false);
        SVGPath btn_favourite_icon = new SVGPath();
        btn_favourite_icon.setContent("M16.5 3c-1.74 0-3.41.81-4.5 2.09C10.91 3.81 9.24 3 7.5 3 4.42 3 2 5.42 2 8.5c0 3.78 3.4 6.86 8.55 11.54L12 21.35l1.45-1.32C18.6 15.36 22 12.28 22 8.5 22 5.42 19.58 3 16.5 3zm-4.4 15.55l-.1.1-.1-.1C7.14 14.24 4 11.39 4 8.5 4 6.5 5.5 5 7.5 5c1.54 0 3.04.99 3.57 2.36h1.87C13.46 5.99 14.96 5 16.5 5c2 0 3.5 1.5 3.5 3.5 0 2.89-3.14 5.74-7.9 10.05z");
        btn_favourite_icon.setScaleX(1.2);
        btn_favourite_icon.setScaleY(1.2);
        btn_favourite.setGraphic(btn_favourite_icon);
        AnchorPane.setTopAnchor(btn_favourite, 28.0);
        AnchorPane.setBottomAnchor(btn_favourite, 67.0);
        AnchorPane.setLeftAnchor(btn_favourite, 80.0);
        AnchorPane.setRightAnchor(btn_favourite, 80.0);

        btn_play.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn_play.setMinSize(40.0, 40.0);
        btn_play.setPrefSize(40.0, 40.0);
        btn_play.setMaxSize(40.0, 40.0);
        btn_play.getStyleClass().add("btn-controls");
        btn_play.setVisible(false);
        SVGPath btn_play_icon = new SVGPath();
        btn_play_icon.setContent("M8 5v14l11-7z");
        btn_play_icon.setScaleX(1.2);
        btn_play_icon.setScaleY(1.2);
        btn_play.setGraphic(btn_play_icon);
        AnchorPane.setTopAnchor(btn_play, 67.0);
        AnchorPane.setBottomAnchor(btn_play, 28.0);
        AnchorPane.setLeftAnchor(btn_play, 40.0);
        AnchorPane.setRightAnchor(btn_play, 120.0);

        btn_addToNowPlaying.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn_addToNowPlaying.setMinSize(40.0, 40.0);
        btn_addToNowPlaying.setPrefSize(40.0, 40.0);
        btn_addToNowPlaying.setMaxSize(40.0, 40.0);
        btn_addToNowPlaying.getStyleClass().add("btn-controls");
        btn_addToNowPlaying.setVisible(false);
        SVGPath btn_addToPlaylist_icon = new SVGPath();
        btn_addToPlaylist_icon.setContent("M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z");
        btn_addToPlaylist_icon.setScaleX(1.2);
        btn_addToPlaylist_icon.setScaleY(1.2);
        btn_addToNowPlaying.setGraphic(btn_addToPlaylist_icon);
        AnchorPane.setTopAnchor(btn_addToNowPlaying, 67.0);
        AnchorPane.setBottomAnchor(btn_addToNowPlaying, 28.0);
        AnchorPane.setLeftAnchor(btn_addToNowPlaying, 120.0);
        AnchorPane.setRightAnchor(btn_addToNowPlaying, 40.0);

        playlistArtPane.getChildren().addAll(colorPane1, colorPane2, playlistArt, btn_favourite, btn_play, btn_addToNowPlaying);

        this.playlistName.setAlignment(Pos.TOP_LEFT);
        this.playlistName.setMaxWidth(Double.MAX_VALUE);
        this.playlistName.setWrapText(true);
        this.playlistName.setFont(Font.font("Roboto", 16.0));
        this.playlistName.setText(playlist.getPlaylistName());
        PlaylistPane.setVgrow(this.playlistName, Priority.ALWAYS);

        this.totalSongs.setAlignment(Pos.TOP_LEFT);
        this.totalSongs.setMaxWidth(Double.MAX_VALUE);
        this.totalSongs.setWrapText(true);
        this.totalSongs.setFont(Font.font("Roboto Light", 14.0));
        this.totalSongs.setText(String.valueOf(playlist.getTotalSongs()));
        PlaylistPane.setVgrow(this.totalSongs, Priority.ALWAYS);

        getChildren().addAll(this.playlistArtPane, this.playlistName, this.totalSongs);
        init();
    }

    private void init() {
        this.setOnMouseEntered(event -> {
            playlistArtPane.setEffect(Utils.createNodeHoverEffect());
            btn_favourite.setVisible(true);
            btn_play.setVisible(true);
            btn_addToNowPlaying.setVisible(true);
        });

        this.setOnMouseExited(event -> {
            playlistArtPane.setEffect(null);
            btn_favourite.setVisible(false);
            btn_play.setVisible(false);
            btn_addToNowPlaying.setVisible(false);
        });

        playlistName.heightProperty().addListener((observable, oldValue, newValue) -> PlaylistTabController.getInstance().setPlaylistNameFieldHeight(newValue.intValue()));

        playlistName.prefHeightProperty().bind(PlaylistTabController.getInstance().playlistNameFieldHeightProperty());

        btn_favourite.setOnAction(this::handleSetFavourite);
    }

    private void handleSetFavourite(ActionEvent actionEvent) {
        if (isFavourite) {
            SVGPath nFavourite = new SVGPath();
            nFavourite.setContent("M16.5 3c-1.74 0-3.41.81-4.5 2.09C10.91 3.81 9.24 3 7.5 3 4.42 3 2 5.42 2 8.5c0 3.78 3.4 6.86 8.55 11.54L12 21.35l1.45-1.32C18.6 15.36 22 12.28 22 8.5 22 5.42 19.58 3 16.5 3zm-4.4 15.55l-.1.1-.1-.1C7.14 14.24 4 11.39 4 8.5 4 6.5 5.5 5 7.5 5c1.54 0 3.04.99 3.57 2.36h1.87C13.46 5.99 14.96 5 16.5 5c2 0 3.5 1.5 3.5 3.5 0 2.89-3.14 5.74-7.9 10.05z");
            btn_favourite.setGraphic(nFavourite);
            isFavourite = false;
        } else {
            SVGPath yFavourite = new SVGPath();
            yFavourite.setContent("M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z");
            yFavourite.setFill(Color.rgb(219, 50, 54, 0.8));
            btn_favourite.setGraphic(yFavourite);
            isFavourite = true;
        }
    }
}
