package org.signature.ui.audioPlayer.dialogs;

import com.jfoenix.controls.JFXDialog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.dataModel.audioPlayer.Album;
import org.signature.dataModel.audioPlayer.Song;
import org.signature.ui.MainWindowController;
import org.signature.ui.audioPlayer.Inventory;
import org.signature.ui.audioPlayer.model.SongPane;
import org.signature.util.Utils;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutSongDialogController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(AboutSongDialogController.class);

    private static AboutSongDialogController instance = null;

    @FXML
    private BorderPane root;
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

    private JFXDialog dialog = null;
    private SongPane songPane = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        songArtist.prefHeightProperty().bindBidirectional(songTitle.prefHeightProperty());
        albumTitle.prefHeightProperty().bindBidirectional(albumArtist.prefHeightProperty());

        this.dialog = new JFXDialog(MainWindowController.getInstance().getRoot(), root, JFXDialog.DialogTransition.CENTER);
        this.dialog.setOnDialogClosed(event -> {
            if (songPane != null) {
                songPane.setSelectSong(false);
            }
        });

//        LOGGER.log(Level.INFO, "About Song Dialog loaded!");
    }

    public static AboutSongDialogController getInstance() {
        return instance;
    }

    public BorderPane getRoot() {
        return root;
    }

    @FXML
    private void handleCloseDialog(ActionEvent actionEvent) {
        dialog.close();
    }

    @FXML
    private void handleEditSongDetail(ActionEvent actionEvent) {
        dialog.close();
        EditSongInfoDialogController.getInstance().load(this.songPane);
    }

    public void load(SongPane songPane) {
        clearAll();

        this.songPane = songPane;
        Song song = songPane.getSong();
        Album album = Inventory.getAlbum(song.getAlbum(), song.getAlbumArtist());

        this.songTitle.setText(song.getTitle());
        this.songArtist.setText(song.getArtist());
        this.track.setText(song.getTrack());
        this.disc.setText("1");
        if (album == null) {
            this.albumTitle.setText(song.getAlbum());
            this.albumArtist.setText(song.getArtist());
        } else {
            this.albumTitle.setText(album.getAlbumName());
            this.albumArtist.setText(album.getAlbumArtist());
        }
        this.genre.setText(song.getGenre());
        this.songLength.setText(Utils.getDuration(song.getLength()));
        this.songReleaseYear.setText(song.getYearOfRelease());
        this.fileLocation.setText(song.getLocation());

        dialog.show();
    }

    private void clearAll() {
        this.songTitle.setText("");
        this.songArtist.setText("");
        this.track.setText("");
        this.disc.setText("");
        this.albumTitle.setText("");
        this.albumArtist.setText("");
        this.genre.setText("");
        this.songLength.setText("");
        this.songReleaseYear.setText("");
        this.fileLocation.setText("");
    }
}
