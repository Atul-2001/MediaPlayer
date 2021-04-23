package org.signature.ui.audioPlayer.dialogs;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.mpatric.mp3agic.*;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.dataModel.audioPlayer.Album;
import org.signature.dataModel.audioPlayer.Artist;
import org.signature.dataModel.audioPlayer.Song;
import org.signature.ui.MainWindowController;
import org.signature.ui.audioPlayer.BaseController;
import org.signature.ui.audioPlayer.Inventory;
import org.signature.ui.audioPlayer.model.AlbumPane;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

public class EditAlbumInfoDialogController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(EditAlbumInfoDialogController.class);

    private static EditAlbumInfoDialogController instance = null;

    @FXML
    private BorderPane root;
    @FXML
    private VBox component_list;
    @FXML
    private ImageView albumArtView;
    @FXML
    private SVGPath editAlbumImage;
    @FXML
    private TextField albumTitle;
    @FXML
    private TextField albumArtist;
    @FXML
    private TextField genre;
    @FXML
    private JFXButton btn_save;

    private JFXDialog dialog;
    private Album album;
    private final ObservableList<SongNode> songNodes = FXCollections.observableArrayList();
    private static BooleanBinding saveBtnStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        editAlbumImage.setOnMouseEntered(event -> {
            editAlbumImage.setScaleX(2.0);
            editAlbumImage.setScaleY(2.0);
        });

        editAlbumImage.setOnMouseExited(event -> {
            editAlbumImage.setScaleX(1.0);
            editAlbumImage.setScaleY(1.0);
        });

        songNodes.addListener((ListChangeListener<SongNode>) change -> {
            change.next();
            if (change.wasAdded()) {
                for (SongNode songNode : change.getAddedSubList()) {
                    component_list.getChildren().add(songNode);
                }
            } else if (change.wasRemoved()) {
                for (SongNode songNode : change.getRemoved()) {
                    component_list.getChildren().remove(songNode);
                }
            }
        });

        saveBtnStatus = albumTitle.textProperty().isEmpty()
                .or(albumArtist.textProperty().isEmpty())
                .or(genre.textProperty().isEmpty());

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

        this.dialog = new JFXDialog(MainWindowController.getInstance().getRoot(), root, JFXDialog.DialogTransition.CENTER);
        this.dialog.setMinHeight(440.0);
        this.dialog.setPrefHeight(440.0);
        this.dialog.setMaxHeight(600.0);

