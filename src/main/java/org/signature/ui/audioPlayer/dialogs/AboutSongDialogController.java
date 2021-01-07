package org.signature.ui.audioPlayer.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutSongDialogController implements Initializable {

    @FXML
    private Label songTitle;
    @FXML
    private Label songArtist;
    @FXML
    private Label track;
    @FXML
    private Label disc;
    @FXML
    private Label albumTitle;
    @FXML
    private Label albumArtist;
    @FXML
    private Label genre;
    @FXML
    private Label songLength;
    @FXML
    private Label songReleaseYear;
    @FXML
    private Label fileLocation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        songArtist.prefHeightProperty().bindBidirectional(songTitle.prefHeightProperty());
        albumTitle.prefHeightProperty().bindBidirectional(albumArtist.prefHeightProperty());
    }

    @FXML
    private void handleCloseDialog(ActionEvent actionEvent) {
    }
}
