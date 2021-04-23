package org.signature.ui.audioPlayer.tabs;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.dataModel.audioPlayer.Album;
import org.signature.dataModel.audioPlayer.Artist;
import org.signature.dataModel.audioPlayer.Song;
import org.signature.ui.audioPlayer.BaseController;
import org.signature.ui.audioPlayer.ConsoleController;
import org.signature.ui.audioPlayer.Inventory;
import org.signature.ui.audioPlayer.dialogs.AddToListDialogController;
import org.signature.ui.audioPlayer.model.AlbumPane;
import org.signature.ui.audioPlayer.model.ArtistPane;
import org.signature.ui.audioPlayer.model.SongPane;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class ArtistViewTabController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(ArtistViewTabController.class);

    private static ArtistViewTabController instance = null;

    private final double ARTIST_VIEW_HEADER_MIN_HEIGHT = 196.0;
    private final double ARTIST_VIEW_HEADER_MAX_HEIGHT = 274.0;

    @FXML
    private VBox artist_view;
    @FXML
    private HBox artistViewHeader;
    @FXML
    private AnchorPane artistArtViewPane;
    @FXML
    private Circle artistArtView;
    @FXML
    private VBox info_and_controls_pane;
    @FXML
    private Label artistName;
    @FXML
    private Label artistGenre;
    @FXML
    private StackPane contentStack;
    @FXML
    private ScrollPane albumViewContentPane, albumSongViewContentPane;
    @FXML
    private VBox albumSongsList;
    @FXML
    private FlowPane albumsList;

    private Artist artist;
    private final ObservableList<Album> albums = FXCollections.observableArrayList();

    private boolean isViewRequestedByAlbum = false;
    private Album requesterAlbum = null;
    private boolean isViewRequestedBySong = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        albums.addListener((ListChangeListener<Album>) change -> {
            change.next();
            if (change.wasAdded()) {
                albumsList.getChildren().clear();

                HBox control = (HBox) albumSongsList.getChildren().get(0);
                albumSongsList.getChildren().clear();
                albumSongsList.getChildren().add(control);

                for (Album album : change.getAddedSubList()) {
                    albumsList.getChildren().add(new AlbumPane(album));
                    albumSongsList.getChildren().add(new MiniAlbumPane(album));
                    for (Song song : Inventory.getSongs(album)) {
                        albumSongsList.getChildren().add(new SongPane(song, artist));
                    }
                }
            }

            if (albumsList.getChildren().size() > 0) {
                if (contentStack.getChildren().get(2).toString().contains("VBox")) {
                    contentStack.getChildren().get(1).setVisible(true);
                    contentStack.getChildren().get(2).setVisible(false);
                    contentStack.getChildren().get(2).toBack();
                }
            }
        });

        AtomicBoolean isViewChanged = new AtomicBoolean(false);
        albumsList.setOnScroll(event -> {
            if (event.getDeltaY() < 0 && !isViewChanged.get()) {
                artistViewHeader.setPrefHeight(ARTIST_VIEW_HEADER_MIN_HEIGHT);
                artistArtView.setRadius((ARTIST_VIEW_HEADER_MIN_HEIGHT * 0.73)/2);
                artistArtViewPane.setPrefWidth(ARTIST_VIEW_HEADER_MIN_HEIGHT * 0.73);
                artistArtViewPane.setPrefHeight(ARTIST_VIEW_HEADER_MIN_HEIGHT * 0.73);
                isViewChanged.set(true);
            } else if (event.getDeltaY() > 0 && isViewChanged.get()){
                artistViewHeader.setPrefHeight(ARTIST_VIEW_HEADER_MAX_HEIGHT);
                artistArtViewPane.setPrefWidth(ARTIST_VIEW_HEADER_MAX_HEIGHT * 0.73);
                artistArtViewPane.setPrefHeight(ARTIST_VIEW_HEADER_MAX_HEIGHT * 0.73);
                artistArtView.setRadius((ARTIST_VIEW_HEADER_MAX_HEIGHT * 0.73)/2);
                isViewChanged.set(false);
            }
        });

        albumSongsList.setOnScroll(albumsList.getOnScroll());