//        LOGGER.log(Level.INFO, "Edit Album Info Dialog loaded!");
    }

    public static EditAlbumInfoDialogController getInstance() {
        return instance;
    }

    public BorderPane getRoot() {
        return root;
    }

    @FXML
    private void handleEditAlbumImage(MouseEvent mouseEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open");
        chooser.setInitialDirectory(new File(System.getProperty("user.home"), "Pictures"));
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("*.png", "*.png"),
                new FileChooser.ExtensionFilter("*.jpg", "*.jpg"),
                new FileChooser.ExtensionFilter("*.jpeg", "*.jpeg"),
                new FileChooser.ExtensionFilter("*.jpe", "*.jpe"),
                new FileChooser.ExtensionFilter("*.jfif", "*.jfif"),
                new FileChooser.ExtensionFilter("*.bmp", "*.bmp"),
                new FileChooser.ExtensionFilter("*.dip", "*.dip"),
                new FileChooser.ExtensionFilter("*.gif", "*.gif"),
                new FileChooser.ExtensionFilter("*.tif", "*.tif"),
                new FileChooser.ExtensionFilter("*.tiff", "*.tiff"),
                new FileChooser.ExtensionFilter("All files (*.png;*.jpg;*.jpeg;*.jpe;*,jfif;*.bmp;*.dip;*.gif;*.tif;*.tiff)", "*.png", "*.jpg", "*.jpeg", "*.jpe", "*,jfif", "*.bmp", "*.dip", "*.gif", "*.tif", "*.tiff"));
        chooser.setSelectedExtensionFilter(chooser.getExtensionFilters().get(chooser.getExtensionFilters().size()-1));
        File imageFile = chooser.showOpenDialog(root.getScene().getWindow());
        if (imageFile != null) {
            albumArtView.setImage(new Image(imageFile.toURI().toString()));
        }
    }

    @FXML
    private void handleSaveInfo(ActionEvent actionEvent) {
        try {
            for (SongNode songNode : songNodes) {
                Song song = songNode.getSong();

                File songFile = new File(song.getLocation());
                String newSongFile = songFile.getParent().concat(File.separator).concat("[New] " + songFile.getName());

                Path songPath = Path.of(songFile.toURI());
                Path newSongPath = Path.of(new File(newSongFile).toURI());
                Mp3File mp3File = new Mp3File(songPath);

                ID3v2 id3v2Tag = new ID3v24Tag();

                id3v2Tag.setTitle(songNode.getTitle());
                id3v2Tag.setTrack(songNode.getTrack());
                id3v2Tag.setAlbum(this.albumTitle.getText());
                id3v2Tag.setAlbumArtist(this.albumArtist.getText());
                id3v2Tag.setArtist(songNode.getArtist());
                id3v2Tag.setGenreDescription(this.genre.getText());

                Image img = new Image(albumArtView.getImage().getUrl());
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(img, null);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                if (ImageIO.write(bufferedImage, "png", byteArrayOutputStream)) {
                    byteArrayOutputStream.close();
                    assert album != null;
                    album.setAlbumImage(byteArrayOutputStream.toByteArray());
                    album.setAlbumImageMimeType(id3v2Tag.getAlbumImageMimeType());
                }

                if (mp3File.hasId3v2Tag()) {
                    ID3v2 id3v2 = mp3File.getId3v2Tag();
                    id3v2Tag.setYear(id3v2.getYear());
                    id3v2Tag.setAlbumImage(byteArrayOutputStream.toByteArray(), id3v2.getAlbumImageMimeType());
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

                song.setTitle(songNode.getTitle());
                song.setTrack(songNode.getTrack());
                song.setAlbum(this.albumTitle.getText());
                if (!album.getAlbumName().equals("Unknown Album")) {
                    album.setAlbumName(this.albumTitle.getText());
                    album.setAlbumArtist(this.albumArtist.getText());
                    album.setArtist(songNode.getArtist());
                    album.setGenre(id3v2Tag.getGenreDescription());
                }
                song.setArtist(songNode.getArtist());
                if (Inventory.getArtist(songNode.getArtist()) == null) {
                    Inventory.addArtist(new Artist(songNode.getArtist()));
                }
                song.setGenre(id3v2Tag.getGenreDescription());
            }
        } catch (IOException | NullPointerException | UnsupportedTagException | InvalidDataException | NotSupportedException ex) {
            LOGGER.log(Level.ERROR, ex.getLocalizedMessage());
            ex.printStackTrace();
        }

        this.dialog.close();
    }

    @FXML
    private void handleCloseDialog(ActionEvent actionEvent) {
        this.dialog.close();
    }

    public void load(Album album) {
        clearAll();
        this.album = album;

        byte[] albumArt = album.getAlbumImage();
        if (albumArt == null || albumArt.length == 0) {
            this.albumArtView.setImage(AlbumPane.getDefaultAlbumArt());
        } else {
            try {
                this.albumArtView.setImage(SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(albumArt)), null));
            } catch (NullPointerException | IOException ex) {
                this.albumArtView.setImage(AlbumPane.getDefaultAlbumArt());
            }
        }
        albumTitle.setText(album.getAlbumName());
        albumArtist.setText(album.getAlbumArtist());
        genre.setText(album.getGenre());

        btn_save.disableProperty().unbind();
        for (Song song : Inventory.getSongs(album)) {
            songNodes.add(new SongNode(song));
        }
        btn_save.disableProperty().bind(saveBtnStatus);

        this.dialog.show();
    }

    private void clearAll() {
        albumTitle.clear();
        albumArtist.clear();
        genre.clear();
        songNodes.clear();
    }

    private static class SongNode extends HBox {

        private final TextField track;
        private final TextField title;
        private final TextField artist;

        private final Song song;

        public SongNode(Song song) {
            assert song != null;
            this.song = song;

            setAlignment(Pos.CENTER_LEFT);
            setPrefHeight(80.0);
            setSpacing(40.0);
            VBox.setVgrow(this, Priority.NEVER);

            /*--------------------------------------------------------------------------------------------------------*/
            final VBox songTrackBox = new VBox(4.0);
            songTrackBox.setAlignment(Pos.CENTER_LEFT);
            songTrackBox.setPrefSize(70.0, 68.0);
            songTrackBox.setMinSize(songTrackBox.getPrefWidth(), songTrackBox.getPrefHeight());
            songTrackBox.setMaxSize(songTrackBox.getPrefWidth(), songTrackBox.getPrefHeight());
            SongNode.setHgrow(songTrackBox, Priority.NEVER);

            final Label songTrack = new Label("Track");
            songTrack.setPrefHeight(32.0);
            songTrack.setMinHeight(songTrack.getPrefHeight());
            songTrack.setMaxHeight(songTrack.getPrefHeight());
            songTrack.setMaxWidth(Double.MAX_VALUE);
            songTrack.setFont(Font.font("Roboto", 14.0));
            VBox.setVgrow(songTrack, Priority.ALWAYS);

            track = new TextField(song.getTrack());
            track.setPrefHeight(32.0);
            track.setMinHeight(track.getPrefHeight());
            track.setMaxHeight(track.getPrefHeight());
            track.setMaxWidth(Double.MAX_VALUE);
            track.setFont(Font.font("Roboto", 14.0));
            track.getStyleClass().add("dialog-text-field");
            track.textProperty().addListener((observable, oldValue, newValue) -> {
                if (track.getText().isEmpty()) {
                    track.setStyle("-fx-border-color: RED");
                } else {
                    track.setStyle(null);
                }
            });

            songTrackBox.getChildren().setAll(songTrack, track);
            /*--------------------------------------------------------------------------------------------------------*/

            /*--------------------------------------------------------------------------------------------------------*/
            final VBox songTitleBox = new VBox(4.0);
            songTitleBox.setAlignment(Pos.CENTER_LEFT);
            songTitleBox.setPrefSize(300.0, 68.0);
            songTitleBox.setMinSize(songTitleBox.getPrefWidth(), songTitleBox.getPrefHeight());
            songTitleBox.setMaxSize(songTitleBox.getPrefWidth(), songTitleBox.getPrefHeight());
            SongNode.setHgrow(songTitleBox, Priority.NEVER);

            final Label songTitle = new Label("Title");
            songTitle.setPrefHeight(32.0);
            songTitle.setMinHeight(songTitle.getPrefHeight());
            songTitle.setMaxHeight(songTitle.getPrefHeight());
            songTitle.setMaxWidth(Double.MAX_VALUE);
            songTitle.setFont(Font.font("Roboto", 14.0));
            VBox.setVgrow(songTitle, Priority.ALWAYS);

            title = new TextField(song.getTitle());
            title.setPrefHeight(32.0);
            title.setMinHeight(title.getPrefHeight());
            title.setMaxHeight(title.getPrefHeight());
            title.setMaxWidth(Double.MAX_VALUE);
            title.setFont(Font.font("Roboto", 14.0));
            title.getStyleClass().add("dialog-text-field");
            title.textProperty().addListener((observable, oldValue, newValue) -> {
                if (title.getText().isEmpty()) {
                    title.setStyle("-fx-border-color: RED");
                } else {
                    title.setStyle(null);
                }
            });

            songTitleBox.getChildren().setAll(songTitle, title);
            /*--------------------------------------------------------------------------------------------------------*/

            /*--------------------------------------------------------------------------------------------------------*/
            final VBox songArtistBox = new VBox(4.0);
            songArtistBox.setAlignment(Pos.CENTER_LEFT);
            songArtistBox.setPrefHeight(68.0);
            songArtistBox.setPrefWidth(300.0);
            songArtistBox.setMinHeight(songArtistBox.getPrefHeight());
            songArtistBox.setMaxSize(Double.MAX_VALUE, songArtistBox.getPrefHeight());
            SongNode.setHgrow(songArtistBox, Priority.NEVER);

            final Label songArtist = new Label("Artist");
            songArtist.setPrefHeight(32.0);
            songArtist.setMinHeight(songArtist.getPrefHeight());
            songArtist.setMaxHeight(songArtist.getPrefHeight());
            songArtist.setMaxWidth(Double.MAX_VALUE);
            songArtist.setFont(Font.font("Roboto", 14.0));
            VBox.setVgrow(songArtist, Priority.ALWAYS);

            artist = new TextField(song.getArtist());
            artist.setPrefHeight(32.0);
            artist.setMinHeight(artist.getPrefHeight());
            artist.setMaxHeight(artist.getPrefHeight());
            artist.setMaxWidth(Double.MAX_VALUE);
            artist.setFont(Font.font("Roboto", 14.0));
            artist.getStyleClass().add("dialog-text-field");
            artist.textProperty().addListener((observable, oldValue, newValue) -> {
                if (artist.getText().isEmpty()) {
                    artist.setStyle("-fx-border-color: RED");
                } else {
                    artist.setStyle(null);
                }
            });

            songArtistBox.getChildren().setAll(songArtist, artist);
            /*--------------------------------------------------------------------------------------------------------*/

            saveBtnStatus = saveBtnStatus.or(track.textProperty().isEmpty())
                    .or(title.textProperty().isEmpty())
                    .or(artist.textProperty().isEmpty());

            getChildren().setAll(songTrackBox, songTitleBox, songArtistBox);
        }

        public Song getSong() {
            return song;
        }

        public String getTrack() {
            return track.getText();
        }

        public String getTitle() {
            return title.getText();
        }

        public String getArtist() {
            return artist.getText();
        }
    }
}
