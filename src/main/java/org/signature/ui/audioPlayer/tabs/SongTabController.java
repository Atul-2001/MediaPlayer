package org.signature.ui.audioPlayer.tabs;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.WelcomeScreenController;
import org.signature.dataModel.audioPlayer.Song;
import org.signature.ui.audioPlayer.BaseController;
import org.signature.ui.audioPlayer.ConsoleController;
import org.signature.ui.audioPlayer.Inventory;
import org.signature.ui.audioPlayer.model.SongPane;
import org.signature.util.Utils;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class SongTabController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(SongTabController.class);

    private static SongTabController instance = null;

    @FXML
    private VBox tab_song;
    @FXML
    private Label btn_artist, btn_album;
    @FXML
    private JFXButton btn_shuffle;
    @FXML
    private JFXComboBox<String> sortCriteria, genreList;
    @FXML
    private StackPane contentStack;
    @FXML
    private VBox songsList;

    private final ObservableList<SongPane> songs = FXCollections.observableArrayList();

    private boolean songsLoaded = false;

    private final DoubleProperty tabWidth = new SimpleDoubleProperty(0.0);
    private final BooleanProperty showSelectionBox = new SimpleBooleanProperty(false);
    private final IntegerProperty selectedSongs = new SimpleIntegerProperty(0);

    private final IntegerProperty songNameFieldWidth = new SimpleIntegerProperty(0);
    private final IntegerProperty artistNameFieldWidth = new SimpleIntegerProperty(0);
    private final IntegerProperty albumNameFieldWidth = new SimpleIntegerProperty(0);
    private final IntegerProperty genreFieldWidth = new SimpleIntegerProperty(0);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (location != null && instance == null) {
            instance = this;
        }

        songsList.getChildren().addListener((ListChangeListener<Node>) c -> {
            if (songsList.getChildren().size() == 0 && !contentStack.getChildren().get(1).toString().contains("Label")) {
                Utils.flipStackPane(contentStack);
            } else if (songsList.getChildren().size() > 0 && contentStack.getChildren().get(1).toString().contains("Label")) {
                Utils.flipStackPane(contentStack);
            }
            btn_shuffle.setText("Shuffle all(" + songsList.getChildren().size() + ")");
        });

        btn_artist.setOnMouseClicked(event -> BaseController.getInstance().getBtnArtists().fire());

        btn_album.setOnMouseClicked(event -> BaseController.getInstance().getBtnAlbums().fire());

        tab_song.widthProperty().addListener((observable, oldValue, newValue) -> tabWidth.set(newValue.doubleValue()));

        selectedSongs.addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() <= 0) {
                showSelectionBox.set(false);
            }
        });

        genreList.getItems().addAll(Inventory.getCachedGenres().values());

        sortCriteria.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            songsList.getChildren().clear();
            songs.sort((o1, o2) -> {
                        if (newValue.intValue() == 0) {
                            return o1.getCreationTime().compareToIgnoreCase(o2.getCreationTime());
                        } else if (newValue.intValue() == 1) {
                            return o1.getSongTitle().compareToIgnoreCase(o2.getSongTitle());
                        } else if (newValue.intValue() == 2) {
                            return o1.getArtist().compareToIgnoreCase(o2.getArtist());
                        } else if (newValue.intValue() == 3) {
                            return o1.getAlbum().compareToIgnoreCase(o2.getAlbum());
                        } else {
                            return 1;
                        }
                    }
            );
            songsList.getChildren().setAll(songs);
            sortCriteria.getTooltip().setText(sortCriteria.getItems().get(newValue.intValue()));
        });

        genreList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            songsList.getChildren().clear();
            songsList.getChildren().setAll(songs.filtered(songPane -> {
                if (newValue.equalsIgnoreCase(genreList.getItems().get(0))) {
                    return true;
                } else {
                    return newValue.equalsIgnoreCase(songPane.getGenre());
                }
            }));
            genreList.getTooltip().setText(newValue);
        });

        loadSongs();
        songsList.getChildren().setAll(songs);

        LOGGER.log(Level.INFO, "Song Tab Loaded !!");
    }

    public static SongTabController getInstance() {
        return instance;
    }

    public VBox getRoot() {
        return tab_song;
    }

    public DoubleProperty getTabWidth() {
        return tabWidth;
    }

    public BooleanProperty getShowSelectionBox() {
        return showSelectionBox;
    }

    public void addSelectedSongs() {
        selectedSongs.set(selectedSongs.get() + 1);
    }

    public void removeSelectedSong() {
        selectedSongs.set(selectedSongs.get() - 1);
    }

    private void loadSongs() {
        if (!songsLoaded) {
            try {
                songsList.getChildren().clear();
                songs.clear();

                AtomicInteger i = new AtomicInteger(0);
                int listSize = Inventory.getCachedSongs().size();

                for (Song song : Inventory.getCachedSongs()) {

                    if (song.getTitle().isEmpty()) {
                        continue;
                    }

                    SongPane songPane = new SongPane(song);
                    if (i.getAndIncrement() %2 == 0) {
                        songPane.getStyleClass().add("songNodeEVEN");
                    } else {
                        songPane.getStyleClass().add("songNodeODD");
                    }
                    songs.add(songPane);
                    WelcomeScreenController.updateProgress(10.0 /listSize);
                }

                sortCriteria.getSelectionModel().select(0);
                genreList.getSelectionModel().select(0);
                songsLoaded = true;

                Inventory.getCachedSongs().addListener((ListChangeListener<Song>) c -> {
                    c.next();
                    if (c.wasAdded()) {

                        for (Song song : c.getAddedSubList()) {

                            if (song.getTitle().isEmpty()) {
                                continue;
                            }

                            SongPane songPane = new SongPane(song);
                            if (i.getAndIncrement() %2 == 0) {
                                songPane.getStyleClass().add("songNodeEVEN");
                            } else {
                                songPane.getStyleClass().add("songNodeODD");
                            }
                            songs.add(songPane);
                        }

                        sortCriteria.getSelectionModel().select(sortCriteria.getSelectionModel().getSelectedIndex());
                        genreList.getSelectionModel().select(genreList.getSelectionModel().getSelectedIndex());

                    } else if (c.wasRemoved()) {

                        if (Inventory.getCachedSongs().size() == 0) {
                            songs.clear();
                            songsList.getChildren().clear();
                        } else {
                            for (Song song : c.getRemoved()) {
                                songs.removeIf(songPane -> songPane.getSongTitle().equals(song.getTitle()));
                                songsList.getChildren().removeIf(node -> ((SongPane) node).getSongTitle().equals(song.getTitle()));
                            }

                            sortCriteria.getSelectionModel().select(sortCriteria.getSelectionModel().getSelectedIndex());
                            genreList.getSelectionModel().select(genreList.getSelectionModel().getSelectedIndex());
                        }
                    }
                });
            } catch (NullPointerException | IllegalStateException | UnsupportedOperationException | IllegalArgumentException e) {
                LOGGER.log(Level.ERROR, "Failed to load song node! " + e.getLocalizedMessage(), e);
            }
        }
    }

    @FXML
    private void handleShuffleAll(ActionEvent actionEvent) {
        ConsoleController.getInstance().handleShuffleAll();
    }

    @FXML
    private void handleClosePopup(ActionEvent actionEvent) {
        tab_song.getChildren().remove(4);
    }

    public IntegerProperty songNameFieldWidthProperty() {
        return songNameFieldWidth;
    }

    public void setSongNameFieldWidth(int newWidth) {
        if (newWidth != songNameFieldWidth.get()) {
            songNameFieldWidth.set(Math.max(newWidth, songNameFieldWidth.get()));
        }
    }

    public IntegerProperty artistNameFieldWidthProperty() {
        return artistNameFieldWidth;
    }

    public void setArtistNameFieldWidth(int newWidth) {
        if (newWidth != artistNameFieldWidth.get()) {
            artistNameFieldWidth.set(Math.max(newWidth, artistNameFieldWidth.get()));
            balanceFieldWidth();
        }
    }

    public IntegerProperty albumNameFieldWidthProperty() {
        return albumNameFieldWidth;
    }

    public void setAlbumNameFieldWidth(int newWidth) {
        if (newWidth != albumNameFieldWidth.get()) {
            albumNameFieldWidth.set(Math.max(newWidth, albumNameFieldWidth.get()));
            balanceFieldWidth();
        }
    }

    public IntegerProperty genreFieldWidthProperty() {
        return genreFieldWidth;
    }

    public void setGenreFieldWidth(int newWidth) {
        if (newWidth != genreFieldWidth.get()) {
            genreFieldWidth.set(Math.max(newWidth, genreFieldWidth.get()));
            balanceFieldWidth();
        }
    }

    private void balanceFieldWidth() {
        int artistWidth = artistNameFieldWidth.get();
        int albumWidth = albumNameFieldWidth.get();
        int genreWidth = genreFieldWidth.get();

        if (artistWidth != albumWidth || artistWidth != genreWidth) {
            int width = Math.max(Math.max(artistWidth, albumWidth), genreWidth);
            artistNameFieldWidth.set(width);
            albumNameFieldWidth.set(width);
            genreFieldWidth.set(width);
        }
    }
}
