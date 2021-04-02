package org.signature.ui.audioPlayer.tabs;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.dataModel.audioPlayer.Album;
import org.signature.dataModel.audioPlayer.Artist;
import org.signature.dataModel.audioPlayer.Song;
import org.signature.ui.audioPlayer.BaseController;
import org.signature.ui.audioPlayer.ConsoleController;
import org.signature.ui.audioPlayer.Inventory;
import org.signature.ui.audioPlayer.model.AlbumPane;
import org.signature.ui.audioPlayer.model.SongPane;
import org.signature.util.Utils;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class AlbumViewTabController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(AlbumViewTabController.class);

    private static AlbumViewTabController instance = null;

    private final double ALBUM_VIEW_HEADER_MIN_HEIGHT = 196.0;
    private final double ALBUM_VIEW_HEADER_MAX_HEIGHT = 274.0;

    @FXML
    private VBox album_view;
    @FXML
    private HBox albumViewHeader;
    @FXML
    private ImageView albumArtView;
    @FXML
    private VBox info_and_controls_pane;
    @FXML
    private TextField albumName;
    @FXML
    private Label artistName;
    @FXML
    private HBox albumInfoPane;
    @FXML
    private Label albumReleaseYear;
    @FXML
    private Label albumCategory;
    @FXML
    private StackPane contentStack;
    @FXML
    private ScrollPane albumViewContentPane;
    @FXML
    private VBox albumSongsList;

    private final ObservableList<Song> songs = FXCollections.observableArrayList();
    private Album album;
    private Artist artist;

    private boolean isViewRequestedFromArtist = false;
    private Artist requesterArtist = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        songs.addListener((ListChangeListener<Song>) c -> {
            c.next();
            if (c.wasAdded()) {
                int i = 0;
                albumSongsList.getChildren().clear();
                for (Song song : c.getAddedSubList()) {
                    if (song.getTitle().isEmpty()) {
                        continue;
                    }

                    SongPane songPane = new SongPane(song);
                    if (i%2 == 0) {
                        songPane.getStyleClass().add("songNodeEVEN");
                    } else {
                        songPane.getStyleClass().add("songNodeODD");
                    }
                    albumSongsList.getChildren().add(songPane);
                    i++;
                }

                if (albumSongsList.getChildren().size() > 0) {
                    if (contentStack.getChildren().get(1).toString().contains("VBox")) {
                        Utils.flipStackPane(contentStack);
                    }
                }
            }
        });

        AtomicBoolean isViewChanged = new AtomicBoolean(false);
        albumSongsList.setOnScroll(event -> {
            if (event.getDeltaY() < 0 && !isViewChanged.get()) {
                info_and_controls_pane.getChildren().remove(albumInfoPane);
                albumViewHeader.setPrefHeight(ALBUM_VIEW_HEADER_MIN_HEIGHT);
                albumArtView.setFitWidth(ALBUM_VIEW_HEADER_MIN_HEIGHT * 0.73);
                albumArtView.setFitHeight(ALBUM_VIEW_HEADER_MIN_HEIGHT * 0.73);
                isViewChanged.set(true);
            } else if (event.getDeltaY() > 0 && isViewChanged.get()){
                albumViewHeader.setPrefHeight(ALBUM_VIEW_HEADER_MAX_HEIGHT);
                albumArtView.setFitWidth(ALBUM_VIEW_HEADER_MAX_HEIGHT * 0.73);
                albumArtView.setFitHeight(ALBUM_VIEW_HEADER_MAX_HEIGHT * 0.73);
                info_and_controls_pane.getChildren().add(2, albumInfoPane);
                isViewChanged.set(false);
            }
        });

        LOGGER.log(Level.INFO, "Tab Album View Loaded!");
    }

    public static AlbumViewTabController getInstance() {
        return instance;
    }

    public VBox getRoot() {
        return album_view;
    }

    @FXML
    private void handleBackOperation(ActionEvent actionEvent) {
        if (isViewRequestedFromArtist) {
            if (requesterArtist != null) {
                if (ArtistViewTabController.getInstance().loadArtist(this.requesterArtist, false, null)) {
                    this.isViewRequestedFromArtist = false;
                    this.requesterArtist = null;
                    BaseController.getInstance().handleShowArtistView();
                }
            }
        } else {
            BaseController.getInstance().getBtnAlbums().fire();
        }
    }

    @FXML
    private void handlePlayAllSongs(ActionEvent actionEvent) {
        ConsoleController.getInstance().load(this.album);
    }

    @FXML
    private void handleAddTo(ActionEvent actionEvent) {
    }

    @FXML
    private void handleShowArtist(ActionEvent actionEvent) {
        if (ArtistViewTabController.getInstance().loadArtist(this.artist, true, this.album)) {
            BaseController.getInstance().handleShowArtistView();
        }
    }

    @FXML
    private void handleEditAlbumInfo(ActionEvent actionEvent) {
    }

    @FXML
    private void handleDeleteAlbum(ActionEvent actionEvent) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(album_view.getScene().getWindow());
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setResizable(false);
        dialog.getDialogPane().setMaxWidth(500.0);

        Label header = new Label("Are you sure you want to delete this?");
        header.setFont(Font.font("Roboto", 20));
        header.setStyle("-fx-background-color: linear-gradient(to right, #badbf9 0%, #E0C3FC 42%, #f8fbc0 100%); -fx-padding: 20 0 10 20");
        dialog.getDialogPane().setHeader(header);

        Label content = new Label();
        content.setText("If you delete \"" + album.getAlbumName() + "\" it won't be on this device any more.");
        content.setWrapText(true);
        content.setMaxWidth(480.0);
        content.setMaxHeight(Double.MAX_VALUE);
        content.setFont(Font.font("Roboto", 18));
        content.setStyle("-fx-background-color: linear-gradient(to right, #badbf9 0%, #E0C3FC 42%, #f8fbc0 100%); -fx-padding: 10 20 40 20");
        dialog.getDialogPane().setContent(content);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get().equals(ButtonType.OK)) {
            for (Song song : songs) {
                try {
                    Files.deleteIfExists(Path.of(song.getLocation()));
                } catch (IOException ignored) {}
            }
            Inventory.removeAlbum(this.album);
        }
    }

    @FXML
    private void handleShowMyMusic(MouseEvent mouseEvent) {
        BaseController.getInstance().getBtnSongs().fire();
    }

    public boolean loadAlbum(Album album, boolean isViewRequestedFromArtist, Artist requesterArtist) {
        try {
            this.album = album;
            this.isViewRequestedFromArtist = isViewRequestedFromArtist;
            this.requesterArtist = requesterArtist;

            this.artist = Inventory.getArtist(this.album.getArtist());

            this.songs.clear();
            this.albumSongsList.getChildren().clear();
            this.songs.setAll(Inventory.getSongs(album));

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

            albumName.setText(album.getAlbumName());
            artistName.setText(album.getArtist());
            albumReleaseYear.setText(album.getReleaseYear());
            albumCategory.setText(Inventory.getGenreDescription(album.getGenre()));

            return true;
        } catch (Exception ex) {
            LOGGER.log(Level.TRACE, ex);
            ex.printStackTrace();
            return false;
        }
    }
}
