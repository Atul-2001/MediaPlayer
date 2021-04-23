package org.signature.ui.audioPlayer.tabs;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.dataModel.audioPlayer.Playlist;
import org.signature.ui.audioPlayer.BaseController;
import org.signature.ui.audioPlayer.Inventory;
import org.signature.ui.audioPlayer.dialogs.AddPlaylistDialogController;
import org.signature.ui.audioPlayer.model.PlaylistPane;
import org.signature.util.Utils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PlaylistTabController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(PlaylistTabController.class);

    private static PlaylistTabController instance = null;

    @FXML
    private VBox tab_playlists;
    @FXML
    private StackPane contentStack;
    @FXML
    private FlowPane list_playlists;

    private final IntegerProperty playlistNameFieldHeight = new SimpleIntegerProperty(0);
    private final ObservableList<Playlist> playlists = FXCollections.observableArrayList();
    private boolean isCachedListLoaded = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        playlists.addListener((ListChangeListener<Playlist>) change -> {
            change.next();
            if (change.wasAdded()) {
                for (Playlist playlist : change.getAddedSubList()) {
                    if (isCachedListLoaded) {
                        Inventory.addPlaylist(playlist);
                    }
                    list_playlists.getChildren().add(new PlaylistPane(playlist));
                    BaseController.getInstance().addPlaylist(playlist);
                }
            } else if (change.wasRemoved()) {
                for (Playlist playlist : change.getRemoved()) {
                    Inventory.removePlaylist(playlist);
                    list_playlists.getChildren().removeIf(node -> ((PlaylistPane) node).getPlaylistName().equalsIgnoreCase(playlist.getPlaylistName()));
                    BaseController.getInstance().removePlaylist(playlist);
                    RecentlyPlayedTabController.getInstance().removeRecentlyPlayed(playlist);
                }
            }

            if (list_playlists.getChildren().size() == 0 && !contentStack.getChildren().get(1).toString().contains("Label")) {
                Utils.flipStackPane(contentStack);
            } else if (list_playlists.getChildren().size() > 0 && contentStack.getChildren().get(1).toString().contains("Label")) {
                Utils.flipStackPane(contentStack);
            }
        });

        if (!isCachedListLoaded) {
            setPlaylists(Inventory.getCachedPlaylist());
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
            isCachedListLoaded = true;
        }

//        LOGGER.log(Level.INFO, "Playlist Tab Loaded !!");
    }

    public static PlaylistTabController getInstance() {
        return instance;
    }

    public VBox getRoot() {
        return tab_playlists;
    }

    @FXML
    private void handleAddPlaylist(ActionEvent actionEvent) {
        AddPlaylistDialogController.getInstance().load(null);
    }

    public IntegerProperty playlistNameFieldHeightProperty() {
        return playlistNameFieldHeight;
    }

    public void setPlaylistNameFieldHeight(int playlistNameFieldHeight) {
        if (playlistNameFieldHeight != this.playlistNameFieldHeight.get()) {
            this.playlistNameFieldHeight.set(Math.max(playlistNameFieldHeight, this.playlistNameFieldHeight.get()));
        }
    }

    public void setPlaylists(List<Playlist> playlists) {
        assert playlists != null;
        list_playlists.getChildren().clear();
        this.playlists.setAll(playlists);
    }

    public void addPlaylist(Playlist playlist) {
        assert playlist != null;
        if (!playlists.contains(playlist)) {
            playlists.add(playlist);
        }
    }

    public void removePlaylist(Playlist playlist) {
        assert playlist != null;
        playlists.remove(playlist);
    }
}
