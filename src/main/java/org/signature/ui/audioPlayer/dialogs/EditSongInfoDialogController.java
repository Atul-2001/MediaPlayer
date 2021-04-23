package org.signature.ui.audioPlayer.dialogs;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXToggleButton;
import com.mpatric.mp3agic.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.dataModel.audioPlayer.Album;
import org.signature.dataModel.audioPlayer.Artist;
import org.signature.dataModel.audioPlayer.Song;
import org.signature.ui.MainWindowController;
import org.signature.ui.audioPlayer.Inventory;
import org.signature.ui.audioPlayer.model.SongPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ResourceBundle;

public class EditSongInfoDialogController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(EditSongInfoDialogController.class);

    private static EditSongInfoDialogController instance = null;

    @FXML
    private BorderPane root;
    @FXML
    private GridPane componentGrid;
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
    private VBox sortTitleBox;
    @FXML
    private TextField songSortTitle;
    @FXML
    private VBox showAdvanceOptionButtonBox;
    @FXML
    private JFXToggleButton showAdvanceOption;
    @FXML
    private VBox fileLocationBox;
    @FXML
    private Label fileLocation;
    @FXML
    private JFXButton btn_save;

    private JFXDialog dialog;
    private SongPane songPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        componentGrid.getChildren().remove(sortTitleBox);
        GridPane.setRowIndex(showAdvanceOptionButtonBox, 4);
        GridPane.setRowIndex(fileLocationBox, 5);

        showAdvanceOption.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                showAdvanceOption.setText("On");
                GridPane.setRowIndex(showAdvanceOptionButtonBox, 5);
                GridPane.setRowIndex(fileLocationBox, 6);
                componentGrid.getChildren().add(sortTitleBox);
                GridPane.setRowIndex(sortTitleBox, 4);
            } else {
                showAdvanceOption.setText("Off");
                componentGrid.getChildren().remove(sortTitleBox);
                GridPane.setRowIndex(showAdvanceOptionButtonBox, 4);
                GridPane.setRowIndex(fileLocationBox, 5);
            }
        });

        btn_save.disableProperty().bind(songTitle.textProperty().isEmpty()
                .or(track.textProperty().isEmpty())
                .or(disc.textProperty().isEmpty())
                .or(albumTitle.textProperty().isEmpty())
                .or(albumArtist.textProperty().isEmpty())
                .or(genre.textProperty().isEmpty()));

        songTitle.textProperty().addListener((observable, oldValue, newValue) -> {
            if (songTitle.getText().isEmpty()) {
                songTitle.setStyle("-fx-border-color: RED");
            } else {
                songTitle.setStyle(null);
            }
        });

        track.textProperty().addListener((observable, oldValue, newValue) -> {
            if (track.getText().isEmpty()) {
                track.setStyle("-fx-border-color: RED");
            } else {
                track.setStyle(null);
            }
        });

        disc.textProperty().addListener((observable, oldValue, newValue) -> {
            if (disc.getText().isEmpty()) {
                disc.setStyle("-fx-border-color: RED");
            } else {
                disc.setStyle(null);
            }
        });

        albumTitle.textProperty().addListener((observable, oldValue, newValue) -> {
            if (albumTitle.getText().isEmpty()) {
                albumTitle.setStyle("-fx-border-color: RED");
            } else {
                albumTitle.setStyle(null);
            }
        });

        albumArtist.textProperty().addListener((observable, oldValue, newValue) -> {
            if (albumArtist.getText().isEmpty()) {
                albumArtist.setStyle("-fx-border-color: RED");
            } else {
                albumArtist.setStyle(null);
            }
        });

        genre.textProperty().addListener((observable, oldValue, newValue) -> {
            if (genre.getText().isEmpty()) {
                genre.setStyle("-fx-border-color: RED");
            } else {
                genre.setStyle(null);
            }
        });

        songReleaseYear.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() < 4 && newValue.length() > 0) {
                songReleaseYear.setStyle("-fx-border-color: RED");
                btn_save.disableProperty().set(true);
            } else {
                songReleaseYear.setStyle(null);
                btn_save.disableProperty().set(false);
            }

            if (newValue.isEmpty()) {
                songReleaseYear.setStyle(null);
                btn_save.disableProperty().set(false);
            }
        });

        this.dialog = new JFXDialog(MainWindowController.getInstance().getRoot(), root, JFXDialog.DialogTransition.CENTER);
        this.dialog.setOnDialogClosed(event -> {
            if (songPane != null) {
                songPane.setSelectSong(false);
            }
        });

