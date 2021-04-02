package org.signature.ui.audioPlayer;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.signature.dataModel.audioPlayer.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {

    private static final String DEFAULT_MUSIC_FOLDER = System.getProperty("user.home").concat(File.separator).concat("Music");

    private static final String DB_URL = "jdbc:h2:file:".concat(System.getProperty("user.dir")).concat(File.separator).concat("music");
    private static Session session = AudioPlayer.getInstance().getSession();

    public static String getDefaultMusicFolder() {
        return DEFAULT_MUSIC_FOLDER;
    }

    public static String getDBUrl() {
        return DB_URL;
    }

    public static Transaction startTransaction() {
        while (session == null) {
            session = AudioPlayer.getInstance().getSession();
        }

        if (session.getTransaction().isActive()) {
            return session.getTransaction();
        } else {
            return session.beginTransaction();
        }
    }

    private static final ObservableList<MusicLibrary> LIBRARIES = FXCollections.observableArrayList();

    public static ObservableList<MusicLibrary> getLibraries() {
        return LIBRARIES;
    }

    public static void setLibraries(List<MusicLibrary> libraryList) {
        try {
            LIBRARIES.setAll(libraryList);
        } catch (NullPointerException ignored) {
        }
    }

    public static boolean addLibrary(MusicLibrary libraryToAdd) {
        if (libraryToAdd != null) {
            for (MusicLibrary library : LIBRARIES) {
                if (library.getFolderLocation().equals(libraryToAdd.getFolderLocation())) {
                    return false;
                }
            }

            new Thread(() -> {
                Transaction transaction = startTransaction();
                try {
                    session.save(libraryToAdd);
                    LIBRARIES.add(libraryToAdd);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (transaction.isActive()) {
                        transaction.rollback();
                    }
                } finally {
                    if (transaction.isActive()) {
                        transaction.commit();
                    }
                }
            }).start();
        }

        return true;
    }

    public static void removeLibrary(int index) {
        if (index > -1) {
            new Thread(() -> {
                Transaction transaction = startTransaction();
                try {
                    session.delete(LIBRARIES.get(index - 1));
                    LIBRARIES.remove(index - 1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (transaction.isActive()) {
                        transaction.rollback();
                    }
                } finally {
                    if (transaction.isActive()) {
                        transaction.commit();
                    }
                }
            }).start();
        }
    }

    private static final ObservableList<Song> CACHED_SONGS = FXCollections.observableArrayList();

    public static ObservableList<Song> getCachedSongs() {
        return CACHED_SONGS;
    }

    public static void addSong(Song song) {
        try {
            CACHED_SONGS.add(song);
        } catch (NullPointerException ignored) {
        }
    }

    public static void removeSong(int index) {
        if (index > -1) {
            CACHED_SONGS.remove(index);
        }
    }

    public static List<Song> getSongs(String songTitle) {
        return CACHED_SONGS.filtered(song -> song.getTitle().startsWith(songTitle));
    }

    public static List<Song> getSongs(Album album) {
        return CACHED_SONGS.filtered(song -> song.getAlbum().equals(album.getAlbumName()));
    }

    public static List<Song> getSongs(Artist artist) {
        return CACHED_SONGS.filtered(song -> song.getArtist().equals(artist.getName()));
    }

    public static Song checkSong(Song song) {
        if (song != null) {
            for (Song cachedSong : CACHED_SONGS) {
                if (cachedSong.getTitle().equals(song.getTitle())) {
                    if (cachedSong.getLocation().equals(song.getLocation())) {
                        return cachedSong;
                    }
                }
            }
        }
        return null;
    }

    private static final ObservableList<Album> CACHED_ALBUMS = FXCollections.observableArrayList();

    public static ObservableList<Album> getCachedAlbums() {
        return CACHED_ALBUMS;
    }

    public static void addAlbum(Album album) {
        try {
            CACHED_ALBUMS.add(album);
        } catch (NullPointerException ignored) {
        }
    }

    public static void removeAlbum(int index) {
        if (index > -1) {
            CACHED_ALBUMS.remove(index);
        }
    }

    public static void removeAlbum(Album album) {
        try {
            CACHED_ALBUMS.remove(album);
        } catch (NullPointerException ignored) {
        }
    }

    public static Album getAlbum(String albumName) {
        for (Album album : CACHED_ALBUMS) {
            if (album.getAlbumName().equalsIgnoreCase(albumName)) {
                return album;
            }
        }
        return null;
    }

    public static List<Album> getAlbums(String albumName) {
        return CACHED_ALBUMS.filtered(album -> album.getAlbumName().startsWith(albumName));
    }

    public static List<Album> getAlbums(Artist artist) {
        return CACHED_ALBUMS.filtered(album -> album.getArtist().equals(artist.getName()));
    }

    public static Album getAlbum(RecentlyPlays recentlyPlays) {
        if (recentlyPlays != null) {
            for (Album album : CACHED_ALBUMS) {
                if (album.getAlbumName().equals(recentlyPlays.getAlbumName())) {
                    if (album.getArtist().equals(recentlyPlays.getArtist())) {
                        return album;
                    }
                }
            }
        }
        return null;
    }

    public static Album checkAlbum(Album album) {
        if (album != null) {
            for (Album cachedAlbum : CACHED_ALBUMS) {
                if (cachedAlbum.getAlbumName().equals(album.getAlbumName())) {
                    if (cachedAlbum.getAlbumArtist().equals(album.getAlbumArtist())) {
                        if (cachedAlbum.getReleaseYear().equals(album.getReleaseYear())) {
                            return album;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static final ObservableList<Artist> CACHED_ARTISTS = FXCollections.observableArrayList();

    public static ObservableList<Artist> getCachedArtists() {
        return CACHED_ARTISTS;
    }

    public static void addArtist(Artist artist) {
        try {
            CACHED_ARTISTS.add(artist);
        } catch (NullPointerException ignored) {
        }
    }

    public static void removeArtist(int index) {
        if (index > -1) {
            CACHED_ARTISTS.remove(index);
        }
    }

    public static Artist getArtist(String artistName) {
        for (Artist artist : CACHED_ARTISTS) {
            if (artist.getName().equalsIgnoreCase(artistName)) {
                return artist;
            }
        }
        return null;
    }

    public static List<Artist> getArtists(String artistName) {
        return CACHED_ARTISTS.filtered(artist -> artist.getName().startsWith(artistName));
    }

    public static Artist getArtist(RecentlyPlays recentlyPlays) {
        if (recentlyPlays != null) {
            for (Artist artist : CACHED_ARTISTS) {
                if (artist.getName().equals(recentlyPlays.getArtist())) {
                    return artist;
                }
            }
        }
        return null;
    }

    public static Artist checkArtist(Artist artist) {
        if (artist != null) {
            for (Artist cachedArtist : CACHED_ARTISTS) {
                if (cachedArtist.getName().equals(artist.getName())) {
                    return cachedArtist;
                }
            }
        }
        return null;
    }

    private static final Map<Integer, String> CACHED_GENRES = new HashMap<>();

    public static Map<Integer, String> getCachedGenres() {
        return CACHED_GENRES;
    }

    public static void addGenreDescription(int key, String description) {
        if (!CACHED_GENRES.containsValue(description)) {
            CACHED_GENRES.put(key, description);
        }
    }

    public static String getGenreDescription(int key) {
        if (key == 0) {
            return "Unknown Genre";
        } else {
            return CACHED_GENRES.get(key);
        }
    }

    private static final ObservableList<Playlist> CACHED_PLAYLISTS = FXCollections.observableArrayList();

    public static List<Playlist> getCachedPlaylist() {
        CACHED_PLAYLISTS.addListener((ListChangeListener<Playlist>) c -> {
            c.next();
            if (c.wasAdded()) {
                new Thread(() -> {
                    Transaction transaction = startTransaction();
                    try {
                        for (Playlist playlist : c.getAddedSubList()) {
                            session.save(playlist);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        if (transaction.isActive()) {
                            transaction.rollback();
                        }
                    } finally {
                        if (transaction.isActive()) {
                            transaction.commit();
                        }
                    }
                }).start();
            } else if (c.wasRemoved()) {
                new Thread(() -> {
                    Transaction transaction = startTransaction();
                    try {
                        for (Playlist playlist : c.getRemoved()) {
                            session.delete(playlist);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        if (transaction.isActive()) {
                            transaction.rollback();
                        }
                    } finally {
                        if (transaction.isActive()) {
                            transaction.commit();
                        }
                    }
                }).start();
            }
        });

        return CACHED_PLAYLISTS;
    }

    public static Playlist getPlaylist(String playlistName) {
        for (Playlist playlist : CACHED_PLAYLISTS) {
            if (playlist.getPlaylistName().equalsIgnoreCase(playlistName)) {
                return playlist;
            }
        }
        return null;
    }

    public static void addPlaylist(Playlist playlist) {
        try {
            if (!CACHED_PLAYLISTS.contains(playlist)) {
                CACHED_PLAYLISTS.add(playlist);
            }
        } catch (NullPointerException ignored) {
        }
    }

    public static void removePlaylist(Playlist playlist) {
        try {
            CACHED_PLAYLISTS.remove(playlist);
        } catch (NullPointerException ignored) {
        }
    }

    public static void removePlaylist(int index) {
        if (index > -1) {
            try {
                CACHED_PLAYLISTS.remove(index);
            } catch (IndexOutOfBoundsException e) {
                CACHED_PLAYLISTS.remove(index - 1);
            }
        }
    }

    private static final ObservableList<Object> CACHED_RECENTLY_PLAYED = FXCollections.observableArrayList();

    public static void addRecentlyPlayed(Object object) {
        assert object instanceof Album || object instanceof Artist || object instanceof OnlineSong;
        try {
            if (!CACHED_RECENTLY_PLAYED.contains(object)) {
                CACHED_RECENTLY_PLAYED.add(object);
            }
        } catch (NullPointerException ignored) {
        }
    }

    public static void insertRecentlyPlayed(Object object, int index) {
        assert object instanceof OnlineSong;
        try {
            if (!CACHED_RECENTLY_PLAYED.contains(object)) {
                CACHED_RECENTLY_PLAYED.add(index, object);
            }
        } catch (NullPointerException ignored) {
        }
    }

    public static ObservableList<Object> getCachedRecentlyPlayed() {
        CACHED_RECENTLY_PLAYED.addListener((ListChangeListener<Object>) c -> {
            c.next();
            if (c.wasAdded()) {
                new Thread(() -> {
                    Transaction transaction = startTransaction();
                    try {
                        for (Object object : c.getAddedSubList()) {
                            if (object instanceof Album || object instanceof Artist) {
                                session.save(new RecentlyPlays(object));
                            } else {
                                session.save((OnlineSong) object);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        if (transaction.isActive()) {
                            transaction.rollback();
                        }
                    } finally {
                        if (transaction.isActive()) {
                            transaction.commit();
                        }
                    }
                }).start();
            }
        });

        return CACHED_RECENTLY_PLAYED;
    }

    public static void getReady() {
        CACHED_SONGS.clear();
        CACHED_ALBUMS.clear();
        CACHED_ARTISTS.clear();
        CACHED_GENRES.clear();
        CACHED_RECENTLY_PLAYED.clear();
    }

    public static void rollbackTransaction() {
        AudioPlayer.getInstance().getSession().getTransaction().rollback();
    }

    public static void finishTransaction() {
        Transaction transaction = AudioPlayer.getInstance().getSession().getTransaction();
        if (transaction.isActive()) {
            transaction.commit();
        }
    }
}
