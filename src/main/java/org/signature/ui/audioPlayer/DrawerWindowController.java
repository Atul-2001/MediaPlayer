package org.signature.ui.audioPlayer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;

import java.net.URL;
import java.util.ResourceBundle;

public class DrawerWindowController implements Initializable {

    @FXML
    private ToggleButton btnRecentlyPlayed, btnSongs, btnAlbums, btnArtists, btnFavourite, btnShowPlaylist, btnSettings;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public ToggleButton getBtnRecentlyPlayed() {
        return btnRecentlyPlayed;
    }

    public ToggleButton getBtnSongs() {
        return btnSongs;
    }

    public ToggleButton getBtnAlbums() {
        return btnAlbums;
    }

    public ToggleButton getBtnArtists() {
        return btnArtists;
    }

    public ToggleButton getBtnFavourite() {
        return btnFavourite;
    }

    public ToggleButton getBtnShowPlaylist() {
        return btnShowPlaylist;
    }

    public ToggleButton getBtnSettings() {
        return btnSettings;
    }
}
