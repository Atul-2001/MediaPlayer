package org.signature.ui.audioPlayer.tabs;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.dataModel.audioPlayer.Playlist;
import org.signature.dataModel.audioPlayer.PlaylistSong;
import org.signature.ui.audioPlayer.BaseController;
import org.signature.ui.audioPlayer.ConsoleController;
import org.signature.ui.audioPlayer.Inventory;
import org.signature.ui.audioPlayer.dialogs.AddToListDialogController;
import org.signature.ui.audioPlayer.model.SongPane;
import org.signature.util.Utils;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class PlaylistViewTabController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(PlaylistViewTabController.class);

    private static PlaylistViewTabController instance = null;

    private final double PLAYLIST_VIEW_HEADER_MIN_HEIGHT = 196.0;
    private final double PLAYLIST_VIEW_HEADER_MAX_HEIGHT = 274.0;

    @FXML private VBox playlist_view;
    @FXML private HBox playlistViewHeader;
    @FXML private AnchorPane playlistArtPane;
    @FXML private Pane playlistArtColor2;
    @FXML private Pane playlistArtColor1;
    @FXML private ImageView playlistArt;
    @FXML private VBox info_and_controls_pane;
    @FXML private TextField playlistName;
    @FXML private HBox playlistSizeInfoPane;
    @FXML private Label noOfSongs;
    @FXML private Label totalDuration;
    @FXML private StackPane contentStack;
    @FXML private ScrollPane playlistViewContentPane;
    @FXML private VBox playlistSongsList;

    private Playlist playlist = null;
    private String playlistOldName;
    private final ObservableList<PlaylistSong> songs = FXCollections.observableArrayList();
    AtomicLong totalSongLength = new AtomicLong(0);
    private ListChangeListener<Node> removePlaylistListener;

    private boolean isViewCalledByPlaylistTab = false;
    private boolean isViewCalledByRecentTab = false;

    private final BooleanProperty requestParentForIndex = new SimpleBooleanProperty(false);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        songs.addListener((ListChangeListener<PlaylistSong>) change -> {
            change.next();
            if (change.wasAdded()) {
                requestParentForIndex.set(false);
                for (PlaylistSong playlistSong : change.getAddedSubList()) {
                    playlistSongsList.getChildren().add(new SongPane(playlistSong, playlist));
                    totalSongLength.getAndAdd((long) playlistSong.getLength());
                }

                String[] values = Utils.getDuration(totalSongLength.get()).split(":");
                if (values.length == 2 || values.length == 1) {
                    playlist.setTotalSongDuration(values[0] + " mins");
                } else {
                    playlist.setTotalSongDuration(values[0] + " hr " + values[1] + " mins");
                }
                requestParentForIndex.set(true);
            }
        });

        playlistSongsList.getChildren().addListener((ListChangeListener<Node>) change -> {
            if (playlistSongsList.getChildren().size() > 0 && contentStack.getChildren().get(1).toString().contains("VBox")) {
                Utils.flipStackPane(contentStack);
            } else if (playlistSongsList.getChildren().size() == 0 && !contentStack.getChildren().get(1).toString().contains("VBox")) {
                Utils.flipStackPane(contentStack);
            }
        });

        removePlaylistListener = change -> {
            change.next();
            if (change.wasRemoved()) {
                if (playlistSongsList.getChildren().size() == 0) {
                    playlist.removeAllSongs();
                    songs.clear();
                    totalSongLength.set(0);
                } else {
                    for (Node node : change.getRemoved()) {
                        songs.removeIf(song -> song.getTitle().equals(((SongPane) node).getSongTitle()));
                        playlist.removeSong(((SongPane) node).getSongTitle());

                        if (((SongPane) node).getSong() != null) {
                            totalSongLength.getAndAdd(-((SongPane) node).getSong().getLength());
                        } else if (((SongPane) node).getPlaylistSong() != null) {
                            totalSongLength.getAndAdd((long) -((SongPane) node).getPlaylistSong().getLength());
                        }
                    }
                }

                String[] values = Utils.getDuration(totalSongLength.get()).split(":");
                if (values.length == 2 || values.length == 1) {
                    playlist.setTotalSongDuration(values[0] + " mins");
                } else {
                    playlist.setTotalSongDuration(values[0] + " hr " + values[1] + " mins");
                }
            }
        };

        AtomicBoolean isViewChanged = new AtomicBoolean(false);
        playlistSongsList.setOnScroll(event -> {
            if (event.getDeltaY() < 0 && !isViewChanged.get()) {
                info_and_controls_pane.getChildren().remove(playlistSizeInfoPane);
                playlistArtPane.setPrefSize(Math.round(PLAYLIST_VIEW_HEADER_MIN_HEIGHT * 0.7811), Math.round(PLAYLIST_VIEW_HEADER_MIN_HEIGHT * 0.7811));
                playlistArtColor2.setPrefSize(Math.round(PLAYLIST_VIEW_HEADER_MIN_HEIGHT * 0.584), Math.round(PLAYLIST_VIEW_HEADER_MIN_HEIGHT * 0.584));
                playlistArtColor1.setPrefSize(Math.round(PLAYLIST_VIEW_HEADER_MIN_HEIGHT * 0.658), Math.round(PLAYLIST_VIEW_HEADER_MIN_HEIGHT * 0.658));
                playlistArt.setFitHeight(Math.round(PLAYLIST_VIEW_HEADER_MIN_HEIGHT * 0.73));
                playlistArt.setFitWidth(Math.round(PLAYLIST_VIEW_HEADER_MIN_HEIGHT * 0.73));
                playlistViewHeader.setPrefHeight(PLAYLIST_VIEW_HEADER_MIN_HEIGHT);
                isViewChanged.set(true);
            } else if (event.getDeltaY() > 0 && isViewChanged.get()){
                playlistViewHeader.setPrefHeight(PLAYLIST_VIEW_HEADER_MAX_HEIGHT);
                playlistArtPane.setPrefSize(Math.round(PLAYLIST_VIEW_HEADER_MAX_HEIGHT * 0.7811), Math.round(PLAYLIST_VIEW_HEADER_MAX_HEIGHT * 0.7811));
                playlistArtColor2.setPrefSize(Math.round(PLAYLIST_VIEW_HEADER_MAX_HEIGHT * 0.584), Math.round(PLAYLIST_VIEW_HEADER_MAX_HEIGHT * 0.584));
                playlistArtColor1.setPrefSize(Math.round(PLAYLIST_VIEW_HEADER_MAX_HEIGHT * 0.658), Math.round(PLAYLIST_VIEW_HEADER_MAX_HEIGHT * 0.658));
                playlistArt.setFitHeight(Math.round(PLAYLIST_VIEW_HEADER_MAX_HEIGHT * 0.73));
                playlistArt.setFitWidth(Math.round(PLAYLIST_VIEW_HEADER_MAX_HEIGHT * 0.73));
                info_and_controls_pane.getChildren().add(1, playlistSizeInfoPane);
                isViewChanged.set(false);
            }
        });