//        LOGGER.log(Level.INFO, "Tab Artist View Loaded!");
    }

    public static ArtistViewTabController getInstance() {
        return instance;
    }

    public VBox getRoot() {
        return artist_view;
    }

    @FXML
    private void handleBackOperation(ActionEvent actionEvent) {
        if (isViewRequestedByAlbum) {
            if (requesterAlbum != null) {
                if (AlbumViewTabController.getInstance().loadAlbum(requesterAlbum, false, null)) {
                    isViewRequestedByAlbum = false;
                    requesterAlbum = null;
                    BaseController.getInstance().handleShowAlbumView();
                }
            }
        } else if (isViewRequestedBySong) {
            this.isViewRequestedBySong = false;
            BaseController.getInstance().getBtnSongs().fire();
        } else {
            BaseController.getInstance().getBtnArtists().fire();
        }
    }

    @FXML
    private void handlePlayAllSongs(ActionEvent actionEvent) {
        ConsoleController.getInstance().load(this.artist);
        RecentlyPlayedTabController.getInstance().addRecentlyPlayed(this.artist);
    }

    @FXML
    private void handleAddTo(ActionEvent actionEvent) {
        AddToListDialogController.getInstance().load(this.artist);
    }

    @FXML
    private void handleShowMyMusic(MouseEvent mouseEvent) {
        BaseController.getInstance().getBtnSongs().fire();
    }

    @FXML
    private void handleShowAlbumsView(ActionEvent actionEvent) {
        for (int i = 0; i < 3; i++) {
            contentStack.getChildren().get(i).setVisible(false);
        }

        for (int i = 0; i < 3; i++) {
            if (contentStack.getChildren().get(i).toString().contains("albumViewContentPane")) {
                contentStack.getChildren().get(i).setVisible(true);
                contentStack.getChildren().get(i).toFront();
            }
        }
    }

    @FXML
    private void handleShowSongsView(ActionEvent actionEvent) {
        for (int i = 0; i < 3; i++) {
            contentStack.getChildren().get(i).setVisible(false);
        }

        for (int i = 0; i < 3; i++) {
            if (contentStack.getChildren().get(i).toString().contains("albumSongViewContentPane")) {
                contentStack.getChildren().get(i).setVisible(true);
                contentStack.getChildren().get(i).toFront();
            }
        }
    }

    public boolean loadArtist(Artist artist, boolean isViewRequestedByAlbum, Album requesterAlbum) {
        try {
            this.artist = artist;
            this.isViewRequestedByAlbum = isViewRequestedByAlbum;
            this.requesterAlbum = requesterAlbum;

            this.albums.clear();
            this.albums.setAll(Inventory.getAlbums(this.artist));

            this.artistArtView.setFill(new ImagePattern(ArtistPane.getDefaultArtistProfile()));
            this.artistName.setText(this.artist.getName());
            if (albums.size() > 0) {
                this.artistGenre.setText(this.albums.get(0).getGenre());
            } else {
                this.artistGenre.setText(Inventory.getCachedGenres().get(Inventory.getCachedGenres().size()-1));
            }

            handleShowAlbumsView(null);
            return true;
        } catch (Exception ex) {
            LOGGER.log(Level.TRACE, ex);
            return false;
        }
    }

    public void setViewRequestedBySong(boolean value) {
        this.isViewRequestedBySong = value;
    }

    private static class MiniAlbumPane extends HBox {

        private final Album album;
        private static final String DEFAULT_STYLESHEET = "model-node-style.css";
        private static final String DEFAULT_ALBUM_ART = "album_white.png";

        private final ImageView albumArtView = new ImageView();
        private final VBox albumDetailBox = new VBox();
        private final Label albumName = new Label();
        private final Label albumReleaseYear = new Label();

        private final String genre;

        public MiniAlbumPane(Album album) {
            assert album != null;
            this.album = album;

            setAlignment(Pos.CENTER_LEFT);
            setSpacing(10.0);
            setFocusTraversable(true);
            getStylesheets().add(AlbumPane.class.getResource(DEFAULT_STYLESHEET).toString());

            this.albumArtView.setFitHeight(160.0);
            this.albumArtView.setFitWidth(160.0);
            this.albumArtView.setPickOnBounds(true);
            byte[] albumArt = album.getAlbumImage();
            if (albumArt == null || albumArt.length == 0) {
                this.albumArtView.setImage(new Image(AlbumPane.class.getResourceAsStream(DEFAULT_ALBUM_ART)));
            } else {
                try {
                    this.albumArtView.setImage(SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(albumArt)), null));
                } catch (NullPointerException | IOException ex) {
                    this.albumArtView.setImage(new Image(AlbumPane.class.getResourceAsStream(DEFAULT_ALBUM_ART)));
                }
            }

            album.albumImageMimeTypeProperty().addListener((observable, oldValue, newValue) -> {
                byte[] albumArt1 = album.getAlbumImage();
                if (albumArt1 == null || albumArt1.length == 0) {
                    this.albumArtView.setImage(AlbumPane.getDefaultAlbumArt());
                } else {
                    try {
                        this.albumArtView.setImage(SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(albumArt1)), null));
                    } catch (NullPointerException | IOException ex) {
                        this.albumArtView.setImage(AlbumPane.getDefaultAlbumArt());
                    }
                }
            });

            this.albumName.setAlignment(Pos.TOP_LEFT);
            this.albumName.setText(album.getAlbumName() == null || album.getAlbumName().isEmpty() ? "Unknown Album" : album.getAlbumName());
            VBox.setVgrow(this.albumName, Priority.NEVER);
            this.albumName.setFont(Font.font("Roboto", 16.0));

            this.albumReleaseYear.setAlignment(Pos.TOP_LEFT);
            this.albumReleaseYear.setText(album.getReleaseYear() == null ? "" : album.getReleaseYear());
            VBox.setVgrow(this.albumReleaseYear, Priority.NEVER);
            this.albumReleaseYear.setFont(Font.font("Roboto Light", 14.0));

            this.albumDetailBox.setAlignment(Pos.CENTER_LEFT);
            this.albumDetailBox.setSpacing(4.0);
            this.albumDetailBox.setMaxHeight(Double.MAX_VALUE);
            this.albumDetailBox.setMaxWidth(Double.MAX_VALUE);
            MiniAlbumPane.setHgrow(this.albumDetailBox, Priority.ALWAYS);
            this.albumDetailBox.getChildren().setAll(this.albumName, this.albumReleaseYear);

            getChildren().addAll(this.albumArtView, this.albumDetailBox);

            this.genre = album.getGenre();
        }

        public Album getAlbum() {
            return album;
        }

        public String getAlbumName() {
            return albumName.getText();
        }

        public String getArtist() {
            return this.album.getArtist();
        }

        public String getGenre() {
            return genre;
        }

        public String getReleaseYear() {
            return this.albumReleaseYear.getText();
        }

        public long getDateCreated() {
            return album.getDateCreated();
        }
    }
}
