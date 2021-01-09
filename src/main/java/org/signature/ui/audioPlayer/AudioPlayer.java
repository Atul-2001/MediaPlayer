package org.signature.ui.audioPlayer;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;
import org.signature.dataModel.audioPlayer.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudioPlayer {

    private static final Logger LOGGER = LogManager.getLogger(AudioPlayer.class);

    private Configuration configuration = null;
    private ServiceRegistry registry = null;
    private SessionFactory factory = null;
    private Session session = null;

    private static AudioPlayer instance = null;

    private AudioPlayer() {
    }

    public static AudioPlayer getInstance() {
        if (instance == null) {
            instance = new AudioPlayer();
        }
        return instance;
    }

    public void startSession() {
        try {
            configuration = new Configuration();
            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
            configuration.setProperty("hibernate.connection.url", Inventory.getDBUrl());
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");
            configuration = configuration.addAnnotatedClass(MusicLibrary.class)
                    .addAnnotatedClass(Artist.class)
                    .addAnnotatedClass(Album.class)
                    .addAnnotatedClass(Song.class)
                    .addAnnotatedClass(RecentlyPlays.class);

            registry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            factory = configuration.buildSessionFactory(registry);
            session = factory.openSession();
        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "Failed to start Audio Player session!, ", ex);
            Platform.exit();
        }
    }

    public Session getSession() {
        return session;
    }

    public void stopSession() {
        try {
            if (session != null && factory != null && registry != null) {
                session.close();
                factory.close();
                registry.close();
                configuration = null;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "Error while closing Audio Payer session! ", ex);
        }
    }

    public void loadLibraries() {
        if (session != null) {
            Query query = session.createQuery("from MusicLibrary");
            List<MusicLibrary> libraries = query.list();

            if (libraries.size() == 0) {
                File library = new File(System.getProperty("user.home"), "Music");
                MusicLibrary musicLibrary = new MusicLibrary(library.getName(), library.getAbsolutePath());
                libraries.add(musicLibrary);
                session.save(musicLibrary);
            }

            Inventory.setLibraries(libraries);
        } else {
            LOGGER.log(Level.ERROR, "Audio Player session not available!");
            Platform.exit();
        }
    }

    public void loadMusic() {
        if (!Inventory.getLibraries().isEmpty()) {

            Inventory.getReady();
            ExecutorService mediaPool = Executors.newCachedThreadPool();

            Artist unknownArtist = new Artist("Unknown Artist");
            Inventory.getCachedArtists().add(unknownArtist);

            Album unknownAlbum = new Album("Unknown Album", unknownArtist.getId(), String.valueOf(LocalDateTime.now().getYear()), String.valueOf(FileTime.from(Instant.now()).toMillis()));
            Inventory.getCachedAlbums().add(unknownAlbum);

            for (MusicLibrary library : Inventory.getLibraries()) {
                Path musicLibrary = Path.of(library.getFolderLocation());

                if (Files.isDirectory(musicLibrary)) {
                    try {
                        Files.walkFileTree(musicLibrary, new FileVisitor<Path>() {
                            @Override
                            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                if (Files.isRegularFile(file) && file.toFile().getName().endsWith(".mp3")) {
                                    try {
                                        Mp3File mp3File = new Mp3File(file);
                                        Song song = new Song();
                                        Album album;
                                        Artist artist;

                                        ID3v2 id3v2Tag = mp3File.getId3v2Tag();
                                        if (id3v2Tag != null) {

                                            if (id3v2Tag.getTitle() == null) {
                                                song.setTitle(file.toFile().getName().substring(0, file.toFile().getName().lastIndexOf(".")));
                                            } else {
                                                song.setTitle(id3v2Tag.getTitle());
                                            }

                                            song.setTrack(id3v2Tag.getTrack());

                                            song.setYearOfRelease(id3v2Tag.getYear());

                                            song.setGenre(id3v2Tag.getGenre());
                                            Inventory.addGenreDescription(id3v2Tag.getGenre(), id3v2Tag.getGenreDescription());

                                            if (id3v2Tag.getAlbum() == null) {
                                                song.setAlbum(unknownAlbum.getId());
                                            } else {
                                                int albumID = Inventory.getAlbumID(id3v2Tag.getAlbum());
                                                if (albumID != -1) {
                                                    song.setAlbum(albumID);
                                                } else {
                                                    album = new Album();
                                                    album.setAlbumName(id3v2Tag.getAlbum());
                                                    album.setAlbumArtist(id3v2Tag.getAlbumArtist());
                                                    album.setAlbumImage(id3v2Tag.getAlbumImage());
                                                    album.setAlbumImageMimeType(id3v2Tag.getAlbumImageMimeType());
                                                    album.setGenre(id3v2Tag.getGenre());
                                                    album.setReleaseYear(id3v2Tag.getYear());
                                                    album.setCreationTime(String.valueOf(attrs.creationTime().toMillis()));

                                                    if (id3v2Tag.getArtist() == null) {
                                                        album.setArtist(unknownArtist.getId());
                                                    } else {
                                                        int artistID = Inventory.getArtistID(id3v2Tag.getArtist());
                                                        if (artistID != -1) {
                                                            album.setArtist(artistID);
                                                        } else {
                                                            artist = new Artist(id3v2Tag.getArtist());
                                                            Inventory.addArtist(artist);

                                                            album.setArtist(artist.getId());
                                                        }
                                                    }
                                                    Inventory.addAlbum(album);
                                                    song.setAlbum(album.getId());
                                                }
                                            }

                                            song.setLocation(file.toAbsolutePath().toString());
                                            song.setCreationTime(String.valueOf(attrs.creationTime().toMillis()));

                                            mediaPool.submit(() -> {
                                                Media media = new Media(file.toUri().toString());
                                                MediaPlayer mediaPlayer = new MediaPlayer(media);
                                                mediaPlayer.setOnReady(() -> {
                                                    song.setLength(media.getDuration().toMillis());
                                                    Inventory.addSong(song);
                                                    mediaPlayer.dispose();
                                                });
                                            });
                                        } else {
                                            ID3v1 id3v1Tag = mp3File.getId3v1Tag();
                                            if (id3v1Tag != null) {

                                                if (id3v1Tag.getTitle() == null) {
                                                    song.setTitle(file.toFile().getName().substring(0, file.toFile().getName().lastIndexOf(".")));
                                                } else {
                                                    song.setTitle(id3v1Tag.getTitle());
                                                }

                                                song.setTrack(id3v1Tag.getTrack());

                                                song.setYearOfRelease(id3v1Tag.getYear());

                                                song.setGenre(id3v1Tag.getGenre());
                                                Inventory.addGenreDescription(id3v1Tag.getGenre(), id3v1Tag.getGenreDescription());

                                                if (id3v1Tag.getAlbum() == null) {
                                                    song.setAlbum(unknownAlbum.getId());
                                                } else {
                                                    int albumID = Inventory.getAlbumID(id3v1Tag.getAlbum());
                                                    if (albumID != -1) {
                                                        song.setAlbum(albumID);
                                                    } else {
                                                        album = new Album();
                                                        album.setAlbumName(id3v1Tag.getAlbum());
                                                        album.setGenre(id3v1Tag.getGenre());
                                                        album.setCreationTime(String.valueOf(attrs.creationTime().toMillis()));

                                                        if (id3v1Tag.getArtist() == null) {
                                                            album.setArtist(unknownArtist.getId());
                                                        } else {
                                                            int artistID = Inventory.getArtistID(id3v1Tag.getArtist());
                                                            if (artistID != -1) {
                                                                album.setArtist(artistID);
                                                            } else {
                                                                artist = new Artist(id3v1Tag.getArtist());
                                                                Inventory.addArtist(artist);

                                                                album.setArtist(artist.getId());
                                                            }
                                                        }
                                                        Inventory.addAlbum(album);
                                                        song.setAlbum(album.getId());
                                                    }
                                                }

                                                song.setLocation(file.toAbsolutePath().toString());
                                                song.setCreationTime(String.valueOf(attrs.creationTime().toMillis()));

                                                mediaPool.submit(() -> {
                                                    Media media = new Media(file.toUri().toString());
                                                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                                                    mediaPlayer.setOnReady(() -> {
                                                        song.setLength(media.getDuration().toMillis());
                                                        Inventory.addSong(song);
                                                        mediaPlayer.dispose();
                                                    });
                                                });
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    } catch (NullPointerException | IOException ex) {
                        LOGGER.log(Level.WARN, ex);
                    }
                }
            }
        }
    }

    public void loadRecentlyPlayed() {
        List<RecentlyPlays> recentlyPlaysList = session.createQuery("from RecentlyPlays ").list();
        for (RecentlyPlays recentlyPlays : recentlyPlaysList) {
            if (recentlyPlays.getAlbumName() != null) {
                if (!recentlyPlays.getAlbumName().isEmpty()) {
                    Album album = Inventory.getAlbum(recentlyPlays);
                    if (album != null) {
                        Inventory.addRecentlyPlayed(album);
                    }
                    continue;
                }
            }

            if (recentlyPlays.getArtist() != null) {
                if (!recentlyPlays.getArtist().isEmpty()) {
                    Artist artist = Inventory.getArtist(recentlyPlays);
                    if (artist != null) {
                        Inventory.addRecentlyPlayed(artist);
                    }
                }
            }
        }
    }

    public void loadFavourites() {
        List<Artist> favouriteArtists = session.createQuery("from Artist ").list();
        for (Artist artist : favouriteArtists) {
            Artist cachedArtist = Inventory.checkArtist(artist);
            if (cachedArtist != null) {
                cachedArtist.setFavourite(true);
                Inventory.addFavouriteArtist(cachedArtist);
            }
        }

        List<Album> favouriteAlbums = session.createQuery("from Album ").list();
        for (Album album : favouriteAlbums) {
            Album cachedAlbum = Inventory.checkAlbum(album);
            if (cachedAlbum != null) {
                cachedAlbum.setFavourite(true);
                Inventory.addFavouriteAlbum(cachedAlbum);
            }
        }

        List<Song> favouriteSongs = session.createQuery("from Song ").list();
        for (Song song : favouriteSongs) {
            Song cachedSong = Inventory.checkSong(song);
            if (cachedSong != null) {
                cachedSong.setFavourite(true);
                Inventory.addFavouriteSong(cachedSong);
            }
        }
    }
}