//        LOGGER.log(Level.INFO, "Tab Playlist View Loaded!");
    }

    public static PlaylistViewTabController getInstance() {
        return instance;
    }

    public VBox getRoot() {
        return playlist_view;
    }

    @FXML
    private void handleBackOperation(ActionEvent actionEvent) {
        if (isViewCalledByPlaylistTab) {
            BaseController.getInstance().getBtnShowPlaylist().fire();
        } else if (isViewCalledByRecentTab) {
            BaseController.getInstance().getBtnRecentlyPlayed().fire();
        } else {
            BaseController.getInstance().getBtnSongs().fire();
        }
    }

    @FXML
    private void handleSaveNewName(ActionEvent actionEvent) {
        if (Inventory.getPlaylist(playlistName.getText()) == null) {
            playlistName.setEditable(false);
            playlist.setPlaylistName(playlistName.getText());
            Inventory.updatePlaylist(playlist);
        } else {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(playlist_view.getScene().getWindow());
            dialog.initStyle(StageStyle.UNDECORATED);
            dialog.setResizable(false);
            dialog.getDialogPane().setMaxWidth(500.0);

            Label header = new Label("Playlist already exist!");
            header.setFont(Font.font("Roboto", 20));
            header.setStyle("-fx-background-color: linear-gradient(to right, #badbf9 0%, #E0C3FC 42%, #f8fbc0 100%); -fx-padding: 20 0 10 20");
            dialog.getDialogPane().setHeader(header);

            Label content = new Label();
            content.setText("Playlist \"" + playlistName.getText() + "\", already exist on this device.\nPlease choose another name for your playlist.");
            content.setWrapText(true);
            content.setMaxWidth(480.0);
            content.setMaxHeight(Double.MAX_VALUE);
            content.setFont(Font.font("Roboto", 18));
            content.setStyle("-fx-background-color: linear-gradient(to right, #badbf9 0%, #E0C3FC 42%, #f8fbc0 100%); -fx-padding: 10 20 40 20");
            dialog.getDialogPane().setContent(content);

            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

            Optional<ButtonType> result = dialog.showAndWait();
            if ((result.isPresent() && result.get().equals(ButtonType.OK)) || !dialog.isShowing()) {
                playlistName.setEditable(false);
                playlist.setPlaylistName(this.playlistOldName);
            }
        }
    }

    @FXML
    private void handlePlayAllSongs(ActionEvent actionEvent) {
        ConsoleController.getInstance().load(playlist);
        RecentlyPlayedTabController.getInstance().addRecentlyPlayed(playlist);
    }

    @FXML
    private void handleAddTo(ActionEvent actionEvent) {
        AddToListDialogController.getInstance().load(playlist);
    }

    @FXML
    private void handleRenamePlaylist(ActionEvent actionEvent) {
        playlistName.setEditable(true);
        playlistName.requestFocus();
    }

    @FXML
    private void handleDeletePlaylist(ActionEvent actionEvent) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(playlist_view.getScene().getWindow());
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setResizable(false);
        dialog.getDialogPane().setMaxWidth(500.0);

        Label header = new Label("Are you sure you want to delete this?");
        header.setFont(Font.font("Roboto", 20));
        header.setStyle("-fx-background-color: linear-gradient(to right, #badbf9 0%, #E0C3FC 42%, #f8fbc0 100%); -fx-padding: 20 0 10 20");
        dialog.getDialogPane().setHeader(header);

        Label content = new Label();
        content.setText("If you delete \"" + playlist.getPlaylistName() + "\", it won't be on this device any more.");
        content.setWrapText(true);
        content.setMaxWidth(480.0);
        content.setMaxHeight(Double.MAX_VALUE);
        content.setFont(Font.font("Roboto", 18));
        content.setStyle("-fx-background-color: linear-gradient(to right, #badbf9 0%, #E0C3FC 42%, #f8fbc0 100%); -fx-padding: 10 20 40 20");
        dialog.getDialogPane().setContent(content);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get().equals(ButtonType.OK)) {
            PlaylistTabController.getInstance().removePlaylist(this.playlist);
            if (isViewCalledByPlaylistTab) {
                BaseController.getInstance().getBtnShowPlaylist().fire();
            } else {
                BaseController.getInstance().getBtnSongs().fire();
            }
        }
    }

    @FXML
    private void handleShowMyMusic(MouseEvent mouseEvent) {
        BaseController.getInstance().getBtnAlbums().fire();
    }

    @FXML
    private void handleImportFromYoutube() {
        BrowseOnlineTabController.getInstance().saveYouTubePlaylistSongsToLocalPlaylist(null, playlist);
        this.songs.addAll(playlist.getSongList());
    }

    public void setViewCalledByPlaylistTab(boolean value) {
        this.isViewCalledByPlaylistTab = value;
    }

    public void setViewCalledByRecentTab(boolean value) {
        this.isViewCalledByRecentTab = value;
    }

    public boolean loadPlaylist(Playlist playlist) {
        try {
            this.playlist = playlist;
            this.playlistOldName = playlist.getPlaylistName();

            noOfSongs.textProperty().unbind();
            totalDuration.textProperty().unbind();

            playlistName.setText(playlist.getPlaylistName());
            noOfSongs.textProperty().bind(playlist.totalSongsProperty());
            totalDuration.textProperty().bind(playlist.totalSongDurationProperty());

            this.totalSongLength.set(0);
            this.songs.clear();
            this.playlistSongsList.getChildren().removeListener(removePlaylistListener);
            this.playlistSongsList.getChildren().clear();

            System.runFinalization();
            System.gc();

            this.songs.setAll(playlist.getSongList());
            this.playlistSongsList.getChildren().addListener(removePlaylistListener);

            return true;
        } catch (Exception ex) {
            LOGGER.log(Level.TRACE, ex.getLocalizedMessage());
            return false;
        }
    }

    public void removeSong(SongPane songNode) {
        requestParentForIndex.set(false);
        playlistSongsList.getChildren().remove(songNode);
        requestParentForIndex.set(true);
    }

    public BooleanProperty requestParentForIndexProperty() {
        return requestParentForIndex;
    }
}
