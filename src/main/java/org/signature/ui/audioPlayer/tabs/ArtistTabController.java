package org.signature.ui.audioPlayer.tabs;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import org.signature.dataModel.audioPlayer.Artist;
import org.signature.ui.audioPlayer.BaseController;
import org.signature.ui.audioPlayer.ConsoleController;
import org.signature.ui.audioPlayer.Inventory;
import org.signature.ui.audioPlayer.model.ArtistPane;
import org.signature.util.Utils;

import java.net.URL;
import java.util.ResourceBundle;

public class ArtistTabController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(ArtistTabController.class);

    private static ArtistTabController instance = null;

    @FXML
    private VBox tab_artist;
    @FXML
    private JFXButton btn_shuffle;
    @FXML
    private StackPane contentStack;
    @FXML
    private FlowPane artistsList;
    @FXML
    private Label btn_song, btn_album;

    private final ObservableList<ArtistPane> artists = FXCollections.observableArrayList();

    private boolean artistsLoaded = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        artistsList.getChildren().addListener((ListChangeListener<Node>) c -> {
            if (artistsList.getChildren().size() == 0 && !contentStack.getChildren().get(1).toString().contains("Label")) {
                Utils.flipStackPane(contentStack);
            } else if (artistsList.getChildren().size() > 0 && contentStack.getChildren().get(1).toString().contains("Label")) {
                Utils.flipStackPane(contentStack);
            }

            btn_shuffle.setText("Shuffle all(" + artistsList.getChildren().size() + ")");
        });

        btn_song.setOnMouseClicked(event -> BaseController.getInstance().getBtnSongs().fire());

        btn_album.setOnMouseClicked(event -> BaseController.getInstance().getBtnAlbums().fire());

        loadArtists();
        artistsList.getChildren().setAll(artists);
        LOGGER.log(Level.INFO, "Artist Tab Loaded !!");
    }

    public static ArtistTabController getInstance() {
        return instance;
    }

    public VBox getRoot() {
        return tab_artist;
    }

    private void loadArtists() {
        if (!artistsLoaded) {
            try {
                artistsList.getChildren().clear();
                artists.clear();

                int listSize = Inventory.getCachedArtists().size();

                for (Artist artist : Inventory.getCachedArtists()) {

                    if (artist.getName().isEmpty()) {
                        continue;
                    }

                    ArtistPane artistPane = new ArtistPane(artist);
                    artists.add(artistPane);
                    WelcomeScreenController.updateProgress(10.0 /listSize);
                }

                artists.sort((o1, o2) -> o1.getArtistName().compareToIgnoreCase(o2.getArtistName()));

                artistsLoaded = true;

                Inventory.getCachedArtists().addListener((ListChangeListener<Artist>) c -> {
                    c.next();
                    if (c.wasAdded()) {

                        for (Artist artist : c.getAddedSubList()) {
                            if (artist.getName().isEmpty()) {
                                continue;
                            }

                            ArtistPane artistPane = new ArtistPane(artist);
                            artists.add(artistPane);
                        }

                        artists.sort((o1, o2) -> o1.getArtistName().compareToIgnoreCase(o2.getArtistName()));

                        artistsList.getChildren().clear();
                        artistsList.getChildren().setAll(artists);

                    } else if (c.wasRemoved()) {

                        if (Inventory.getCachedArtists().size() == 0) {
                            artists.clear();
                            artistsList.getChildren().clear();
                        } else {
                            for (Artist artist : c.getRemoved()) {
                                artists.removeIf(artistPane -> artistPane.getArtistName().equals(artist.getName()));
                                artistsList.getChildren().removeIf(node -> ((ArtistPane) node).getArtistName().equals(artist.getName()));
                            }

                            artists.sort((o1, o2) -> o1.getArtistName().compareToIgnoreCase(o2.getArtistName()));

                            artistsList.getChildren().clear();
                            artistsList.getChildren().setAll(artists);
                        }
                    }
                });
            } catch (NullPointerException | IllegalStateException | UnsupportedOperationException | IllegalArgumentException e) {
                LOGGER.log(Level.ERROR, "Failed to load artist node! " + e.getLocalizedMessage(), e);
            }
        }
    }

    @FXML
    private void handleShuffleAll(ActionEvent actionEvent) {
        ConsoleController.getInstance().handleShuffleAll();
    }
}
