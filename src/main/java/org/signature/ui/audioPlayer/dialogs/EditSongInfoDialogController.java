package org.signature.ui.audioPlayer.dialogs;

import com.jfoenix.controls.JFXToggleButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class EditSongInfoDialogController implements Initializable {

    @FXML
    private TextField songTitle;
    @FXML
    private TextField songArtist;
    @FXML
    private TextField track;
    @FXML
    private TextField disc;
    @FXML
    private TextField albumTitle;
    @FXML
    private TextField albumArtist;
    @FXML
    private TextField genre;
    @FXML
    private TextField songReleaseYear;
    @FXML
    private TextField songSortTitle;
    @FXML
    private JFXToggleButton showAdvanceOption;
    @FXML
    private Label fileLocation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showAdvanceOption.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                showAdvanceOption.setText("On");
            } else {
                showAdvanceOption.setText("Off");
            }
        });
    }

    @FXML
    private void handleSaveInfo(ActionEvent actionEvent) {
    }

    @FXML
    private void handleCloseDialog(ActionEvent actionEvent) {
    }
}
