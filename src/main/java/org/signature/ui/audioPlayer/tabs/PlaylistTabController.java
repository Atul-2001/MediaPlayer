package org.signature.ui.audioPlayer.tabs;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.dataModel.audioPlayer.Playlist;
import org.signature.ui.audioPlayer.dialogs.AddPlaylistDialogController;
import org.signature.ui.audioPlayer.model.PlaylistPane;
import org.signature.util.Utils;

import java.net.URL;
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

    private JFXDialog dialog = null;

    private boolean isPlaylistsLoaded = false;
    private final IntegerProperty playlistNameFieldHeight = new SimpleIntegerProperty(0);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        loadPlaylists();
        Utils.flipStackPane(contentStack);

        LOGGER.log(Level.INFO, "Playlist Tab Loaded !!");
    }

    public static PlaylistTabController getInstance() {
        return instance;
    }

    public VBox getRoot() {
        return tab_playlists;
    }

    @FXML
    private void handleAddPlaylist(ActionEvent actionEvent) {
        if (dialog == null) {
            BorderPane node = AddPlaylistDialogController.getInstance().getRoot();
            dialog = new JFXDialog((StackPane) tab_playlists.getParent(), node, JFXDialog.DialogTransition.CENTER);
            dialog.setMinSize(400, 400);
            ((JFXButton) ((HBox) node.getBottom()).getChildren().get(0)).setOnAction(event -> dialog.close());
        }
        dialog.show();
    }

    private void loadPlaylists() {
        if (!isPlaylistsLoaded) {
            try {
                list_playlists.getChildren().clear();
                for (int i = 0; i < 10; i++) {
                    PlaylistPane playlist = new PlaylistPane(new Playlist(String.valueOf(i+1), (i+1)*10));
                    list_playlists.getChildren().add(playlist);
                }
                isPlaylistsLoaded = true;
            } catch (NullPointerException | IllegalStateException | UnsupportedOperationException | IllegalArgumentException e) {
                LOGGER.log(Level.ERROR, "Failed to load Playlist node! " + e.getLocalizedMessage(), e);
            }
        }
    }

    public IntegerProperty playlistNameFieldHeightProperty() {
        return playlistNameFieldHeight;
    }

    public void setPlaylistNameFieldHeight(int playlistNameFieldHeight) {
        if (playlistNameFieldHeight != this.playlistNameFieldHeight.get()) {
            this.playlistNameFieldHeight.set(Math.max(playlistNameFieldHeight, this.playlistNameFieldHeight.get()));
        }
    }
}
