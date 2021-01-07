package org.signature.ui.audioPlayer.tabs;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.dataModel.audioPlayer.Album;
import org.signature.dataModel.audioPlayer.Artist;
import org.signature.dataModel.audioPlayer.Song;
import org.signature.ui.audioPlayer.model.AlbumPane;
import org.signature.ui.audioPlayer.model.ArtistPane;
import org.signature.ui.audioPlayer.model.SongPane;
import org.signature.util.Utils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchResultTabController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(SearchResultTabController.class);

    private static SearchResultTabController instance = null;

    @FXML
    private StackPane tab_searchResult;
    @FXML
    private VBox tab_resultPlaylist, tab_resultSong, tab_resultAlbum, tab_resultArtist, tab_result;
    @FXML
    private Label searchKey;
    @FXML
    private StackPane contentStack;
    @FXML
    private VBox tabsPreviewList;
    @FXML
    private VBox preview_resultArtist, preview_resultAlbum, preview_resultSong, preview_resultPlaylist;
    @FXML
    private VBox list_songs, mirror_list_songs;
    @FXML
    private HBox list_artists, list_albums, list_playlists;
    @FXML
    private FlowPane mirror_list_artists, mirror_list_albums, mirror_list_playlists;

    private final ObservableList<Song> songs = FXCollections.observableArrayList();
    private final ObservableList<Album> albums = FXCollections.observableArrayList();
    private final ObservableList<Artist> artists = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        tabsPreviewList.getChildren().removeAll(preview_resultArtist, preview_resultAlbum, preview_resultSong, preview_resultPlaylist);
        tabsPreviewList.getChildren().addListener((ListChangeListener<Node>) c -> {
            if (tabsPreviewList.getChildren().size() == 0 && !contentStack.getChildren().get(1).toString().contains("VBox")) {
                Utils.flipStackPane(contentStack);
            } else if (tabsPreviewList.getChildren().size() > 0 && contentStack.getChildren().get(1).toString().contains("VBox")) {
                Utils.flipStackPane(contentStack);
            }
        });

        var i = new AtomicInteger(0);
        songs.addListener((ListChangeListener<Song>) c -> {
            c.next();
            if (c.wasAdded()) {
                list_songs.getChildren().clear();
                mirror_list_songs.getChildren().clear();
                songs.sort((o1, o2) -> o1.getTitle().compareToIgnoreCase(o2.getTitle()));

                for (Song song : c.getAddedSubList()) {
                    if (song.getTitle().isEmpty()) {
                        continue;
                    }

                    SongPane songPane = new SongPane(song);
                    if (i.intValue() %2 == 0) {
                        songPane.getStyleClass().add("songNodeEVEN");
                    } else {
                        songPane.getStyleClass().add("songNodeODD");
                    }
                    list_songs.getChildren().add(songPane);

                    songPane = new SongPane(song);
                    if (i.intValue() %2 == 0) {
                        songPane.getStyleClass().add("songNodeEVEN");
                    } else {
                        songPane.getStyleClass().add("songNodeODD");
                    }
                    mirror_list_songs.getChildren().add(songPane);
                    i.incrementAndGet();
                }

                if (!tabsPreviewList.getChildren().contains(preview_resultSong)) {
                    if (list_songs.getChildren().size() > 0) {
                        if (tabsPreviewList.getChildren().contains(preview_resultPlaylist)) {
                            tabsPreviewList.getChildren().add(2, preview_resultSong);
                        } else {
                            tabsPreviewList.getChildren().add(preview_resultSong);
                        }
                    }
                }
            } else if (c.wasRemoved()) {
                if (tabsPreviewList.getChildren().contains(preview_resultSong)) {
                    if (list_songs.getChildren().size() == 0) {
                        tabsPreviewList.getChildren().remove(preview_resultSong);
                    }
                }
            }
        });

        albums.addListener((ListChangeListener<Album>) c -> {
            c.next();
            if (c.wasAdded()) {
                list_albums.getChildren().clear();
                mirror_list_albums.getChildren().clear();
                albums.sort((o1, o2) -> o1.getAlbumName().compareToIgnoreCase(o2.getAlbumName()));

                for (Album album : c.getAddedSubList()) {
                    if (album.getAlbumName().isEmpty()) {
                        continue;
                    }
                    list_albums.getChildren().add(new AlbumPane(album));
                    mirror_list_albums.getChildren().add(new AlbumPane(album));
                }

                if (!tabsPreviewList.getChildren().contains(preview_resultAlbum)) {
                    if (list_albums.getChildren().size() > 0) {
                        if (tabsPreviewList.getChildren().contains(preview_resultArtist)) {
                            tabsPreviewList.getChildren().add(1, preview_resultAlbum);
                        } else {
                            tabsPreviewList.getChildren().add(0, preview_resultAlbum);
                        }
                    }
                }
            } else if (c.wasRemoved()) {
                if (tabsPreviewList.getChildren().contains(preview_resultAlbum)) {
                    if (list_albums.getChildren().size() == 0) {
                        tabsPreviewList.getChildren().remove(preview_resultAlbum);
                    }
                }
            }
        });

        artists.addListener((ListChangeListener<Artist>) c -> {
            c.next();
            if (c.wasAdded()) {
                list_artists.getChildren().clear();
                mirror_list_artists.getChildren().clear();
                artists.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));

                for (Artist artist : c.getAddedSubList()) {
                    if (artist.getName().isEmpty()) {
                        continue;
                    }
                    list_artists.getChildren().add(new ArtistPane(artist));
                    mirror_list_artists.getChildren().add(new ArtistPane(artist));
                }

                if (!tabsPreviewList.getChildren().contains(preview_resultArtist)) {
                    if (list_artists.getChildren().size() > 0) {
                        tabsPreviewList.getChildren().add(0, preview_resultArtist);
                    }
                }
            } else if (c.wasRemoved()) {
                if (tabsPreviewList.getChildren().contains(preview_resultArtist)) {
                    if (list_artists.getChildren().size() == 0) {
                        tabsPreviewList.getChildren().remove(preview_resultArtist);
                    }
                }
            }
        });

        tab_searchResult.opacityProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == 0.0) {
                handleBackOperation(null);
            }
        });

        LOGGER.log(Level.INFO, "'Search Result' Tab Loaded !!");
    }

    public static SearchResultTabController getInstance() {
        return instance;
    }

    public StackPane getRoot() {
        return tab_searchResult;
    }

    @FXML
    private void handleShowAllArtists(ActionEvent actionEvent) {
        Utils.swapTabStack(tab_searchResult, tab_resultArtist);
    }

    @FXML
    private void handleShowAllAlbums(ActionEvent actionEvent) {
        Utils.swapTabStack(tab_searchResult, tab_resultAlbum);
    }

    @FXML
    private void handleShowAllSongs(ActionEvent actionEvent) {
        Utils.swapTabStack(tab_searchResult, tab_resultSong);
    }

    @FXML
    private void handleShowAllPlaylists(ActionEvent actionEvent) {
        Utils.swapTabStack(tab_searchResult, tab_resultPlaylist);
    }

    public void handleBackOperation(ActionEvent actionEvent) {
        Utils.swapTabStack(tab_searchResult, tab_result);
    }

    public void setQueryString(String query) {
        if (query != null) {
            this.searchKey.setText(query);
        }
    }

    public void setSongs(List<Song> songs) {
        try {
            list_songs.getChildren().clear();
            mirror_list_songs.getChildren().clear();
            this.songs.setAll(songs);
        } catch (NullPointerException ignored) {}
    }

    public void addSong(Song song) {
        try {
            if (!songs.contains(song)) {
                songs.add(song);
            }
        } catch (NullPointerException ignored) {}
    }

    public void removeSong(Song song) {
        try {
            list_songs.getChildren().remove(songs.indexOf(song));
            mirror_list_songs.getChildren().remove(songs.indexOf(song));
            songs.remove(song);
        } catch (NullPointerException | IndexOutOfBoundsException ignored) {}
    }

    public void setAlbums(List<Album> albums) {
        try {
            list_albums.getChildren().clear();
            mirror_list_albums.getChildren().clear();
            this.albums.setAll(albums);
        } catch (NullPointerException ignored) {}
    }

    public void addAlbum(Album album) {
        try {
            if (!albums.contains(album)) {
                albums.add(album);
            }
        } catch (NullPointerException ignored) {}
    }

    public void removeAlbum(Album album) {
        try {
            list_albums.getChildren().remove(albums.indexOf(album));
            mirror_list_albums.getChildren().remove(albums.indexOf(album));
            albums.remove(album);
        } catch (NullPointerException | IndexOutOfBoundsException ignored) {}
    }

    public void setArtists(List<Artist> artists) {
        try {
            list_artists.getChildren().clear();
            mirror_list_artists.getChildren().clear();
            this.artists.setAll(artists);
        } catch (NullPointerException ignored) {}
    }

    public void addArtist(Artist artist) {
        try {
            if (!artists.contains(artist)) {
                artists.add(artist);
            }
        } catch (NullPointerException ignored) {}
    }

    public void removeArtist(Artist artist) {
        try {
            list_artists.getChildren().remove(artists.indexOf(artist));
            mirror_list_artists.getChildren().remove(artists.indexOf(artist));
            artists.remove(artist);
        } catch (NullPointerException ignored) {}
    }
}