//        LOGGER.log(Level.INFO, "Edit Song Info Dialog loaded!");
    }

    public static EditSongInfoDialogController getInstance() {
        return instance;
    }

    public BorderPane getRoot() {
        return root;
    }

    @FXML
    private void handleSaveInfo(ActionEvent actionEvent) {
        try {
            Song song = songPane.getSong();

            File songFile = new File(song.getLocation());
            String newSongFile = songFile.getParent().concat(File.separator).concat("[New] " + songFile.getName());

            Path songPath = Path.of(songFile.toURI());
            Path newSongPath = Path.of(new File(newSongFile).toURI());
            Mp3File mp3File = new Mp3File(songPath);

            ID3v2 id3v2Tag = new ID3v24Tag();

            id3v2Tag.setTitle(this.songTitle.getText());
            id3v2Tag.setTrack(this.track.getText());
            id3v2Tag.setAlbum(this.albumTitle.getText());
            id3v2Tag.setAlbumArtist(this.albumArtist.getText());
            id3v2Tag.setArtist(this.songArtist.getText());
            id3v2Tag.setGenreDescription(this.genre.getText());
            id3v2Tag.setYear(this.songReleaseYear.getText());

            if (mp3File.hasId3v2Tag()) {
                ID3v2 id3v2 = mp3File.getId3v2Tag();
                id3v2Tag.setAlbumImage(id3v2.getAlbumImage(), id3v2.getAlbumImageMimeType());
                id3v2Tag.setArtistUrl(id3v2.getArtistUrl());
                id3v2Tag.setAudiofileUrl(id3v2.getAudiofileUrl());
                id3v2Tag.setAudioSourceUrl(id3v2.getAudioSourceUrl());
                id3v2Tag.setBPM(id3v2.getBPM());
                id3v2Tag.setChapters(id3v2.getChapters());
                id3v2Tag.setChapterTOC(id3v2.getChapterTOC());
                id3v2Tag.setCommercialUrl(id3v2.getCommercialUrl());
                id3v2Tag.setCompilation(id3v2.isCompilation());
                id3v2Tag.setComposer(id3v2.getComposer());
                id3v2Tag.setCopyright(id3v2.getCopyright());
                id3v2Tag.setCopyrightUrl(id3v2.getCopyrightUrl());
                id3v2Tag.setDate(id3v2.getDate());
                id3v2Tag.setEncoder(id3v2.getEncoder());
                id3v2Tag.setFooter(id3v2.hasFooter());
                id3v2Tag.setGrouping(id3v2.getGrouping());
                id3v2Tag.setItunesComment(id3v2.getItunesComment());
                id3v2Tag.setKey(id3v2.getKey());
                id3v2Tag.setLyrics(id3v2.getLyrics());
                id3v2Tag.setOriginalArtist(id3v2.getOriginalArtist());
                id3v2Tag.setPadding(id3v2.getPadding());
                id3v2Tag.setPartOfSet(id3v2.getPartOfSet());
                id3v2Tag.setPaymentUrl(id3v2.getPaymentUrl());
                id3v2Tag.setPublisher(id3v2.getPublisher());
                id3v2Tag.setPublisherUrl(id3v2.getPublisherUrl());
                id3v2Tag.setRadiostationUrl(id3v2.getRadiostationUrl());
                id3v2Tag.setUnsynchronisation(id3v2.hasUnsynchronisation());
                id3v2Tag.setUrl(id3v2.getUrl());
                id3v2Tag.setWmpRating(id3v2.getWmpRating());
                id3v2Tag.setComment(id3v2.getComment());
            }
            mp3File.setId3v2Tag(id3v2Tag);

            mp3File.save(newSongFile);
            Files.move(newSongPath, songPath, StandardCopyOption.REPLACE_EXISTING);

            song.setTitle(this.songTitle.getText());
            song.setTrack(this.track.getText());
            song.setAlbum(this.albumTitle.getText());
            Album album = Inventory.getAlbum(this.albumTitle.getText(), this.albumArtist.getText());
            if (album == null) {
                Album newAlbum = new Album(this.albumTitle.getText(), this.songArtist.getText(), this.songReleaseYear.getText(), FileTime.from(Instant.now()).toMillis());
                newAlbum.setAlbumArtist(this.albumArtist.getText());
                newAlbum.setGenre(id3v2Tag.getGenreDescription());
                Inventory.addAlbum(newAlbum);
            } else {
                if (!album.getAlbumName().equals("Unknown Album")) {
                    album.setAlbumArtist(this.albumArtist.getText());
                    album.setArtist(this.songArtist.getText());
                    album.setGenre(id3v2Tag.getGenreDescription());
                    album.setReleaseYear(this.songReleaseYear.getText());
                }
            }
            song.setArtist(this.songArtist.getText());
            if (Inventory.getArtist(this.songArtist.getText()) == null) {
                Inventory.addArtist(new Artist(this.songArtist.getText()));
            }
            song.setGenre(id3v2Tag.getGenreDescription());
            song.setYearOfRelease(this.songReleaseYear.getText());

        } catch (IOException | NullPointerException | UnsupportedTagException | InvalidDataException | NotSupportedException ex) {
            LOGGER.log(Level.ERROR, ex.getLocalizedMessage());
        }

        dialog.close();
    }

    @FXML
    private void handleCloseDialog(ActionEvent actionEvent) {
        dialog.close();
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
        this.songReleaseYear.setText(song.getYearOfRelease());
        this.songSortTitle.setText(song.getTitle());
        this.fileLocation.setText(song.getLocation());

        dialog.show();
    }

    private void clearAll() {
        this.songTitle.clear();
        this.songArtist.clear();
        this.track.clear();
        this.disc.clear();
        this.albumTitle.clear();
        this.albumArtist.clear();
        this.genre.clear();
        this.songReleaseYear.clear();
        this.songSortTitle.clear();
        this.fileLocation.setText("");
    }
}
