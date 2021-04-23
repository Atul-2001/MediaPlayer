package org.signature.ui.audioPlayer;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class AudioPlayer {
    private static final Logger LOGGER = LogManager.getLogger(AudioPlayer.class);

    private Configuration configuration = null;
    private ServiceRegistry registry = null;
    private SessionFactory factory = null;

    ExecutorService mediaPool = Executors.newCachedThreadPool(new ThreadFactory() {
        final AtomicInteger counter = new AtomicInteger();

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "Media Loader Thread Pool--Thread-" + counter.incrementAndGet());
        }
    });

    private static AudioPlayer instance = null;

    private AudioPlayer() {
    }

    public static AudioPlayer getInstance() {
        if (instance == null) {
            instance = new AudioPlayer();
        }
        return instance;
    }

    public void startDatabaseSession() {
        try {
            configuration = new Configuration();
            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
            configuration.setProperty("hibernate.connection.url", Inventory.getDBUrl());
            configuration.setProperty("hibernate.hbm2ddl.auto", "update"); // DB Schema will be updated instead of new schema update / create
//            configuration.setProperty("show_sql", "true");
            configuration = configuration.addAnnotatedClass(MusicLibrary.class)
                    .addAnnotatedClass(RecentlyPlays.class)
                    .addAnnotatedClass(OnlineSong.class)
                    .addAnnotatedClass(Playlist.class)
                    .addAnnotatedClass(PlaylistSong.class);

            registry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            factory = configuration.buildSessionFactory(registry);
        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "Failed to start Audio Player session! ", ex);
            System.out.println("It looks like media player is already running!");
            Platform.exit();
        }
    }

    public Session getSession() {
        return factory.openSession();
    }

    public void stopDatabaseSession() {
        try {
            if (factory != null && registry != null) {
                factory.close();
                registry.close();
                configuration = null;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "Error while closing Audio Payer session! ", ex);
        }
    }

    public ExecutorService getMediaPool() {
        return mediaPool;
    }

    public void loadLibraries() {
        Transaction tx = null;
        try (Session session = getSession()) {
            List<MusicLibrary> libraries = session.createQuery("from MusicLibrary").list();
            if (libraries.size() == 0) {
                File library = new File(System.getProperty("user.home"), "Music");
                MusicLibrary musicLibrary = new MusicLibrary(library.getName(), library.getAbsolutePath());

                tx = session.beginTransaction();
                session.save(musicLibrary);
                tx.commit();

                libraries.add(musicLibrary);
            }

            Inventory.setLibraries(libraries);
        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "Error while loading music libraries ", ex);
            if (tx != null) {
                tx.rollback();
            }
            Platform.exit();
        }
    }

    public void musicLoader(Album unknownAlbum, Artist unknownArtist) {
        for (MusicLibrary library : Inventory.getLibraries()) {
            Path musicLibrary = Path.of(library.getFolderLocation());

            if (library.isLibraryLoaded()) {
                continue;
            }

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
                                addSong(file, attrs, library.getId(), unknownAlbum, unknownArtist);
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
                } finally {
                    library.setLibraryLoaded(true);
                }
            }
        }
    }

    public void addSong(Path file, BasicFileAttributes attrs, int libraryID, Album unknownAlbum, Artist unknownArtist) {
        try {
            Mp3File mp3File = new Mp3File(file);
            Song song = new Song();
            Album album = new Album();
            Artist artist = new Artist();

            song.setMusicLibraryId(libraryID);

            if (mp3File.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3File.getId3v2Tag();

                if (id3v2Tag.getTitle() == null) {
                    song.setTitle(file.toFile().getName().substring(0, file.toFile().getName().lastIndexOf(".")));
                } else if (id3v2Tag.getTitle().isEmpty()) {
                    song.setTitle(file.toFile().getName().substring(0, file.toFile().getName().lastIndexOf(".")));
                } else {
                    song.setTitle(id3v2Tag.getTitle());
                }

                song.setTrack(id3v2Tag.getTrack());
                if (id3v2Tag.getAlbum() == null) {
                    song.setAlbum(unknownAlbum.getAlbumName());
                    song.setAlbumArtist(unknownAlbum.getAlbumArtist());
                    if (id3v2Tag.getAlbumImage() != null) {
                        unknownAlbum.setAlbumImage(id3v2Tag.getAlbumImage());
                        unknownAlbum.setAlbumImageMimeType(id3v2Tag.getAlbumImageMimeType());
                    }
                } else {
                    song.setAlbum(id3v2Tag.getAlbum());
                    song.setAlbumArtist(id3v2Tag.getAlbumArtist());
                }

                if (id3v2Tag.getArtist() == null) {
                    song.setArtist(unknownArtist.getName());
                } else {
                    song.setArtist(id3v2Tag.getArtist());
                }
                song.setYearOfRelease(id3v2Tag.getYear());

                song.setGenre(id3v2Tag.getGenreDescription());
                Inventory.addGenreDescription(id3v2Tag.getGenreDescription());

                song.setLocation(file.toAbsolutePath().toString());
                song.setLength(mp3File.getLengthInMilliseconds());
                song.setDateCreated(attrs.creationTime().toMillis());
                Inventory.addSong(song);

                mediaPool.submit(() -> {
                    Media media = new Media(file.toUri().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    mediaPlayer.setOnReady(() -> {
                        song.setLength(media.getDuration().toMillis());
                        mediaPlayer.dispose();
                    });
                });

                if (id3v2Tag.getAlbum() != null) {
                    Album fAlbum = Inventory.getAlbum(id3v2Tag.getAlbum(), id3v2Tag.getAlbumArtist());
                    if (fAlbum == null || !fAlbum.getAlbumArtist().equals(id3v2Tag.getAlbumArtist())) {
                        album = new Album();
                        album.setAlbumName(id3v2Tag.getAlbum());
                        album.setAlbumArtist(id3v2Tag.getAlbumArtist());
                        if (id3v2Tag.getArtist() != null) {
                            Artist tmpArtist = Inventory.getArtist(id3v2Tag.getArtist());
                            if (tmpArtist != null) {
                                album.setArtist(tmpArtist.getName());
                            } else {
                                album.setArtist(id3v2Tag.getArtist());
                            }
                        }
                        album.setAlbumImage(id3v2Tag.getAlbumImage());
                        album.setAlbumImageMimeType(id3v2Tag.getAlbumImageMimeType());
                        album.setGenre(id3v2Tag.getGenreDescription());
                        album.setReleaseYear(id3v2Tag.getYear());
                        album.setDateCreated(attrs.creationTime().toMillis());
                        Inventory.addAlbum(album);
                    } else if (!fAlbum.equals(unknownAlbum) || !fAlbum.getAlbumName().equals("Unknown Album")) {
                        if (id3v2Tag.getAlbumArtist() != null) {
                            if (!id3v2Tag.getAlbumArtist().isEmpty()) {
                                if (fAlbum.getAlbumArtist().equals(fAlbum.getArtist())) {
                                    fAlbum.setAlbumArtist(id3v2Tag.getAlbumArtist());
                                }
                            }
                        }

                        if (id3v2Tag.getAlbumImage() != null) {
                            if (fAlbum.getAlbumImage() == null) {
                                fAlbum.setAlbumImage(id3v2Tag.getAlbumImage());
                                fAlbum.setAlbumImageMimeType(id3v2Tag.getAlbumImageMimeType());
                            } else if (fAlbum.getAlbumImage().length == 0) {
                                fAlbum.setAlbumImage(id3v2Tag.getAlbumImage());
                                fAlbum.setAlbumImageMimeType(id3v2Tag.getAlbumImageMimeType());
                            }
                        }

                        fAlbum.setDateCreated(attrs.creationTime().toMillis());
                    }

                    if (fAlbum != null) {
                        if (fAlbum.equals(unknownAlbum) || fAlbum.getAlbumName().equals("Unknown Album")) {
                            fAlbum.setDateCreated(attrs.creationTime().toMillis());
                        }
                    }
                } else {
                    unknownAlbum.setDateCreated(attrs.creationTime().toMillis());
                }

                if (id3v2Tag.getArtist() != null) {
                    if (Inventory.getArtist(id3v2Tag.getArtist()) == null) {
                        artist = new Artist(id3v2Tag.getArtist());
                        Inventory.addArtist(artist);
                    }
                }

            } else {
                if (mp3File.hasId3v1Tag()) {
                    ID3v1 id3v1Tag = mp3File.getId3v1Tag();

                    if (id3v1Tag.getTitle() == null) {
                        song.setTitle(file.toFile().getName().substring(0, file.toFile().getName().lastIndexOf(".")));
                    } else if (id3v1Tag.getTitle().isEmpty()) {
                        song.setTitle(file.toFile().getName().substring(0, file.toFile().getName().lastIndexOf(".")));
                    } else {
                        song.setTitle(id3v1Tag.getTitle());
                    }

                    song.setTrack(id3v1Tag.getTrack());
                    if (id3v1Tag.getAlbum() == null) {
                        song.setAlbum(unknownAlbum.getAlbumName());
                    } else {
                        song.setAlbum(id3v1Tag.getAlbum());
                    }

                    if (id3v1Tag.getArtist() == null) {
                        song.setArtist(unknownArtist.getName());
                    } else {
                        song.setArtist(id3v1Tag.getArtist());
                    }
                    song.setYearOfRelease(id3v1Tag.getYear());

                    song.setGenre(id3v1Tag.getGenreDescription());
                    Inventory.addGenreDescription(id3v1Tag.getGenreDescription());

                    song.setLocation(file.toAbsolutePath().toString());
                    song.setLength(mp3File.getLengthInMilliseconds());
                    song.setDateCreated(attrs.creationTime().toMillis());
                    Inventory.addSong(song);

                    mediaPool.submit(() -> {
                        Media media = new Media(file.toUri().toString());
                        MediaPlayer mediaPlayer = new MediaPlayer(media);
                        mediaPlayer.setOnReady(() -> {
                            song.setLength(media.getDuration().toMillis());
                            mediaPlayer.dispose();
                        });
                    });

                    if (id3v1Tag.getAlbum() != null) {
                        Album fAlbum = Inventory.getAlbum(id3v1Tag.getAlbum(), id3v1Tag.getArtist());
                        if (fAlbum == null) {
                            album = new Album();
                            album.setAlbumName(id3v1Tag.getAlbum());
                            if (id3v1Tag.getArtist() != null) {
                                Artist tmpArtist = Inventory.getArtist(id3v1Tag.getArtist());
                                if (tmpArtist != null) {
                                    album.setArtist(tmpArtist.getName());
                                } else {
                                    album.setArtist(id3v1Tag.getArtist());
                                }
                            }
                            album.setGenre(id3v1Tag.getGenreDescription());
                            album.setReleaseYear(id3v1Tag.getYear());
                            album.setDateCreated(attrs.creationTime().toMillis());
                            Inventory.addAlbum(album);
                        } else if (!fAlbum.equals(unknownAlbum) || !fAlbum.getAlbumName().equals("Unknown Album")) {
                            fAlbum.setDateCreated(attrs.creationTime().toMillis());
                        }

                        if (fAlbum != null) {
                            if (fAlbum.equals(unknownAlbum) || fAlbum.getAlbumName().equals("Unknown Album")) {
                                fAlbum.setDateCreated(attrs.creationTime().toMillis());
                            }
                        }
                    }

                    if (id3v1Tag.getArtist() != null) {
                        if (Inventory.getArtist(id3v1Tag.getArtist()) == null) {
                            artist = new Artist(id3v1Tag.getArtist());
                            Inventory.addArtist(artist);
                        }
                    }
                }
            }

        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "", ex);
        }
    }

    public void loadMusic() {
        if (!Inventory.getLibraries().isEmpty()) {
            Inventory.getReady();

            Artist unknownArtist = new Artist("Unknown Artist");
            Inventory.addArtist(unknownArtist);

            Album unknownAlbum = new Album("Unknown Album", unknownArtist.getName(), String.valueOf(LocalDateTime.now().getYear()), FileTime.from(Instant.now()).toMillis());
            Inventory.addAlbum(unknownAlbum);

            Inventory.addGenreDescription("All genres");

            musicLoader(unknownAlbum, unknownArtist);

            Inventory.addGenreDescription("Unknown Genre");
            Inventory.getCachedGenres().addListener((ListChangeListener<String>) change -> {
                int index = Inventory.getCachedGenres().indexOf("Unknown Genre");
                if (index != -1) {
                    if (index != (Inventory.getCachedGenres().size() - 1)) {
                        Inventory.getCachedGenres().remove(index);
                        Inventory.addGenreDescription("Unknown Genre");
                    }
                }
            });
        }
    }

    public void loadPlaylists() {
        try (Session session = getSession()) {
            Inventory.setCachedPlaylists(session.createQuery("from Playlist ").list());
        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "Error while loading playlists ", ex);
            Platform.exit();
        }
    }

    public void loadRecentlyPlayed() {
        try (Session session = getSession()) {
            List<Object> recentlyPlayedList = new ArrayList<>();
            for (Object recentlyPlays : session.createQuery("from RecentlyPlays ").list()) {
                if (((RecentlyPlays) recentlyPlays).getCategory().startsWith("Album by ")) {
                    if (!((RecentlyPlays) recentlyPlays).getName().isEmpty()) {
                        Album album = Inventory.getAlbum(((RecentlyPlays) recentlyPlays));
                        if (album != null) {
                            recentlyPlayedList.add(album);
                        }
                    }
                } else if (((RecentlyPlays) recentlyPlays).getCategory().startsWith("Artist")) {
                    if (!((RecentlyPlays) recentlyPlays).getName().isEmpty()) {
                        Artist artist = Inventory.getArtist(((RecentlyPlays) recentlyPlays));
                        if (artist != null) {
                            recentlyPlayedList.add(artist);
                        }
                    }
                } else if (((RecentlyPlays) recentlyPlays).getCategory().startsWith("Playlist")) {
                    if (!((RecentlyPlays) recentlyPlays).getName().isEmpty()) {
                        Playlist playlist = Inventory.getPlaylist(((RecentlyPlays) recentlyPlays).getName());
                        if (playlist != null) {
                            recentlyPlayedList.add(playlist);
                        }
                    }
                }
            }
            Inventory.setCachedRecentlyPlayed(recentlyPlayedList);

            Map<Integer, OnlineSong> recentlyPlayedList2 = new HashMap<>();
            for (Object onlineSong : session.createQuery("from OnlineSong ").list()) {
                recentlyPlayedList2.put(((OnlineSong) onlineSong).getSNO(), ((OnlineSong) onlineSong));
            }
            Inventory.setCachedRecentlyPlayed(recentlyPlayedList2);
        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, "Error while loading recently plays ", ex);
            Platform.exit();
        }
    }

}
