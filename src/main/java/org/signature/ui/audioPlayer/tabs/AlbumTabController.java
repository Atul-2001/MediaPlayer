package org.signature.ui.audioPlayer.tabs;

import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.WelcomeScreenController;
import org.signature.dataModel.audioPlayer.Album;
import org.signature.ui.audioPlayer.BaseController;
import org.signature.ui.audioPlayer.Inventory;
import org.signature.ui.audioPlayer.model.AlbumPane;
import org.signature.util.Utils;

import java.net.URL;
import java.util.ResourceBundle;

public class AlbumTabController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(AlbumTabController.class);

    private static AlbumTabController instance = null;

    @FXML
    private VBox tab_album;
    @FXML
    private FlowPane albumsList;
    @FXML
    private JFXComboBox<String> sortCriteria, genreList;
    @FXML
    private StackPane contentStack;
    @FXML
    private Label btn_song, btn_artist;

    private final ObservableList<AlbumPane> albums = FXCollections.observableArrayList();

    private boolean albumsLoaded = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        albumsList.getChildren().addListener((ListChangeListener<Node>) c -> {
            if (albumsList.getChildren().size() == 0 && !contentStack.getChildren().get(1).toString().contains("Label")) {
                Utils.flipStackPane(contentStack);
            } else if (albumsList.getChildren().size() > 0 && contentStack.getChildren().get(1).toString().contains("Label")) {
                Utils.flipStackPane(contentStack);
            }
        });

        btn_song.setOnMouseClicked(event -> BaseController.getInstance().getBtnSongs().fire());

        btn_artist.setOnMouseClicked(event -> BaseController.getInstance().getBtnArtists().fire());

        genreList.getItems().addAll(Inventory.getCachedGenres().values());

        sortCriteria.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
           albumsList.getChildren().clear();
           albums.sort((o1, o2) -> {
               if (newValue.intValue() == 0) {
                   return o1.getCreationTime().compareToIgnoreCase(o2.getCreationTime());
               } else if (newValue.intValue() == 1) {
                   return o1.getAlbumName().compareToIgnoreCase(o2.getAlbumName());
               } else if (newValue.intValue() == 2) {
                   return o1.getReleaseYear().compareToIgnoreCase(o2.getReleaseYear());
               } else if (newValue.intValue() == 3) {
                   return o1.getArtist().compareToIgnoreCase(o2.getArtist());
               } else {
                   return 1;
               }
           });
           albumsList.getChildren().setAll(albums);
        });

        genreList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            albumsList.getChildren().clear();
            albumsList.getChildren().setAll(albums.filtered(albumPane -> {
                if (newValue.equalsIgnoreCase(genreList.getItems().get(0))) {
                    return true;
                } else {
                    return newValue.equalsIgnoreCase(albumPane.getGenre());
                }
            }));
        });

        loadAlbums();
//        albumsList.getChildren().setAll(albums);
        LOGGER.log(Level.INFO, "Album Tab Loaded !!");
    }

    public static AlbumTabController getInstance() {
        return instance;
    }

    public VBox getRoot() {
        return tab_album;
    }

    private void loadAlbums() {
        if (!albumsLoaded) {
            try {
                albumsList.getChildren().clear();
                int listSize = Inventory.getCachedAlbums().size();

                for (Album album : Inventory.getCachedAlbums()) {

                    if (album.getAlbumName().isEmpty()) {
                        continue;
                    }

                    AlbumPane albumPane = new AlbumPane(album);
                    albums.add(albumPane);
                    WelcomeScreenController.updateProgress(10.0/listSize);
                }

                sortCriteria.getSelectionModel().select(0);
                genreList.getSelectionModel().select(0);
                albumsLoaded = true;
            } catch (NullPointerException | IllegalStateException | UnsupportedOperationException | IllegalArgumentException e) {
                LOGGER.log(Level.ERROR, "Failed to load album node! " + e.getLocalizedMessage(), e);
            }
        }
    }
}
