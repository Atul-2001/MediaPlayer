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

    private static final String DB_URL = "jdbc:h2:file:".concat(System.getProperty("user.dir")).concat(File.separator).concat("music");
    private static Session session = AudioPlayer.getInstance().getSession();

    public static String getDBUrl() {
        return DB_URL;
    }

    public static Transaction startTransaction() {
        while (session == null) {
            session = AudioPlayer.getInstance().getSession();
        }
        return session.beginTransaction();
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

    public static List<Song> getSongs(int albumID) {
        return CACHED_SONGS.filtered(song -> song.getAlbum() == albumID);
    }

    public static List<Song> getSongs(String songTitle) {
        return CACHED_SONGS.filtered(song -> song.getTitle().startsWith(songTitle));
    }

    public static Song checkSong(Song song) {
        if (song != null) {
            for (Song cachedSong : CACHED_SONGS) {
                if (cachedSong.getTitle().equalsIgnoreCase(song.getTitle())) {
                    if (cachedSong.getLocation().equalsIgnoreCase(song.getLocation())) {
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

    public static int getAlbumID(String albumName) {
        if (albumName != null) {
            for (Album album : CACHED_ALBUMS) {
                if (album.getAlbumName().equalsIgnoreCase(albumName)) {
                    return album.getId();
                }
            }
        }
        return -1;
    }

    public static Album getAlbum(int albumID) {
        for (Album album : CACHED_ALBUMS) {
            if (album.getId() == albumID) {
                return album;
            }
        }
        return CACHED_ALBUMS.get(0);
    }

    public static List<Album> getAlbums(String albumName) {
        return CACHED_ALBUMS.filtered(album -> album.getAlbumName().startsWith(albumName));
    }

    public static List<Album> getAlbums(int artistID) {
        return CACHED_ALBUMS.filtered(album -> album.getArtist() == artistID);
    }

    public static Album getAlbum(RecentlyPlays recentlyPlays) {
        if (recentlyPlays != null) {
            for (Album album : CACHED_ALBUMS) {
                if (album.getAlbumName().equalsIgnoreCase(recentlyPlays.getAlbumName())) {
                    Artist artist = getArtist(album.getArtist());
                    if (artist.getName().equalsIgnoreCase(recentlyPlays.getArtist())) {
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
                if (cachedAlbum.getAlbumName().equalsIgnoreCase(album.getAlbumName())) {
                    if (cachedAlbum.getAlbumArtist().equalsIgnoreCase(album.getAlbumArtist())) {
                        if (cachedAlbum.getReleaseYear().equalsIgnoreCase(album.getReleaseYear())) {
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

    public static int getArtistID(String artistName) {
        if (artistName != null) {
            for (Artist artist : CACHED_ARTISTS) {
                if (artist.getName().equalsIgnoreCase(artistName)) {
                    return artist.getId();
                }
            }
        }
        return -1;
    }

    public static Artist getArtist(int artistID) {
        for (Artist artist : CACHED_ARTISTS) {
            if (artist.getId() == artistID) {
                return artist;
            }
        }
        return CACHED_ARTISTS.get(0);
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
                if (cachedArtist.getName().equalsIgnoreCase(artist.getName())) {
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
        return CACHED_GENRES.get(key);
    }

    private static final ObservableList<Object> CACHED_RECENTLY_PLAYED = FXCollections.observableArrayList();

    public static void addRecentlyPlayed(Object object) {
        assert object instanceof Album || object instanceof Artist;
        try {
            if (!CACHED_RECENTLY_PLAYED.contains(object)) {
                CACHED_RECENTLY_PLAYED.add(object);
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

    private static final ObservableList<Song> CACHED_FAVOURITE_SONGS = FXCollections.observableArrayList();

    public static void addFavouriteSong(Song song) {
        try {
            if (!CACHED_FAVOURITE_SONGS.contains(song)) {
                CACHED_FAVOURITE_SONGS.add(song);
            }
        } catch (NullPointerException ignored) {
        }
    }

    public static void removeFavouriteSong(Song song) {
        try {
            CACHED_FAVOURITE_SONGS.remove(song);
        } catch (NullPointerException ignored) {
        }
    }

    public static void removeFavouriteSong(int index) {
        if (index > -1) {
            try {
                CACHED_FAVOURITE_SONGS.remove(index);
            } catch (IndexOutOfBoundsException e) {
                CACHED_FAVOURITE_SONGS.remove(index - 1);
            }
        }
    }

    public static ObservableList<Song> getCachedFavouriteSongs() {
        CACHED_FAVOURITE_SONGS.addListener((ListChangeListener<Song>) c -> {
            c.next();
            if (c.wasAdded()) {
                new Thread(() -> {
                    Transaction transaction = startTransaction();
                    try {
                        for (Song song : c.getAddedSubList()) {
                            session.save(song);
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
                        for (Song song : c.getRemoved()) {
                            session.delete(song);
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
        return CACHED_FAVOURITE_SONGS;
    }

    private static final ObservableList<Album> CACHED_FAVOURITE_ALBUMS = FXCollections.observableArrayList();

    public static void addFavouriteAlbum(Album album) {
        try {
            if (!CACHED_FAVOURITE_ALBUMS.contains(album)) {
                CACHED_FAVOURITE_ALBUMS.add(album);
            }
        } catch (NullPointerException ignored) {
        }
    }

    public static void removeFavouriteAlbum(Album album) {
        try {
            CACHED_FAVOURITE_ALBUMS.remove(album);
        } catch (NullPointerException ignored) {
        }
    }

    public static void removeFavouriteAlbum(int index) {
        if (index > -1) {
            try {
                CACHED_FAVOURITE_ALBUMS.remove(index);
            } catch (IndexOutOfBoundsException ex) {
                CACHED_FAVOURITE_ALBUMS.remove(index - 1);
            }
        }
    }

    public static ObservableList<Album> getCachedFavouriteAlbums() {
        CACHED_FAVOURITE_ALBUMS.addListener((ListChangeListener<Album>) c -> {
            c.next();
            if (c.wasAdded()) {
                new Thread(() -> {
                    Transaction transaction = startTransaction();
                    try {
                        for (Album album : c.getAddedSubList()) {
                            session.save(album);
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
                        for (Album album : c.getRemoved()) {
                            session.delete(album);
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
        return CACHED_FAVOURITE_ALBUMS;
    }

    private static final ObservableList<Artist> CACHED_FAVOURITE_ARTISTS = FXCollections.observableArrayList();

    public static void addFavouriteArtist(Artist artist) {
        try {
            if (!CACHED_FAVOURITE_ARTISTS.contains(artist)) {
                CACHED_FAVOURITE_ARTISTS.add(artist);
            }
        } catch (NullPointerException ignored) {
        }
    }

    public static void removeFavouriteArtist(Artist artist) {
        try {
            CACHED_FAVOURITE_ARTISTS.remove(artist);
        } catch (NullPointerException ignored) {
        }
    }

    public static void removeFavouriteArtist(int index) {
        if (index > -1) {
            try {
                CACHED_FAVOURITE_ARTISTS.remove(index);
            } catch (IndexOutOfBoundsException ex) {
                CACHED_FAVOURITE_ARTISTS.remove(index - 1);
            }
        }
    }

    public static ObservableList<Artist> getCachedFavouriteArtists() {
        CACHED_FAVOURITE_ARTISTS.addListener((ListChangeListener<Artist>) c -> {
            c.next();
            if (c.wasAdded()) {
                new Thread(() -> {
                    Transaction transaction = startTransaction();
                    try {
                        for (Artist artist : c.getAddedSubList()) {
                            session.save(artist);
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
                        for (Artist artist : c.getRemoved()) {
                            session.delete(artist);
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
        return CACHED_FAVOURITE_ARTISTS;
    }

    public static void getReady() {
        CACHED_SONGS.clear();
        CACHED_ALBUMS.clear();
        CACHED_ARTISTS.clear();
        CACHED_GENRES.clear();
        CACHED_RECENTLY_PLAYED.clear();
        CACHED_FAVOURITE_SONGS.clear();
        CACHED_FAVOURITE_ALBUMS.clear();
        CACHED_FAVOURITE_ARTISTS.clear();
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
