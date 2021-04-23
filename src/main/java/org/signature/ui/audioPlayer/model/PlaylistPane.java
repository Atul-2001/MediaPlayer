package org.signature.ui.audioPlayer.model;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import org.signature.dataModel.audioPlayer.Playlist;
import org.signature.ui.audioPlayer.BaseController;
import org.signature.ui.audioPlayer.ConsoleController;
import org.signature.ui.audioPlayer.dialogs.AddToListDialogController;
import org.signature.ui.audioPlayer.tabs.PlaylistTabController;
import org.signature.ui.audioPlayer.tabs.PlaylistViewTabController;
import org.signature.ui.audioPlayer.tabs.RecentlyPlayedTabController;
import org.signature.util.Utils;

public class PlaylistPane extends VBox {

    private static final String DEFAULT_PLAYLIST_ART = "playlist_white.png";

    private final AnchorPane playlistArtPane = new AnchorPane();
    private final JFXButton btn_play = new JFXButton();
    private final JFXButton btn_addToPlaylist = new JFXButton();
    private final Label playlistName = new Label();
    private final Label totalSongs = new Label();

    private final Playlist playlist;

    public PlaylistPane(Playlist playlist) {
        this.playlist = playlist;

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

        Pane colorPane1 = new Pane();
        colorPane1.setMinSize(66.0, 66.0);
        colorPane1.setPrefSize(66.0, 66.0);
        colorPane1.setMaxSize(66.0, 66.0);
        colorPane1.setStyle("-fx-background-color: DARKGRAY;");
        AnchorPane.setTopAnchor(colorPane1, 17.0);
        AnchorPane.setBottomAnchor(colorPane1, 52.0);
        AnchorPane.setLeftAnchor(colorPane1, 68.0);
        AnchorPane.setRightAnchor(colorPane1, 65.0);

        Pane colorPane2 = new Pane();
        colorPane2.setMinSize(78.0, 78.0);
        colorPane2.setPrefSize(78.0, 78.0);
        colorPane2.setMaxSize(78.0, 78.0);
        colorPane2.setStyle("-fx-background-color: GRAY;");
        AnchorPane.setTopAnchor(colorPane2, 20.0);
        AnchorPane.setBottomAnchor(colorPane2, 37.0);
        AnchorPane.setLeftAnchor(colorPane2, 62.0);
        AnchorPane.setRightAnchor(colorPane2, 60.0);

        ImageView playlistArt = new ImageView();
        playlistArt.setFitWidth(94.0);
        playlistArt.setFitHeight(94.0);
        playlistArt.setPickOnBounds(true);
        playlistArt.setImage(getDefaultPlaylistArt());
        AnchorPane.setTopAnchor(playlistArt, 24.0);
        AnchorPane.setBottomAnchor(playlistArt, 18.0);
        AnchorPane.setLeftAnchor(playlistArt, 54.0);
        AnchorPane.setRightAnchor(playlistArt, 52.0);

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
        AnchorPane.setTopAnchor(btn_play, 47.5);
        AnchorPane.setBottomAnchor(btn_play, 47.5);
        AnchorPane.setLeftAnchor(btn_play, 55.0);
        AnchorPane.setRightAnchor(btn_play, 105.0);

        btn_addToPlaylist.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn_addToPlaylist.setMinSize(40.0, 40.0);
        btn_addToPlaylist.setPrefSize(40.0, 40.0);
        btn_addToPlaylist.setMaxSize(40.0, 40.0);
        btn_addToPlaylist.getStyleClass().add("btn-controls");
        btn_addToPlaylist.setVisible(false);
        SVGPath btn_addToPlaylist_icon = new SVGPath();
        btn_addToPlaylist_icon.setContent("M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z");
        btn_addToPlaylist_icon.setScaleX(1.2);
        btn_addToPlaylist_icon.setScaleY(1.2);
        btn_addToPlaylist.setGraphic(btn_addToPlaylist_icon);
        AnchorPane.setTopAnchor(btn_addToPlaylist, 47.5);
        AnchorPane.setBottomAnchor(btn_addToPlaylist, 47.5);
        AnchorPane.setLeftAnchor(btn_addToPlaylist, 105.0);
        AnchorPane.setRightAnchor(btn_addToPlaylist, 55.0);

        playlistArtPane.getChildren().addAll(colorPane1, colorPane2, playlistArt, btn_play, btn_addToPlaylist);

        this.playlistName.setAlignment(Pos.TOP_LEFT);
        this.playlistName.setMaxWidth(Double.MAX_VALUE);
        this.playlistName.setWrapText(true);
        this.playlistName.setFont(Font.font("Roboto", 16.0));
        this.playlistName.textProperty().bind(playlist.playlistNameProperty());
//        this.playlistName.setText(playlist.getPlaylistName());
        PlaylistPane.setVgrow(this.playlistName, Priority.ALWAYS);

        this.totalSongs.setAlignment(Pos.TOP_LEFT);
        this.totalSongs.setMaxWidth(Double.MAX_VALUE);
        this.totalSongs.setWrapText(true);
        this.totalSongs.setFont(Font.font("Roboto Light", 14.0));
        this.totalSongs.textProperty().bind(playlist.totalSongsProperty());
//        this.totalSongs.setText(String.valueOf(playlist.getTotalSongs()));
        PlaylistPane.setVgrow(this.totalSongs, Priority.ALWAYS);

        getChildren().addAll(this.playlistArtPane, this.playlistName, this.totalSongs);
        init();
    }

    private void init() {
        this.setOnMouseEntered(event -> {
            playlistArtPane.setEffect(Utils.createNodeHoverEffect());
            btn_play.setVisible(true);
            btn_addToPlaylist.setVisible(true);
        });

        this.setOnMouseExited(event -> {
            playlistArtPane.setEffect(null);
            btn_play.setVisible(false);
            btn_addToPlaylist.setVisible(false);
        });

        this.setOnMouseClicked(event -> {
            PlaylistViewTabController.getInstance().loadPlaylist(this.playlist);
            PlaylistViewTabController.getInstance().setViewCalledByPlaylistTab(true);
            PlaylistViewTabController.getInstance().setViewCalledByRecentTab(false);
            BaseController.getInstance().handleShowPlaylistView();
        });

        playlistName.heightProperty().addListener((observable, oldValue, newValue) -> PlaylistTabController.getInstance().setPlaylistNameFieldHeight(newValue.intValue()));

        playlistName.prefHeightProperty().bind(PlaylistTabController.getInstance().playlistNameFieldHeightProperty());

        btn_play.setOnAction(event -> {
            ConsoleController.getInstance().load(playlist);
            RecentlyPlayedTabController.getInstance().addRecentlyPlayed(playlist);
        });

        btn_addToPlaylist.setOnAction(event -> AddToListDialogController.getInstance().load(playlist));
    }

    public String getPlaylistName() {
        return playlistName.getText();
    }

    public String getTotalSongs() {
        return totalSongs.getText();
    }

    public static Image getDefaultPlaylistArt() {
        return new Image(PlaylistPane.class.getResourceAsStream(DEFAULT_PLAYLIST_ART));
    }
}
