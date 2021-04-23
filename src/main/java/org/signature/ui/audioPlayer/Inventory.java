package org.signature.ui.audioPlayer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.signature.dataModel.audioPlayer.*;
import org.signature.ui.audioPlayer.tabs.RecentlyPlayedTabController;

import java.io.File;
import java.nio.file.attribute.FileTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class Inventory {

    private static final Logger LOGGER = LogManager.getLogger(Inventory.class);

    private static final String DEFAULT_MUSIC_FOLDER = System.getProperty("user.home").concat(File.separator).concat("Music");

    private static final String DB_URL = "jdbc:h2:file:".concat(System.getProperty("user.dir")).concat(File.separator).concat("music");

    private static final ExecutorService databaseServicePool = Executors.newCachedThreadPool(new ThreadFactory() {
        final AtomicInteger counter = new AtomicInteger();

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "Database Service Pool--Thread-" + counter);
        }
    });

    public static String getDefaultMusicFolder() {
        return DEFAULT_MUSIC_FOLDER;
    }

    public static String getDBUrl() {
        return DB_URL;
    }

    public static ExecutorService getDatabaseServicePool() {
        return databaseServicePool;
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

    public static int getLibraryId(String libraryPath) {
        if (libraryPath != null) {
            for (MusicLibrary library : LIBRARIES) {
                if (library.getFolderLocation().equals(libraryPath)) {
                    return library.getId();
                }
            }
        }
        return 0;
    }

    public static boolean addLibrary(MusicLibrary libraryToAdd) {
        if (libraryToAdd != null) {
            for (MusicLibrary library : LIBRARIES) {
                if (library.getFolderLocation().equals(libraryToAdd.getFolderLocation())) {
                    return false;
                }
            }

            databaseServicePool.submit(() -> {
                Session session = AudioPlayer.getInstance().getSession();
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();
                    session.save(libraryToAdd);
                    tx.commit();
                    Platform.runLater(() -> LIBRARIES.add(libraryToAdd));
                } catch (Exception ex) {
                    if (tx != null) {
                        tx.rollback();
                        Platform.runLater(() -> LIBRARIES.remove(libraryToAdd));
                    }
                    LOGGER.log(Level.ERROR, "Exception while adding music library: ", ex);
                } finally {
                    session.close();
                }
            });
        }

        return true;
    }

    public static void removeLibrary(int index) {
        if (index > -1) {
            databaseServicePool.submit(() -> {
                Session session = AudioPlayer.getInstance().getSession();
                Transaction tx = null;
                MusicLibrary library = LIBRARIES.get(index);

                try {
                    tx = session.beginTransaction();
                    session.delete(library);
                    tx.commit();
                    Platform.runLater(() -> {
                        LIBRARIES.remove(library);

                        int libraryId = library.getId();
                        Song song;
                        for (Iterator<Song> songIterator = CACHED_SONGS.iterator(); songIterator.hasNext(); ) {
                            song = songIterator.next();
                            if (song.getMusicLibraryId() == libraryId) {
                                Album album = getAlbum(song.getAlbum(), song.getAlbumArtist());
                                if (album != null) {
                                    if (Inventory.getSongs(album).size() == 1) {
                                        Artist artist = getArtist(song.getArtist());
                                        if (artist != null) {
                                            if (Inventory.getAlbums(artist).size() == 1 && Inventory.getSongs(artist).size() == 1) {
                                                removeArtist(artist);
                                            }
                                        }
                                        removeAlbum(album);
                                    }
                                }
                                songIterator.remove();
                            }
                        }
                    });
                } catch (Exception ex) {
                    if (tx != null) {
                        tx.rollback();
                        Platform.runLater(() -> LIBRARIES.add(index, library));
                    }
                    LOGGER.log(Level.ERROR, "Exception while removing music library: ", ex);
                } finally {
                    session.close();
                }
            });
        }
    }

    private static final ObservableList<Song> CACHED_SONGS = FXCollections.observableArrayList();

    public static ObservableList<Song> getCachedSongs() {
        return CACHED_SONGS;
    }

    public static void addSong(Song song) {
        if (song != null) {
            CACHED_SONGS.add(song);
        }
    }

    public static void removeSong(int index) {
        if (index > -1) {
            CACHED_SONGS.remove(index);
        }
    }

    public static boolean removeSong(Song song) {
        if (song != null) {
            return CACHED_SONGS.remove(song);
        } else {
            return false;
        }
    }

    public static Song getSong(String songTitle, String songLocation) {
        if (songTitle != null && songLocation != null) {
            for (Song song : CACHED_SONGS) {
                if (song.getTitle().equals(songTitle)) {
                    if (song.getLocation().equals(songLocation)) {
                        return song;
                    }
                }
            }
        }
        return null;
    }

    public static ObservableList<Song> getSongs(String songTitle) {
        return CACHED_SONGS.filtered(song -> song.getTitle().startsWith(songTitle));
    }

    public static ObservableList<Song> getSongs(Album album) {
        return CACHED_SONGS.filtered(song -> song.getAlbum().equals(album.getAlbumName()))
                .filtered(song -> song.getAlbumArtist().equals(album.getAlbumArtist()))
                .sorted((song1, song2) -> Integer.compare(0, FileTime.fromMillis(song1.getDateCreated()).compareTo(FileTime.fromMillis(song2.getDateCreated()))));
    }

    public static ObservableList<Song> getSongs(Artist artist) {
        return CACHED_SONGS.filtered(song -> song.getArtist().equals(artist.getName()))
                .sorted((song1, song2) -> Integer.compare(0, FileTime.fromMillis(song1.getDateCreated()).compareTo(FileTime.fromMillis(song2.getDateCreated()))));
    }

    private static final ObservableList<Album> CACHED_ALBUMS = FXCollections.observableArrayList();

    public static ObservableList<Album> getCachedAlbums() {
        return CACHED_ALBUMS;
    }

    public static void addAlbum(Album album) {
        if (album != null) {
            CACHED_ALBUMS.add(album);
        }
    }

    public static void removeAlbum(int index) {
        if (index > -1) {
            CACHED_ALBUMS.remove(index);
        }
    }

    public static boolean removeAlbum(Album album) {
        if (album != null) {
            RecentlyPlayedTabController.getInstance().removeRecentlyPlayed(album);
            return CACHED_ALBUMS.remove(album);
        }
        return false;
    }

    public static Album getAlbum(String albumName, String albumArtist) {
        if (albumName != null && albumArtist != null) {
            for (Album album : CACHED_ALBUMS) {
                if (album.getAlbumName().equals(albumName)) {
                    if (album.getAlbumArtist().equals(albumArtist)) {
                        return album;
                    }
                }
            }
        }
        return null;
    }

    public static ObservableList<Album> getAlbums(String albumName) {
        return CACHED_ALBUMS.filtered(album -> album.getAlbumName().startsWith(albumName));
    }

    public static ObservableList<Album> getAlbums(Artist artist) {
        return CACHED_ALBUMS.filtered(album -> album.getArtist().equals(artist.getName()));
    }

    public static Album getAlbum(RecentlyPlays recentlyPlays) {
        if (recentlyPlays != null) {
            for (Album album : CACHED_ALBUMS) {
                if (album.getAlbumName().equals(recentlyPlays.getName())) {
                    if (album.getArtist().equals(recentlyPlays.getCategory().replaceAll("^Album by ", ""))) {
                        return album;
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
        if (artist != null) {
            CACHED_ARTISTS.add(artist);
        }
    }

    public static void removeArtist(int index) {
        if (index > -1) {
            CACHED_ARTISTS.remove(index);
        }
    }

    public static boolean removeArtist(Artist artist) {
        if (artist != null) {
            RecentlyPlayedTabController.getInstance().removeRecentlyPlayed(artist);
            return CACHED_ARTISTS.remove(artist);
        }
        return false;
    }

    public static Artist getArtist(String artistName) {
        for (Artist artist : CACHED_ARTISTS) {
            if (artist.getName().equalsIgnoreCase(artistName)) {
                return artist;
            }
        }
        return null;
    }

    public static ObservableList<Artist> getArtists(String artistName) {
        return CACHED_ARTISTS.filtered(artist -> artist.getName().startsWith(artistName));
    }

    public static Artist getArtist(RecentlyPlays recentlyPlays) {
        if (recentlyPlays != null) {
            for (Artist artist : CACHED_ARTISTS) {
                if (artist.getName().equals(recentlyPlays.getName())) {
                    return artist;
                }
            }
        }
        return null;
    }

    private static final ObservableList<String> CACHED_GENRES = FXCollections.observableArrayList();

    public static ObservableList<String> getCachedGenres() {
        return CACHED_GENRES;
    }

    public static void addGenreDescription(String description) {
        if (description != null) {
            if (!description.isEmpty()) {
                if (!CACHED_GENRES.contains(description)) {
                    CACHED_GENRES.add(description);
                }
            }
        }
    }

    private static final ObservableList<Playlist> CACHED_PLAYLISTS = FXCollections.observableArrayList();

    public static List<Playlist> getCachedPlaylist() {
        return CACHED_PLAYLISTS;
    }

    public static void setCachedPlaylists(List<Playlist> playlists) {
        CACHED_PLAYLISTS.setAll(playlists);
    }

    public static Playlist getPlaylist(String playlistName) {
        for (Playlist playlist : CACHED_PLAYLISTS) {
            if (playlist.getPlaylistName().equals(playlistName)) {
                return playlist;
            }
        }
        return null;
    }

    public static void addPlaylist(Playlist playlist) {
        if (playlist != null) {
            if (!CACHED_PLAYLISTS.contains(playlist)) {
                databaseServicePool.submit(() -> {
                    Session session = AudioPlayer.getInstance().getSession();
                    Transaction tx = null;
                    try {
                        tx = session.beginTransaction();
                        session.save(playlist);
                        for (PlaylistSong playlistSong : playlist.getSongList()) {
                            playlistSong.setPlaylist(playlist);
                            session.save(playlistSong);
                        }
                        tx.commit();
                        Platform.runLater(() -> CACHED_PLAYLISTS.add(playlist));
                    } catch (Exception ex) {
                        if (tx != null) {
                            tx.rollback();
                        }
                        LOGGER.log(Level.ERROR, "Exception while adding playlist: ", ex);
                    } finally {
                        session.close();
                    }
                });
            }
        }
    }

    public static void updatePlaylist(Playlist playlist) {
        if (playlist != null) {
            if (CACHED_PLAYLISTS.contains(playlist)) {
                databaseServicePool.submit(() -> {
                    Session session = AudioPlayer.getInstance().getSession();
                    Transaction tx = null;
                    try {
                        tx = session.beginTransaction();
                        session.update(playlist);
                        tx.commit();
                    } catch (Exception ex) {
                        if (tx != null) {
                            tx.rollback();
                        }
                        LOGGER.log(Level.ERROR, "Exception while updating playlist: ", ex);
                    } finally {
                        session.close();
                    }
                });
            }
        }
    }

    public static void updatePlaylist(Playlist playlist, PlaylistSong playlistSong, boolean save) {
        if (playlist != null && playlistSong != null) {
            if (CACHED_PLAYLISTS.contains(playlist)) {
                databaseServicePool.submit(() -> {
                    Session session = AudioPlayer.getInstance().getSession();
                    Transaction tx = null;
                    try {
                        tx = session.beginTransaction();
                        if (save) {
                            if (!playlist.getSongList().contains(playlistSong)) {
                                playlistSong.setPlaylist(playlist);
                                session.save(playlistSong);
                            }
                        } else {
                            if (playlist.getSongList().contains(playlistSong)) {
                                session.delete(playlistSong);
                            }
                        }
                        tx.commit();

                        Platform.runLater(() -> {
                            if (save) {
                                if (!playlist.getSongList().contains(playlistSong)) {
                                    playlist.getSongList().add(playlistSong);
                                    playlist.setTotalSongs("" + playlist.getSongList().size());
                                }
                            } else {
                                if (playlist.getSongList().contains(playlistSong)) {
                                    playlist.getSongList().remove(playlistSong);
                                    playlist.setTotalSongs("" + playlist.getSongList().size());
                                }
                            }
                            updatePlaylist(playlist);
                        });
                    } catch (Exception ex) {
                        if (tx != null) {
                            tx.rollback();
                        }
                        LOGGER.log(Level.ERROR, "Exception while adding/removing song in playlist: ", ex);
                    } finally {
                        session.close();
                    }
                });
            }
        }
    }

    public static void updatePlaylist(Playlist playlist, List<PlaylistSong> playlistSongs) {
        if (playlist != null && playlistSongs != null) {
            if (CACHED_PLAYLISTS.contains(playlist)) {
                databaseServicePool.submit(() -> {
                    Session session = AudioPlayer.getInstance().getSession();
                    Transaction tx = null;
                    try {
                        tx = session.beginTransaction();
                        for (PlaylistSong playlistSong : playlistSongs) {
                            if (!playlist.getSongList().contains(playlistSong)) {
                                playlistSong.setPlaylist(playlist);
                                session.save(playlistSong);
                            }
                        }
                        tx.commit();

                        Platform.runLater(() -> {
                            for (PlaylistSong playlistSong : playlistSongs) {
                                if (!playlist.getSongList().contains(playlistSong)) {
                                    playlist.getSongList().add(playlistSong);
                                }
                            }
                            playlist.setTotalSongs("" + playlist.getSongList().size());
                            updatePlaylist(playlist);
                        });
                    } catch (Exception ex) {
                        if (tx != null) {
                            tx.rollback();
                        }
                        LOGGER.log(Level.ERROR, "Exception while adding songs to playlist: ", ex);
                    } finally {
                        session.close();
                    }
                });
            }
        }
    }

    public static void updateRemoveAllSongFromPlaylist(Playlist playlist) {
        if (playlist != null) {
            if (CACHED_PLAYLISTS.contains(playlist)) {
                databaseServicePool.submit(() -> {
                    Session session = AudioPlayer.getInstance().getSession();
                    Transaction tx = null;
                    try {
                        tx = session.beginTransaction();
                        int updateSize = session.createNativeQuery("DELETE from PLAYLISTSONG where PLAYLIST_ID = ?").setParameter(1, playlist.getId()).executeUpdate();
                        tx.commit();

                        Platform.runLater(() -> {
                            if (updateSize == playlist.getSongList().size()) {
                                playlist.getSongList().clear();
                                playlist.setTotalSongs("" + 0);
                            }
                            updatePlaylist(playlist);
                        });
                    } catch (Exception ex) {
                        if (tx != null) {
                            tx.rollback();
                        }
                        LOGGER.log(Level.ERROR, "Exception while removing all Songs from playlist: ", ex);
                    } finally {
                        session.close();
                    }
                });
            }
        }
    }

    public static void removePlaylist(Playlist playlist) {
        if (playlist != null) {
            if (CACHED_PLAYLISTS.contains(playlist)) {
                databaseServicePool.submit(() -> {
                    Session session = AudioPlayer.getInstance().getSession();
                    Transaction tx = null;
                    try {
                        tx = session.beginTransaction();
                        int updateSize = session.createNativeQuery("DELETE from PLAYLISTSONG where PLAYLIST_ID = ?").setParameter(1, playlist.getId()).executeUpdate();
                        if (updateSize == playlist.getSongList().size()) {
                            session.delete(playlist);
                        } else {
                            throw new Exception();
                        }
                        tx.commit();
                        Platform.runLater(() -> CACHED_PLAYLISTS.remove(playlist));
                    } catch (Exception ex) {
                        if (tx != null) {
                            tx.rollback();
                        }
                        LOGGER.log(Level.ERROR, "Exception while removing playlist: ", ex);
                    } finally {
                        session.close();
                    }
                });
            }
        }
    }

    private static final ObservableList<Object> CACHED_RECENTLY_PLAYED = FXCollections.observableArrayList();

    public static ObservableList<Object> getCachedRecentlyPlayed() {
        return CACHED_RECENTLY_PLAYED;
    }

    public static void setCachedRecentlyPlayed(List<Object> recentlyPlayedList) {
        CACHED_RECENTLY_PLAYED.setAll(recentlyPlayedList);
    }

    public static void setCachedRecentlyPlayed(Map<Integer, OnlineSong> recentlyPlayedList) {
        for (Integer index : recentlyPlayedList.keySet()) {
            if (index >= CACHED_RECENTLY_PLAYED.size()) {
                CACHED_RECENTLY_PLAYED.add(recentlyPlayedList.get(index));
            } else {
                CACHED_RECENTLY_PLAYED.add(index, recentlyPlayedList.get(index));
            }
        }
    }

    public static void addRecentlyPlayed(Object object) {
        if (object != null) {
            assert object instanceof Album || object instanceof Artist || object instanceof OnlineSong || object instanceof Playlist;
            if (!CACHED_RECENTLY_PLAYED.contains(object)) {
                databaseServicePool.submit(() -> {
                    Session session = AudioPlayer.getInstance().getSession();
                    Transaction tx = null;
                    try {
                        tx = session.beginTransaction();
                        if (object instanceof Album || object instanceof Artist || object instanceof Playlist) {
                            session.save(new RecentlyPlays(object));
                        } else {
                            session.save((OnlineSong) object);
                        }
                        tx.commit();
                        Platform.runLater(() -> CACHED_RECENTLY_PLAYED.add(object));
                    } catch (Exception ex) {
                        if (tx != null) {
                            tx.rollback();
                        }
                        LOGGER.log(Level.ERROR, "Exception while adding recently plays: ", ex);
                    } finally {
                        session.close();
                    }
                });
            }
        }
    }

    public static boolean removeRecentlyPlayed(Object recentlyPlayed) {
        if (recentlyPlayed != null) {
            if (CACHED_RECENTLY_PLAYED.contains(recentlyPlayed)) {
                databaseServicePool.submit(() -> {
                    Session session = AudioPlayer.getInstance().getSession();
                    Transaction tx = null;
                    try {
                        tx = session.beginTransaction();
                        if (recentlyPlayed instanceof Album) {
                            session.createNativeQuery("DELETE FROM RecentlyPlays WHERE NAME = ? AND CATEGORY = ?")
                                    .setParameter(1, ((Album) recentlyPlayed).getAlbumName())
                                    .setParameter(2, "Album by ".concat(((Album) recentlyPlayed).getArtist()))
                                    .executeUpdate();
                        } else if (recentlyPlayed instanceof Artist) {
                            session.createNativeQuery("DELETE FROM RecentlyPlays WHERE NAME = ? AND CATEGORY = ?")
                                    .setParameter(1, ((Artist) recentlyPlayed).getName())
                                    .setParameter(2, "Artist")
                                    .executeUpdate();
                        } else if (recentlyPlayed instanceof Playlist) {
                            session.createNativeQuery("DELETE FROM RecentlyPlays WHERE NAME = ? AND CATEGORY = ?")
                                    .setParameter(1, ((Playlist) recentlyPlayed).getPlaylistName())
                                    .setParameter(2, "Playlist")
                                    .executeUpdate();
                        } else if (recentlyPlayed instanceof OnlineSong) {
                            session.delete((OnlineSong) recentlyPlayed);
                        }
                        tx.commit();

                        Platform.runLater(() -> CACHED_RECENTLY_PLAYED.remove(recentlyPlayed));
                    } catch (Exception ex) {
                        if (tx != null) {
                            tx.rollback();
                        }
                        LOGGER.log(Level.ERROR, "Exception while removing recently plays: ", ex);
                    } finally {
                        session.close();
                    }
                });
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static void getReady() {
        CACHED_SONGS.clear();
        CACHED_ALBUMS.clear();
        CACHED_ARTISTS.clear();
        CACHED_GENRES.clear();
        CACHED_RECENTLY_PLAYED.clear();
    }

}
