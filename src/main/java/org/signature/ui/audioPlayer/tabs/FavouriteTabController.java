package org.signature.ui.audioPlayer.tabs;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
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
import org.signature.ui.audioPlayer.BaseController;
import org.signature.ui.audioPlayer.Inventory;
import org.signature.ui.audioPlayer.model.AlbumPane;
import org.signature.ui.audioPlayer.model.ArtistPane;
import org.signature.ui.audioPlayer.model.PlaylistPane;
import org.signature.ui.audioPlayer.model.SongPane;
import org.signature.util.Utils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class FavouriteTabController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(FavouriteTabController.class);

    private static FavouriteTabController instance = null;

    @FXML
    private StackPane tab_favourite_stack;
    @FXML
    private VBox tab_favouritePlaylist, tab_favouriteSong, tab_favouriteAlbum, tab_favouriteArtist, tab_favourite;
    @FXML
    private StackPane contentStack;
    @FXML
    private VBox tabsPreviewList;
    @FXML
    private VBox preview_favouriteArtists, preview_favouriteAlbums, preview_favouriteSongs, preview_favouritePlaylists;
    @FXML
    private VBox list_songs, mirror_list_songs;
    @FXML
    private HBox list_artists, list_albums, list_playlists;
    @FXML
    private FlowPane mirror_list_artists, mirror_list_albums, mirror_list_playlists;

    private final ObservableList<Song> songs = FXCollections.observableArrayList();
    private final ObservableList<Album> albums = FXCollections.observableArrayList();
    private final ObservableList<Artist> artists = FXCollections.observableArrayList();

    private boolean isListLoaded = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        tabsPreviewList.getChildren().removeAll(preview_favouriteArtists, preview_favouriteAlbums, preview_favouriteSongs, preview_favouritePlaylists);
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

                for (Song song : songs) {
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

                if (isListLoaded) {
                    for (Song song : c.getAddedSubList()) {
                        if (song.getTitle().isEmpty()) {
                            continue;
                        }

                        Inventory.addFavouriteSong(song);
                    }
                }

                if (!tabsPreviewList.getChildren().contains(preview_favouriteSongs)) {
                    if (list_songs.getChildren().size() > 0) {
                        if (tabsPreviewList.getChildren().contains(preview_favouritePlaylists)) {
                            tabsPreviewList.getChildren().add(2, preview_favouriteSongs);
                        } else {
                            tabsPreviewList.getChildren().add(preview_favouriteSongs);
                        }
                    }
                }
            } else if (c.wasRemoved()) {
                if (tabsPreviewList.getChildren().contains(preview_favouriteSongs)) {
                    if (list_songs.getChildren().size() == 0) {
                        tabsPreviewList.getChildren().remove(preview_favouriteSongs);
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

                for (Album album : albums) {
                    if (album.getAlbumName().isEmpty()) {
                        continue;
                    }
                    list_albums.getChildren().add(new AlbumPane(album));
                    mirror_list_albums.getChildren().add(new AlbumPane(album));
                }

                if (isListLoaded) {
                    for (Album album : c.getAddedSubList()) {
                        if (album.getAlbumName().isEmpty()) {
                            continue;
                        }

                        Inventory.addFavouriteAlbum(album);
                    }
                }

                if (!tabsPreviewList.getChildren().contains(preview_favouriteAlbums)) {
                    if (list_albums.getChildren().size() > 0) {
                        if (tabsPreviewList.getChildren().contains(preview_favouriteArtists)) {
                            tabsPreviewList.getChildren().add(1, preview_favouriteAlbums);
                        } else {
                            tabsPreviewList.getChildren().add(0, preview_favouriteAlbums);
                        }
                    }
                }
            } else if (c.wasRemoved()) {
                if (tabsPreviewList.getChildren().contains(preview_favouriteAlbums)) {
                    if (list_albums.getChildren().size() == 0) {
                        tabsPreviewList.getChildren().remove(preview_favouriteAlbums);
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

                for (Artist artist : artists) {
                    if (artist.getName().isEmpty()) {
                        continue;
                    }
                    list_artists.getChildren().add(new ArtistPane(artist));
                    mirror_list_artists.getChildren().add(new ArtistPane(artist));
                }

                if (isListLoaded) {
                    for (Artist artist : c.getAddedSubList()) {
                        if (artist.getName().isEmpty()) {
                            continue;
                        }

                        Inventory.addFavouriteArtist(artist);
                    }
                }

                if (!tabsPreviewList.getChildren().contains(preview_favouriteArtists)) {
                    if (list_artists.getChildren().size() > 0) {
                        tabsPreviewList.getChildren().add(0, preview_favouriteArtists);
                    }
                }
            } else if (c.wasRemoved()) {
                if (tabsPreviewList.getChildren().contains(preview_favouriteArtists)) {
                    if (list_artists.getChildren().size() == 0) {
                        tabsPreviewList.getChildren().remove(preview_favouriteArtists);
                    }
                }
            }
        });

        loadFavourites();

        tab_favourite_stack.opacityProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == 0.0) {
                handleBackOperation(null);
            }
        });

        LOGGER.log(Level.INFO, "Favourite Tab Loaded !!");
    }

    public static FavouriteTabController getInstance() {
        return instance;
    }

    public StackPane getRoot() {
        return tab_favourite_stack;
    }

    private void loadFavourites() {
        if (!isListLoaded) {
            list_artists.getChildren().clear();
            mirror_list_artists.getChildren().clear();
            artists.setAll(Inventory.getCachedFavouriteArtists());

            list_albums.getChildren().clear();
            mirror_list_albums.getChildren().clear();
            albums.setAll(Inventory.getCachedFavouriteAlbums());

            list_songs.getChildren().clear();
            mirror_list_songs.getChildren().clear();
            songs.setAll(Inventory.getCachedFavouriteSongs());

            loadPlaylists();
            isListLoaded = true;
        }
    }

    private void loadPlaylists() {
        try {
            list_playlists.getChildren().clear();
            mirror_list_playlists.getChildren().clear();

            for (int i = 0; i < 4; i++) {
                PlaylistPane playlist = new PlaylistPane(String.valueOf(i+1), (i+1)*10);
                list_playlists.getChildren().add(playlist);

                playlist = new PlaylistPane(String.valueOf(i+1), (i+1)*10);
                mirror_list_playlists.getChildren().add(playlist);
            }
        } catch (NullPointerException | IllegalStateException | UnsupportedOperationException | IllegalArgumentException e) {
            LOGGER.log(Level.ERROR, "Failed to load Playlist node! " + e.getLocalizedMessage(), e);
        }
    }

    @FXML
    private void handleShowMyMusic(MouseEvent mouseEvent) {
        BaseController.getInstance().getBtnSongs().fire();
    }

    @FXML
    private void handleShowAllArtists(ActionEvent actionEvent) {
        Utils.swapTabStack(tab_favourite_stack, tab_favouriteArtist);
    }

    @FXML
    private void handleShowAllAlbums(ActionEvent actionEvent) {
        Utils.swapTabStack(tab_favourite_stack, tab_favouriteAlbum);
    }

    @FXML
    private void handleShowAllSongs(ActionEvent actionEvent) {
        Utils.swapTabStack(tab_favourite_stack, tab_favouriteSong);
    }

    @FXML
    private void handleShowAllPlaylists(ActionEvent actionEvent) {
        Utils.swapTabStack(tab_favourite_stack, tab_favouritePlaylist);
    }

    @FXML
    private void handleBackOperation(ActionEvent actionEvent) {
        Utils.swapTabStack(tab_favourite_stack, tab_favourite);
    }

    public void setSongs(List<Song> songs) {
        try {
            list_songs.getChildren().clear();
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
            Inventory.removeFavouriteSong(song);
        } catch (NullPointerException | IndexOutOfBoundsException ignored) {}
    }

    public void setAlbums(List<Album> albums) {
        try {
            list_albums.getChildren().clear();
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
            Inventory.removeFavouriteAlbum(album);
        } catch (NullPointerException | IndexOutOfBoundsException ignored) {}
    }

    public void setArtists(List<Artist> artists) {
        try {
            list_artists.getChildren().clear();
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
            Inventory.removeFavouriteArtist(artist);
        } catch (NullPointerException ignored) {}
    }
}
