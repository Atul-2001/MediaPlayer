package org.signature.ui.audioPlayer.dialogs;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.ui.audioPlayer.BaseController;
import org.signature.ui.audioPlayer.tabs.PlaylistViewTabController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddPlaylistDialogController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(AddPlaylistDialogController.class);

    private static AddPlaylistDialogController instance = null;

    @FXML
    private BorderPane root;
    @FXML
    private TextField playlistName;
    @FXML
    private JFXButton createPlaylistButton;
    @FXML
    private JFXButton cancelButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        createPlaylistButton.disableProperty().bind(playlistName.textProperty().isEmpty());

        LOGGER.log(Level.INFO, "Add Playlist Dialog Loaded !!");
    }

    public static AddPlaylistDialogController getInstance() {
        return instance;
    }

    public BorderPane getRoot() {
        return root;
    }

    @FXML
    private void handleAddPlaylist(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(PlaylistViewTabController.class.getResource("Tab_PlaylistView.fxml"));
            VBox playlist_view = loader.load();
            playlist_view.setOpacity(0.0);
            PlaylistViewTabController controller = loader.getController();
            controller.createPlaylist(playlistName.getText());
            BaseController.getInstance().addPlaylist(playlist_view, controller.playlistNamePropertyProperty());
            playlistName.clear();
            cancelButton.fire();
        } catch (NullPointerException | IOException e) {
            LOGGER.log(Level.ERROR, e);
        }
    }
}
