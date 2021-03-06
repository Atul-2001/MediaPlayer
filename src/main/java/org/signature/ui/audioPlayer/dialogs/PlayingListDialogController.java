package org.signature.ui.audioPlayer.dialogs;

import com.jfoenix.controls.JFXDialog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.dataModel.audioPlayer.OnlineSong;
import org.signature.dataModel.audioPlayer.PlaylistSong;
import org.signature.dataModel.audioPlayer.Song;
import org.signature.ui.MainWindowController;
import org.signature.ui.audioPlayer.model.OnlineSongPane;
import org.signature.ui.audioPlayer.model.SongPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PlayingListDialogController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(PlayingListDialogController.class);

    private static PlayingListDialogController instance = null;

    @FXML
    private VBox root;
    @FXML
    private VBox songsList;

    private JFXDialog dialog = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        dialog = new JFXDialog(MainWindowController.getInstance().getRoot(), root, JFXDialog.DialogTransition.CENTER);
//        LOGGER.log(Level.INFO, "Playing List Dialog Loaded !!");
    }

    public static PlayingListDialogController getInstance() {
        return instance;
    }

    public VBox getRoot() {
        return root;
    }

    @FXML
    private void handleCloseDialog(ActionEvent actionEvent) {
        this.dialog.close();
    }

    public void loadData(List<Object> songs) {
        if (songs != null) {
            songsList.getChildren().clear();
            int i = 0;

            for (Object song : songs) {

                if (song instanceof Song) {
                    if (((Song) song).getTitle().isEmpty()) {
                        continue;
                    }

                    SongPane songPane = new SongPane((Song) song, "Playing List");
                    if (i%2 == 0) {
                        songPane.getStyleClass().add("songNodeEVEN");
                    } else {
                        songPane.getStyleClass().add("songNodeODD");
                    }
                    songsList.getChildren().add(songPane);
                } else if (song instanceof OnlineSong) {
                    OnlineSongPane onlineSongPane = new OnlineSongPane((OnlineSong) song);
                    if (i%2 == 0) {
                        onlineSongPane.getStyleClass().add("songNodeEVEN");
                    } else {
                        onlineSongPane.getStyleClass().add("songNodeODD");
                    }
                    songsList.getChildren().add(onlineSongPane);
                } else if (song instanceof PlaylistSong) {
                    SongPane songPane = new SongPane((PlaylistSong) song, ((PlaylistSong) song).getPlaylist());
                    if (i%2 == 0) {
                        songPane.getStyleClass().add("songNodeEVEN");
                    } else {
                        songPane.getStyleClass().add("songNodeODD");
                    }
                    songsList.getChildren().add(songPane);
                }

                i++;
            }
        }
    }

    public void clearData() {
        songsList.getChildren().clear();
    }

    public void showDialog() {
        this.dialog.show();
    }
}
