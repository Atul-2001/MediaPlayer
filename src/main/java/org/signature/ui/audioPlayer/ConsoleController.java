package org.signature.ui.audioPlayer;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXSpinner;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.signature.App;
import org.signature.dataModel.audioPlayer.*;
import org.signature.ui.audioPlayer.dialogs.EqualizerController;
import org.signature.ui.audioPlayer.dialogs.PlayingListDialogController;
import org.signature.ui.audioPlayer.model.AlbumPane;
import org.signature.ui.audioPlayer.model.PlaylistPane;
import org.signature.ui.audioPlayer.tabs.RecentlyPlayedTabController;
import org.signature.util.Alerts;
import org.signature.util.Utils;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsoleController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(ConsoleController.class);

    private final String DEFAULT_TIME = "00:00";
    private final String PAUSE_ICON = "M256 8C119 8 8 119 8 256s111 248 248 248 248-111 248-248S393 8 256 8zm0 448c-110.5 0-200-89.5-200-200S145.5 56 256 56s200 89.5 200 200-89.5 200-200 200zm96-280v160c0 8.8-7.2 16-16 16h-48c-8.8 0-16-7.2-16-16V176c0-8.8 7.2-16 16-16h48c8.8 0 16 7.2 16 16zm-112 0v160c0 8.8-7.2 16-16 16h-48c-8.8 0-16-7.2-16-16V176c0-8.8 7.2-16 16-16h48c8.8 0 16 7.2 16 16z";
    private final String PLAY_ICON = "M371.7 238l-176-107c-15.8-8.8-35.7 2.5-35.7 21v208c0 18.4 19.8 29.8 35.7 21l176-101c16.4-9.1 16.4-32.8 0-42zM504 256C504 119 393 8 256 8S8 119 8 256s111 248 248 248 248-111 248-248zm-448 0c0-110.5 89.5-200 200-200s200 89.5 200 200-89.5 200-200 200S56 366.5 56 256z";
    private final String MUTE_ICON = "M16.5 12c0-1.77-1.02-3.29-2.5-4.03v2.21l2.45 2.45c.03-.2.05-.41.05-.63zm2.5 0c0 .94-.2 1.82-.54 2.64l1.51 1.51C20.63 14.91 21 13.5 21 12c0-4.28-2.99-7.86-7-8.77v2.06c2.89.86 5 3.54 5 6.71zM4.27 3L3 4.27 7.73 9H3v6h4l5 5v-6.73l4.25 4.25c-.67.52-1.42.93-2.25 1.18v2.06c1.38-.31 2.63-.95 3.69-1.81L19.73 21 21 19.73l-9-9L4.27 3zM12 4L9.91 6.09 12 8.18V4z";
    private final String UNMUTE_ICON = "M3 9v6h4l5 5V4L7 9H3zm13.5 3c0-1.77-1.02-3.29-2.5-4.03v8.05c1.48-.73 2.5-2.25 2.5-4.02zM14 3.23v2.06c2.89.86 5 3.54 5 6.71s-2.11 5.85-5 6.71v2.06c4.01-.91 7-4.49 7-8.77s-2.99-7.86-7-8.77z";
    private final String SHUFFLE_ICON = "M10.59 9.17L5.41 4 4 5.41l5.17 5.17 1.42-1.41zM14.5 4l2.04 2.04L4 18.59 5.41 20 17.96 7.46 20 9.5V4h-5.5zm.33 9.41l-1.41 1.41 3.13 3.13L14.5 20H20v-5.5l-2.04 2.04-3.13-3.13z";
    private final String REPEAT_ICON = "M7 7h10v3l4-4-4-4v3H5v6h2V7zm10 10H7v-3l-4 4 4 4v-3h12v-6h-2v4z";
    private final String REPEAT_ONCE_ICON = "M7 7h10v3l4-4-4-4v3H5v6h2V7zm10 10H7v-3l-4 4 4 4v-3h12v-6h-2v4zm-4-2V9h-1l-2 1v1h1.5v4H13z";

    private static ConsoleController instance = null;

    @FXML
    private StackPane console;
    @FXML
    private ImageView albumArt;
    @FXML
    private JFXSpinner playerLoader;
    @FXML
    private Label songName, songArtist;
    @FXML
    private HBox playerControls, playerMiscControls;
    @FXML
    private Label currentDuration, totalDuration;
    @FXML
    private JFXSlider songSeek, volumeSlider;
    @FXML
    private JFXButton playPause, volume;
    @FXML
    private ToggleButton shuffle, repeat;
    @FXML
    private SVGPath playPauseIcon, volumeIcon;
    @FXML
    private JFXButton skipPrevious, fastRewind, fastForward, skipNext;
    @FXML
    private JFXButton miniPlayer, nowPlaying, clearPlayer, moreOptions, moreOptions2;
    @FXML
    private CustomMenuItem contextMenuShuffle, contextMenuRepeat, contextMenuMiniPlayer;

    private boolean swapped = false, miniPlayerRemoved = false, miscControlsRemovedStage1 = false, miscControlsRemovedStage2 = false;
    private boolean isMute = false, isRepeatOnce = false, isRepeat = false;
    private final BooleanProperty isPlaying = new SimpleBooleanProperty(false);
    private final BooleanProperty isActive = new SimpleBooleanProperty(false);
    private boolean goingForward = true, isSongListLoaded = false, isOnlineSongListLoaded = false;

    private int songID = -1, playValue = -1;

    private final ObservableList<Object> playingList = FXCollections.observableArrayList();
    private ListIterator<Object> playingListIterator = playingList.listIterator();

    private Object previousSong;
    private Media media;
    private MediaPlayer mediaPlayer;
    private static final ExecutorService mediaPlayerService;

    private String youtubeExecName = "youtube-dl.exe";
    private boolean isUnix = false;

    static {
        mediaPlayerService = Executors.newFixedThreadPool(2, new ThreadFactory() {
            final AtomicInteger counter = new AtomicInteger();

            @Override
            public Thread newThread(Runnable runnable) {

                Thread thread = new Thread(runnable, "Media Player Service Pool--Thread-" + counter.incrementAndGet());
                thread.setPriority(Thread.NORM_PRIORITY);

                return thread;
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("linux") || osName.contains("mac"))
            isUnix = true;

        if (isUnix) youtubeExecName = "./youtube-dl";

        console.setOpacity(0.0);
        console.setTranslateY(100.0);
        console.widthProperty().addListener((observable, oldValue, newValue) -> {
            double screenWidth = App.getScreenWidth();
            if (newValue.doubleValue() <= screenWidth * 0.5 && !swapped) {
                Utils.flipStackPane(console);
                swapped = true;
            } else if (newValue.doubleValue() > screenWidth * 0.5 && swapped) {
                Utils.flipStackPane(console);
                swapped = false;
            }

            if (newValue.doubleValue() <= screenWidth * 0.55 && !miniPlayerRemoved) {
                playerControls.getChildren().remove(miniPlayer);
                contextMenuMiniPlayer.setVisible(true);
                miniPlayerRemoved = true;
            } else if (newValue.doubleValue() > screenWidth * 0.55 && miniPlayerRemoved) {
                playerControls.getChildren().add(3, miniPlayer);
                contextMenuMiniPlayer.setVisible(false);
                miniPlayerRemoved = false;
            }

            if (newValue.doubleValue() <= screenWidth * 0.60 && !miscControlsRemovedStage2) {
                playerMiscControls.getChildren().removeAll(shuffle, repeat);
                contextMenuShuffle.setVisible(true);
                contextMenuRepeat.setVisible(true);
                miscControlsRemovedStage2 = true;
            } else if (newValue.doubleValue() > screenWidth * 0.60 && miscControlsRemovedStage2) {
                playerMiscControls.getChildren().add(0, shuffle);
                playerMiscControls.getChildren().add(repeat);
                contextMenuShuffle.setVisible(false);
                contextMenuRepeat.setVisible(false);
                miscControlsRemovedStage2 = false;
            }

            if (newValue.doubleValue() <= screenWidth * 0.65 && !miscControlsRemovedStage1) {
                playerMiscControls.getChildren().removeAll(fastRewind, fastForward);
                miscControlsRemovedStage1 = true;
            } else if (newValue.doubleValue() > screenWidth * 0.65 && miscControlsRemovedStage1) {
                playerMiscControls.getChildren().add(2, fastRewind);
                playerMiscControls.getChildren().add(4, fastForward);
                miscControlsRemovedStage1 = false;
            }
        });

        songSeek.setValueFactory(slider -> Bindings.createStringBinding(() -> Utils.getDuration((long) slider.getValue() * 1000), slider.valueProperty()));


        moreOptions.setOnAction(event -> {
            Bounds screenBounds = moreOptions.localToScreen(moreOptions.getBoundsInLocal());
            moreOptions.getContextMenu().show(moreOptions, screenBounds.getMinX(), screenBounds.getMinY());
        });
        moreOptions2.setOnAction(event -> {
            Bounds screenBounds = moreOptions2.localToScreen(moreOptions2.getBoundsInLocal());
            moreOptions.getContextMenu().show(moreOptions2, screenBounds.getMinX(), screenBounds.getMinY());
        });

        registerMediaKeys();
    }

    public static ConsoleController getInstance() {
        return instance;
    }

    public StackPane getRoot() {
        return console;
    }

    public static ExecutorService getMediaPlayerService() {
        return mediaPlayerService;
    }

    public ObjectProperty<Image> albumArtProperty() {
        return albumArt.imageProperty();
    }

    public StringProperty songTitleProperty() {
        return songName.textProperty();
    }

    public StringProperty songArtistProperty() {
        return songArtist.textProperty();
    }

    public DoubleProperty currentTimeProperty() {
        return songSeek.valueProperty();
    }

    public double getSongLength() {
        return songSeek.getMax();
    }

    public BooleanProperty activeProperty() {
        return isActive;
    }

    public BooleanProperty playingProperty() {
        return isPlaying;
    }

    public JFXButton getSkipPrevious() {
        return skipPrevious;
    }

    public JFXButton getPlayPause() {
        return playPause;
    }

    public JFXButton getSkipNext() {
        return skipNext;
    }

    private void registerMediaKeys() {
//        https://stackoverflow.com/questions/30560212/how-to-remove-the-logging-data-from-jnativehook-library
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(java.util.logging.Level.OFF);
        logger.setUseParentHandlers(false);

        try {
                    /*Raw Key codes of media buttons
                    Linux :
                    65300 - Play/Pause
                    65303 - Next
                    65302 - Previous

                    Windows :
                    179 - Play/Pause
                    176 - Next
                    177 - Previous
                    178 - Stop
                     */
            GlobalScreen.registerNativeHook();

            GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
                @Override
                public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

                }

                @Override
                public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
                    int keyEventCode = nativeKeyEvent.getRawCode();

                    if (!isUnix && keyEventCode == 57380) {
                        handleClearPlayer(null);
                    }

                    if (isActive.get() && !isUnix && keyEventCode == 178) {
                        onEndOfMediaTrigger();
                    }

                    if (!isUnix && keyEventCode == 179 || isUnix && keyEventCode == 65300) {
                        handlePlayPauseSong(null);
                    } else if (!isUnix && keyEventCode == 177 || isUnix && keyEventCode == 65302) {
                        handlePlayPrevious(null);
                    } else if (!isUnix && keyEventCode == 176 || isUnix && keyEventCode == 65303) {
                        handlePlayNext(null);
                    }
                }

                @Override
                public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

                }
            });
        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, ex);
        }
    }

    @FXML
    private void handleShuffle(ActionEvent actionEvent) {
        if (shuffle.isSelected()) {
            shuffle.getTooltip().setText("Shuffle: on");
        } else {
            shuffle.getTooltip().setText("Shuffle: off");
        }

        if (!playingList.isEmpty() && previousSong != null) {
            playingList.remove(previousSong);
            Collections.shuffle(playingList);
            playingList.add(0, previousSong);

            playingListIterator = playingList.listIterator();
            PlayingListDialogController.getInstance().loadData(playingList);
            if (isActive.get()) {
                if (playingListIterator.hasNext()) {
                    playingListIterator.next();
                }
            }
            goingForward = true;
        }
    }

    public void handleShuffleAll() {
        playingList.clear();
        playingList.setAll(Inventory.getCachedSongs());
        Collections.shuffle(playingList);
        if (isActive.get()) {
            playingList.remove(previousSong);
            playingList.add(0, previousSong);
        }
        PlayingListDialogController.getInstance().loadData(playingList);
        playingListIterator = playingList.listIterator();
        goingForward = true;
        if (isPlaying.get()) {
            if (playingListIterator.hasNext()) {
                playingListIterator.next();
            }
        } else {
            handlePlayNext(null);
        }
    }

    public void handleShuffleRecentlyPlays() {
        playingList.clear();
        for (Object recentlyPlays : Inventory.getCachedRecentlyPlayed()) {
            if (recentlyPlays instanceof Album) {
                playingList.addAll(Inventory.getSongs((Album) recentlyPlays));
            } else if (recentlyPlays instanceof Artist) {
                playingList.addAll(Inventory.getSongs((Artist) recentlyPlays));
            } else if (recentlyPlays instanceof OnlineSong) {
                playingList.add((OnlineSong) recentlyPlays);
            }
        }

        Collections.shuffle(playingList);
        PlayingListDialogController.getInstance().loadData(playingList);
        playingListIterator = playingList.listIterator();
        goingForward = true;
        handlePlayNext(null);
    }

    @FXML
    private void handlePlayPrevious(ActionEvent actionEvent) {
        if (goingForward) {
            if (playingListIterator.hasPrevious()) {
                playingListIterator.previous();
            }
            goingForward = false;
        }

        if (playingListIterator.hasPrevious()) {
            playSong(playingListIterator.previous());
        }
    }

    @FXML
    private void handleFastRewind(ActionEvent actionEvent) {
        if (isPlaying.get()) {
            mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(5)));
        }
    }

    @FXML
    public void handlePlayPauseSong(ActionEvent actionEvent) {
        if (isActive.get()) {
            if (isPlaying.get()) {
                playPauseIcon.setContent(PLAY_ICON);
                playPause.getTooltip().setText("Play");
                mediaPlayer.pause();
                isPlaying.set(false);
            } else {
                playPauseIcon.setContent(PAUSE_ICON);
                playPause.getTooltip().setText("Pause");
                mediaPlayer.play();
                isPlaying.set(true);
            }
        }
    }

    @FXML
    private void handleFastForward(ActionEvent actionEvent) {
        if (isPlaying.get()) {
            mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(10)));
        }
    }

    @FXML
    private void handlePlayNext(ActionEvent actionEvent) {
        if (!goingForward) {
            if (playingListIterator.hasNext()) {
                playingListIterator.next();
            }
            goingForward = true;
        }

        if (playingListIterator.hasNext()) {
            playSong(playingListIterator.next());
        }
    }

    @FXML
    private void handleRepeat(ActionEvent actionEvent) {
        if (((SVGPath) repeat.getGraphic()).getContent().equals(REPEAT_ICON)) {
            if (repeat.isSelected()) {
                isRepeat = true;
                isRepeatOnce = false;
                repeat.getTooltip().setText("Repeat: on");
            } else {
                ((SVGPath) repeat.getGraphic()).setContent(REPEAT_ONCE_ICON);
                isRepeat = false;
                isRepeatOnce = true;
                repeat.setSelected(true);
                repeat.getTooltip().setText("Repeat: one");
            }
        } else if (((SVGPath) repeat.getGraphic()).getContent().equals(REPEAT_ONCE_ICON)) {
            ((SVGPath) repeat.getGraphic()).setContent(REPEAT_ICON);
            isRepeat = false;
            isRepeatOnce = false;
            repeat.getTooltip().setText("Repeat: off");
        }
    }

    @FXML
    private void handleMute(ActionEvent actionEvent) {
        if (isMute) {
            volumeIcon.setContent(UNMUTE_ICON);
            if (isActive.get()) mediaPlayer.setMute(false);
            volume.getTooltip().setText("Mute: off");
            isMute = false;
        } else {
            volumeIcon.setContent(MUTE_ICON);
            if (isActive.get()) mediaPlayer.setMute(true);
            volume.getTooltip().setText("Mute: on");
            isMute = true;
        }
    }

    @FXML
    private void handleShowMiniPlayer(ActionEvent actionEvent) {
        MiniPlayerViewController.getInstance().showStage();
    }

    @FXML
    private void handleShowNowPlaying(ActionEvent actionEvent) {
        PlayingListDialogController.getInstance().showDialog();
    }

    @FXML
    private void handleClearPlayer(ActionEvent actionEvent) {
        if (isActive.get()) {
            if (mediaPlayer != null) {
                if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING)) {
                    mediaPlayer.pause();
                    isPlaying.set(false);
                }
                mediaPlayer.dispose();
            } else {
                isPlaying.set(false);
            }
            isActive.set(false);
        } else {
            isPlaying.set(false);
        }

        albumArt.setImage(null);
        songName.setText("");
        songArtist.setText("");
        playPauseIcon.setContent(PLAY_ICON);
        fastRewind.setDisable(true);
        playPause.setDisable(true);
        fastForward.setDisable(true);
        currentDuration.setText(DEFAULT_TIME);
        totalDuration.setText(DEFAULT_TIME);
        songSeek.setValue(-1);
        songSeek.setDisable(true);

        if (actionEvent != null) {
            playerLoader.setVisible(false);
            skipPrevious.setDisable(true);
            skipNext.setDisable(true);
            nowPlaying.setDisable(true);
            clearPlayer.setDisable(true);
            PlayingListDialogController.getInstance().clearData();
        }

        if (previousSong != null) {
            if (previousSong instanceof Song) {
                ((Song) previousSong).setPlaying(false);
            } else if (previousSong instanceof PlaylistSong) {
                ((PlaylistSong) previousSong).setPlaying(false);
            } else if (previousSong instanceof OnlineSong) {
                ((OnlineSong) previousSong).setPlaying(false);
            }
        }

    }

    public void playlistLoadProgress(boolean start) {
        if (start) {
            this.playerLoader.setVisible(true);
            this.albumArt.setImage(PlaylistPane.getDefaultPlaylistArt());
            this.songName.setText("Loading playlist...");
            this.songArtist.setText("");
        }
    }

    public void load(List<Song> songs, Song song) {
        if (songs != null && song != null) {
            if (!isSongListLoaded) {
                playingList.clear();
                playingList.setAll(songs);
                PlayingListDialogController.getInstance().loadData(playingList);

                isSongListLoaded = true;
                goingForward = true;
            }
            int index = playingList.indexOf(song);
            if (index > -1) {
                playingListIterator = playingList.listIterator(index);
            } else {
                playingListIterator = playingList.listIterator();
            }
            handlePlayNext(null);
        }
    }

    public void addToNowPlaying(Song song) {
        if (song != null) {
            if (!playingList.contains(song)) {
                playingList.add(song);
            }

            startNowPlaying();
        }
    }

    public void load(Album album) {
        if (album != null) {
            playingList.clear();
            playingList.setAll(Inventory.getSongs(album));
            PlayingListDialogController.getInstance().loadData(playingList);

            playingListIterator = playingList.listIterator();
            handlePlayNext(null);
            isSongListLoaded = false;
            goingForward = true;
        }
    }

    public void load(Album album, Song song) {
        if (album != null && song != null) {
            load(Inventory.getSongs(album), song);
        }
    }

    public void addToNowPlaying(Album album) {
        if (album != null) {
            for (Song song : Inventory.getSongs(album)) {
                if (!playingList.contains(song)) {
                    playingList.add(song);
                }
            }

            startNowPlaying();
        }
    }

    public void load(Artist artist) {
        if (artist != null) {
            playingList.clear();
            playingList.addAll(Inventory.getSongs(artist));

            PlayingListDialogController.getInstance().loadData(playingList);
            playingListIterator = playingList.listIterator();
            handlePlayNext(null);
            isSongListLoaded = false;
            goingForward = true;
        }
    }

    public void load(Artist artist, Song song) {
        if (artist != null && song != null) {
            load(Inventory.getSongs(artist), song);
        }
    }

    public void addToNowPlaying(Artist artist) {
        if (artist != null) {
            for (Song song : Inventory.getSongs(artist)) {
                if (!playingList.contains(song)) {
                    playingList.add(song);
                }
            }

            startNowPlaying();
        }
    }

    public void load(Playlist playlist) {
        if (playlist != null) {
            playingList.clear();
            playingList.addAll(playlist.getSongList());
            PlayingListDialogController.getInstance().loadData(playingList);
            playingListIterator = playingList.listIterator();
            handlePlayNext(null);
            isSongListLoaded = false;
            goingForward = true;
        }
    }

    public void load(Playlist playlist, PlaylistSong playlistSong) {
        if (playlist != null) {
            this.playingList.clear();
            this.playingList.addAll(playlist.getSongList());

            PlayingListDialogController.getInstance().loadData(playingList);
            int index = playingList.indexOf(playlistSong);
            if (index > -1) {
                playingListIterator = playingList.listIterator(index);
            } else {
                playingListIterator = playingList.listIterator();
            }
            handlePlayNext(null);
            isSongListLoaded = false;
            goingForward = true;
        }
    }

    public void addToNowPlaying(Playlist playlist) {
        if (playlist != null) {
            playingList.addAll(playlist.getSongList());
            startNowPlaying();
        }
    }

    public void addToNowPlaying(PlaylistSong playlistSong) {
        if (playlistSong != null) {
            if (!playingList.contains(playlistSong)) {
                this.playingList.add(playlistSong);
            }
            startNowPlaying();
        }
    }


    public void setOnlineSongListLoaded(boolean value) {
        this.isOnlineSongListLoaded = value;
    }

    public void loadOnlineSong(List<OnlineSong> onlineSongs, OnlineSong onlineSong) {
        if (onlineSongs != null && onlineSong != null) {
            if (!isOnlineSongListLoaded) {
                playingList.clear();
                playingList.setAll(onlineSongs);
                PlayingListDialogController.getInstance().loadData(playingList);

                isSongListLoaded = false;
                isOnlineSongListLoaded = true;
                goingForward = true;
            }
            int index = playingList.indexOf(onlineSong);
            if (index > -1) {
                playingListIterator = playingList.listIterator(index);
            } else {
                playingListIterator = playingList.listIterator();
            }
            handlePlayNext(null);
        }
    }

    public void addToNowPlaying(OnlineSong onlineSong) {
        if (onlineSong != null) {
            if (!playingList.contains(onlineSong)) {
                playingList.add(onlineSong);
            }

            startNowPlaying();
        }
    }

    public void addToNowPlaying(List<OnlineSong> onlineSongs) {
        if (onlineSongs != null) {
            playingList.addAll(onlineSongs);
            startNowPlaying();
        }
    }

    private String getYoutubeURL(String rawURL) throws IOException {
        String youtubeDLQuery = youtubeExecName + " -f 18 -g " + rawURL;
        Process p = Runtime.getRuntime().exec(youtubeDLQuery);

        InputStream i = p.getInputStream();
        InputStream e = p.getErrorStream();

        StringBuilder result = new StringBuilder();
        while (true) {
            int c = i.read();
            if (c == -1) break;
            result.append((char) c);
        }

        if (result.length() == 0) {
            //get errors
            StringBuilder errResult = new StringBuilder();
            while (true) {
                int c = e.read();
                if (c == -1) break;
                errResult.append((char) c);
            }


            e.close();
            Alerts.showErrorAlert("Uh OH!", "Unable to play, probably because Age Restricted/Live Video. If not, check connection and try again!\n\n" + errResult);
            return null;
        }

        i.close();
        e.close();

        return result.substring(0, result.length() - 1);
    }

    private void startNowPlaying() {
        if (clearPlayer.isDisable()) {
            PlayingListDialogController.getInstance().loadData(playingList);
            playingListIterator = playingList.listIterator();
            handlePlayNext(null);
            isSongListLoaded = false;
            goingForward = true;
        } else {
            PlayingListDialogController.getInstance().loadData(playingList);
            playingListIterator = playingList.listIterator(playingListIterator.nextIndex());
        }
    }

    private void playSong(Object song) {
        assert song instanceof Song || song instanceof PlaylistSong || song instanceof OnlineSong;

        playerLoader.setVisible(true);

        mediaPlayerService.submit(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    StringBuilder mediaURL = new StringBuilder();

                    if (song instanceof Song) {
                        Album album = Inventory.getAlbum(((Song) song).getAlbum(), ((Song) song).getAlbumArtist());
                        if (album == null) {
                            playerLoader.setVisible(false);
                            playingListIterator.remove();
                            PlayingListDialogController.getInstance().loadData(playingList);
                            handlePlayNext(null);
                            return null;
                        }
                        Platform.runLater(() -> {
                            handleClearPlayer(null);

                            if (album.getAlbumImage() == null || album.getAlbumImage().length == 0) {
                                albumArt.setImage(new Image(AlbumPane.class.getResourceAsStream("album_white.png")));
                            } else {
                                try {
                                    albumArt.setImage(SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(album.getAlbumImage())), null));
                                } catch (NullPointerException | IOException ex) {
                                    albumArt.setImage(new Image(AlbumPane.class.getResourceAsStream("album_white.png")));
                                }
                            }

                            album.albumImageMimeTypeProperty().addListener((observable, oldValue, newValue) -> {
                                byte[] albumArt1 = album.getAlbumImage();
                                if (albumArt1 == null || albumArt1.length == 0) {
                                    albumArt.setImage(AlbumPane.getDefaultAlbumArt());
                                } else {
                                    try {
                                        albumArt.setImage(SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(albumArt1)), null));
                                    } catch (NullPointerException | IOException ex) {
                                        albumArt.setImage(AlbumPane.getDefaultAlbumArt());
                                    }
                                }
                            });

                            songName.setText(((Song) song).getTitle());
                            songArtist.setText(((Song) song).getArtist());
                            totalDuration.setText(Utils.getDuration(((Song) song).getLength()));
                        });

                        mediaURL.append(new File(((Song) song).getLocation()).toURI());
                    } else if (song instanceof PlaylistSong) {
                        Platform.runLater(() -> {
                            handleClearPlayer(null);

                            PlaylistSong playlistSong = (PlaylistSong) song;
                            if (playlistSong.getThumbnail() == null || playlistSong.getThumbnail().length == 0) {
                                albumArt.setImage(PlaylistPane.getDefaultPlaylistArt());
                            } else {
                                try {
                                    albumArt.setImage(SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(playlistSong.getThumbnail())), null));
                                } catch (IOException ignored) {
                                    albumArt.setImage(PlaylistPane.getDefaultPlaylistArt());
                                }
                            }

                            songName.setText(playlistSong.getTitle());
                            songArtist.setText(playlistSong.getArtist());
                            totalDuration.setText(Utils.getDuration((long) playlistSong.getLength()));
                        });

                        if (((PlaylistSong) song).getLocation().startsWith("https://")) {
                            mediaURL.append(getYoutubeURL(((PlaylistSong) song).getLocation()));
                            if (mediaURL.length() == 0) {
                                handlePlayNext(null);
                                return null;
                            }
                        } else {
                            mediaURL.append(new File(((PlaylistSong) song).getLocation()).toURI());
                        }
                    } else {
                        Platform.runLater(() -> {
                            handleClearPlayer(null);

                            OnlineSong onlineSong = (OnlineSong) song;
                            if (onlineSong.getThumbnailURL() == null || onlineSong.getThumbnailURL().isEmpty()) {
                                albumArt.setImage(new Image(AlbumPane.class.getResourceAsStream("album_white.png")));
                            } else {
                                try {
                                    albumArt.setImage(new Image(onlineSong.getThumbnailURL()));
                                } catch (NullPointerException ex) {
                                    albumArt.setImage(new Image(AlbumPane.class.getResourceAsStream("album_white.png")));
                                }
                            }

                            songName.setText(onlineSong.getTitle());
                            songArtist.setText(onlineSong.getChannelName());
                        });

                        if (((OnlineSong) song).isLocalSourceAvailable()) {
                            mediaURL.append(new File(((OnlineSong) song).getURL()).toURI());
                        } else {
                            mediaURL.append(getYoutubeURL(((OnlineSong) song).getURL()));
                            if (mediaURL.length() == 0) {
                                handlePlayNext(null);
                                return null;
                            }
                        }

                        Platform.runLater(() -> RecentlyPlayedTabController.getInstance().addRecentlyPlayed((OnlineSong) song));
                    }

                    mediaPlayerService.submit(new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            media = new Media(mediaURL.toString());
                            mediaPlayer = EqualizerController.getInstance().setAudioEqualizer(new MediaPlayer(media));

                            mediaPlayer.setVolume(volumeSlider.getValue() / 100);
                            if (isMute) {
                                mediaPlayer.setMute(true);
                            }

                            mediaPlayer.setOnError(() -> {
                                stop();
                                LOGGER.log(Level.TRACE, "", mediaPlayer.getError());
                            });

                            mediaPlayer.setOnReady(() -> {
                                Platform.runLater(() -> {
                                    totalDuration.setText(Utils.getDuration((long) media.getDuration().toMillis()));
                                    skipPrevious.setDisable(false);
                                    fastRewind.setDisable(false);
                                    playPause.setDisable(false);
                                    fastForward.setDisable(false);
                                    skipNext.setDisable(false);
                                    songSeek.setDisable(false);
                                    songSeek.setMax(media.getDuration().toSeconds());
                                    nowPlaying.setDisable(false);
                                    clearPlayer.setDisable(false);

                                    songSeek.setOnMousePressed(event -> mediaPlayer.seek(Duration.seconds(songSeek.getValue())));
                                    songSeek.setOnMouseDragged(event -> mediaPlayer.seek(Duration.seconds(songSeek.getValue())));

                                    volumeSlider.setOnMousePressed(event -> mediaPlayer.setVolume(volumeSlider.getValue() / 100));
                                    volumeSlider.setOnMouseDragged(event -> mediaPlayer.setVolume(volumeSlider.getValue() / 100));

                                    if (song instanceof Song) {
                                        ((Song) song).setPlaying(true);

                                        if (((Song) song).getId() != songID) {
                                            playValue = -1;
                                            songID = ((Song) song).getId();
                                        }
                                    } else if (song instanceof PlaylistSong) {
                                        ((PlaylistSong) song).setPlaying(true);

                                        if (((PlaylistSong) song).getId() != songID) {
                                            playValue = -1;
                                            songID = ((PlaylistSong) song).getId();
                                        }
                                    } else {
                                        ((OnlineSong) song).setPlaying(true);

                                        if (((OnlineSong) song).getSNO() != songID) {
                                            playValue = -1;
                                            songID = ((OnlineSong) song).getSNO();
                                        }
                                    }
                                    isActive.set(true);
                                    playValue += 1;
                                });

                                mediaPlayer.play();
                                Platform.runLater(() -> {
                                    playPauseIcon.setContent(PAUSE_ICON);
                                    playerLoader.setVisible(false);
                                });
                                isPlaying.set(true);
                            });

                            mediaPlayer.setOnEndOfMedia(() -> Platform.runLater(() -> {
                                handlePlayPauseSong(null);
                                onEndOfMediaTrigger();
                            }));

                            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(() -> {
                                currentDuration.setText(Utils.getDuration((long) newValue.toMillis()));
                                songSeek.setValue(newValue.toSeconds());
                            }));
                            return null;
                        }
                    });

                    if (previousSong != null) {
                        if (previousSong instanceof Song) {
                            ((Song) previousSong).setPlaying(false);
                        } else if (previousSong instanceof PlaylistSong) {
                            ((PlaylistSong) previousSong).setPlaying(false);
                        } else if (previousSong instanceof OnlineSong) {
                            ((OnlineSong) previousSong).setPlaying(false);
                        }
                    }

                    previousSong = song;
                } catch (Exception ex) {
                    LOGGER.log(Level.ERROR, "", ex);
                    Platform.runLater(() -> playerLoader.setVisible(false));
                }
                return null;
            }
        });
    }

    private void stop() {
        if (isPlaying.get()) {
            isPlaying.set(false);
            try {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            } catch (Exception e) {
                System.out.println("disposing ...");
            }
        }
        isActive.set(false);
    }

    private void onEndOfMediaTrigger() {
        if (isRepeat) {
            repeat();
        } else if (isRepeatOnce) {
            if (playValue <= 0) {
                repeat();
                playValue += 1;
            } else {
                handlePlayNext(null);
            }
        } else {
            handlePlayNext(null);
        }
    }

    private void repeat() {
        if (isActive.get()) {
            mediaPlayer.stop();
            handlePlayPauseSong(null);
        }
    }
}
