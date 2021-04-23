package org.signature.ui.audioPlayer.dialogs;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXRadioButton;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.dataModel.audioPlayer.*;
import org.signature.ui.MainWindowController;
import org.signature.ui.audioPlayer.BaseController;
import org.signature.ui.audioPlayer.ConsoleController;
import org.signature.ui.audioPlayer.Inventory;
import org.signature.ui.audioPlayer.model.AlbumPane;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddToListDialogController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(AddToListDialogController.class);

    private static AddToListDialogController instance = null;

    private final String ADD_TO_PLAYLIST_ICON = "M10 20h4V4h-4v16zm-6 0h4v-8H4v8zM16 9v11h4V9h-4z";
    private final String NEW_PLAYLIST_ICON = "M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z";
    private final String PLAYLIST_ICON = "M15 6H3v2h12V6zm0 4H3v2h12v-2zM3 16h8v-2H3v2zM17 6v8.18c-.31-.11-.65-.18-1-.18-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3V8h3V6h-5z";

    private final int MIN_DIALOG_HEIGHT = 188;

    @FXML
    private BorderPane root;
    @FXML
    private ScrollPane contentPane;
    @FXML
    private VBox playlists;
    @FXML
    private ToggleGroup selectedPlaylist;
    @FXML
    private JFXRadioButton btn_newPlaylist;

    private JFXDialog dialog;
    private JFXRadioButton selectedButton;
    private Object item;
    private List<OnlineSong> onlineSongsList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        playlists.getChildren().addListener((ListChangeListener<Node>) change -> {
            change.next();
            if (change.wasAdded()) {
                if (playlists.getHeight() < 334) {
                    root.setPrefHeight(root.getPrefHeight() + 32);
                }
            } else if (change.wasRemoved()) {
                for (Node node : change.getRemoved()) {
                    if (playlists.getHeight() > 188) {
                        root.setPrefHeight(root.getPrefHeight() - 32);
                    }
                }
            }
        });

        this.dialog = new JFXDialog(MainWindowController.getInstance().getRoot(), root, JFXDialog.DialogTransition.CENTER);
//        this.dialog.setMinHeight(188.0); // min 188 or 220
        this.dialog.setPrefHeight(450.0); // 220.0
//        this.dialog.setMaxHeight(450.0); // 450.0

//        LOGGER.log(Level.INFO, "Add to list dialog loaded!");
    }

    private JFXRadioButton getPlaylistButton(String playlistName) {
        JFXRadioButton radioButton = new JFXRadioButton(playlistName);
        radioButton.setSelectedColor(Color.rgb(60, 186, 84));
        radioButton.setFont(Font.font("Roboto", 16.0));
        radioButton.setToggleGroup(selectedPlaylist);
        radioButton.setGraphicTextGap(8.0);
        SVGPath icon = new SVGPath();
        icon.setContent(PLAYLIST_ICON);
        radioButton.setGraphic(icon);
        radioButton.setOnAction(this::handleOnButtonSelected);
        VBox.setVgrow(radioButton, Priority.NEVER);
        return radioButton;
    }

    public static AddToListDialogController getInstance() {
        return instance;
    }

    public BorderPane getRoot() {
        return root;
    }

    @FXML
    private void handleOnButtonSelected(ActionEvent actionEvent) {
        this.selectedButton = ((JFXRadioButton) actionEvent.getSource());
    }

    @FXML
    private void handleConfirmToAdd(ActionEvent actionEvent) {
        if (selectedButton.getText().equals("Now playing")) {
            if (item != null) {
                if (item instanceof Song) {
                    ConsoleController.getInstance().addToNowPlaying((Song) item);
                } else if (item instanceof Album) {
                    ConsoleController.getInstance().addToNowPlaying((Album) item);
                } else if (item instanceof Artist) {
                    ConsoleController.getInstance().addToNowPlaying((Artist) item);
                } else if (item instanceof OnlineSong) {
                    ConsoleController.getInstance().addToNowPlaying((OnlineSong) item);
                } else if (item instanceof Playlist) {
                    ConsoleController.getInstance().addToNowPlaying((Playlist) item);
                } else if (item instanceof PlaylistSong) {
                    ConsoleController.getInstance().addToNowPlaying((PlaylistSong) item);
                }
            } else if (onlineSongsList.size() > 0){
                ConsoleController.getInstance().addToNowPlaying(onlineSongsList);
            }
        } else if (selectedButton.getText().equals("New playlist")) {
            if (onlineSongsList.size() > 0) {
                AddPlaylistDialogController.getInstance().load(null, (OnlineSong) onlineSongsList);
            } else {
                AddPlaylistDialogController.getInstance().load(item);
            }
        } else {
            if (item != null) {
                if (item instanceof Song) {
                    Playlist playlist = Inventory.getPlaylist(selectedButton.getText());
                    if (playlist != null) {
                        Song song = (Song) item;
                        Album album = Inventory.getAlbum(song.getAlbum(), song.getAlbumArtist());

                        byte[] thumbnail = new byte[0];
                        if (album.getAlbumImage() == null || album.getAlbumImage().length == 0) {
                            try {
                                Image img = new Image(AlbumPane.class.getResourceAsStream("album_white.png"));
                                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(img, null);
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                                thumbnail = byteArrayOutputStream.toByteArray();
                            } catch (IOException ignored) {}
                        } else {
                            thumbnail = album.getAlbumImage();
                        }

                        playlist.addSong(new PlaylistSong(thumbnail, song.getTitle(),
                                song.getArtist(), song.getAlbum(), song.getYearOfRelease(),
                                song.getGenre(), song.getLength(), song.getLocation()));
                    }
                } else if (item instanceof Album) {
                    Playlist playlist = Inventory.getPlaylist(selectedButton.getText());
                    if (playlist != null) {
                        Album album = (Album) item;

                        byte[] thumbnail = new byte[0];
                        if (album.getAlbumImage() == null || album.getAlbumImage().length == 0) {
                            try {
                                Image img = new Image(AlbumPane.class.getResourceAsStream("album_white.png"));
                                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(img, null);
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                                thumbnail = byteArrayOutputStream.toByteArray();
                            } catch (IOException ignored) {}
                        } else {
                            thumbnail = album.getAlbumImage();
                        }

                        List<PlaylistSong> playlistSongs = new ArrayList<>();
                        for (Song song : Inventory.getSongs(album)) {
                            playlistSongs.add(new PlaylistSong(thumbnail, song.getTitle(),
                                    song.getArtist(), song.getAlbum(), song.getYearOfRelease(),
                                    song.getGenre(), song.getLength(), song.getLocation()));
                        }
                        playlist.addSongs(playlistSongs);
                    }
                } else if (item instanceof Artist) {
                    Playlist playlist = Inventory.getPlaylist(selectedButton.getText());
                    if (playlist != null) {
                        Artist artist = (Artist) item;
                        byte[] thumbnail = new byte[0];
                        List<PlaylistSong> playlistSongs = new ArrayList<>();

                        for (Album album : Inventory.getAlbums(artist)) {
                            if (album.getAlbumImage() == null || album.getAlbumImage().length == 0) {
                                try {
                                    Image img = new Image(AlbumPane.class.getResourceAsStream("album_white.png"));
                                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(img, null);
                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                                    thumbnail = byteArrayOutputStream.toByteArray();
                                } catch (IOException ignored) {}
                            } else {
                                thumbnail = album.getAlbumImage();
                            }

                            for (Song song : Inventory.getSongs(album)) {
                                playlistSongs.add(new PlaylistSong(thumbnail, song.getTitle(),
                                        song.getArtist(), song.getAlbum(), song.getYearOfRelease(),
                                        song.getGenre(), song.getLength(), song.getLocation()));
                            }
                        }
                        playlist.addSongs(playlistSongs);
                    }
                } else if (item instanceof OnlineSong) {
                    Playlist playlist = Inventory.getPlaylist(selectedButton.getText());
                    if (playlist != null) {
                        OnlineSong onlineSong = (OnlineSong) item;

                        byte[] thumbnail = new byte[0];
                        try {
                            Image img = new Image(onlineSong.getThumbnailURL());
                            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(img, null);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                            thumbnail = byteArrayOutputStream.toByteArray();
                        } catch (Exception ignored) {
                            try {
                                Image img = new Image(AlbumPane.class.getResourceAsStream("album_white.png"));
                                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(img, null);
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                                thumbnail = byteArrayOutputStream.toByteArray();
                            } catch (Exception ignored2) {}
                        }

                        playlist.addSong(new PlaylistSong(thumbnail, onlineSong.getTitle(),
                                onlineSong.getChannelName(), "Youtube", null,
                                "youtube.com", onlineSong.getLength(), onlineSong.getURL()));
                    }
                } else if (item instanceof Playlist) {
                    Playlist playlist = Inventory.getPlaylist(selectedButton.getText());
                    if (playlist != null) {
                        playlist.addSongs(((Playlist) item).getSongList());
                    }
                } else if (item instanceof PlaylistSong) {
                    Playlist playlist = Inventory.getPlaylist(selectedButton.getText());
                    if (playlist != null) {
                        playlist.addSong((PlaylistSong) item);
                    }
                }
            } else if (onlineSongsList.size() > 0) {
                Playlist playlist = Inventory.getPlaylist(selectedButton.getText());
                if (playlist != null) {
                    List<PlaylistSong> playlistSongs = new ArrayList<>();
                    byte[] thumbnail = new byte[0];

                    for (OnlineSong onlineSong : onlineSongsList) {
                        try {
                            Image img = new Image(onlineSong.getThumbnailURL());
                            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(img, null);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                            thumbnail = byteArrayOutputStream.toByteArray();
                        } catch (Exception ignored) {
                            try {
                                Image img = new Image(AlbumPane.class.getResourceAsStream("album_white.png"));
                                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(img, null);
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                                thumbnail = byteArrayOutputStream.toByteArray();
                            } catch (Exception ignored2) {}
                        }

                        playlistSongs.add(new PlaylistSong(thumbnail, onlineSong.getTitle(),
                                onlineSong.getChannelName(), "Youtube", null,
                                "youtube.com", onlineSong.getLength(), onlineSong.getURL()));
                    }
                    playlist.addSongs(playlistSongs);
                }
            }
        }
        this.dialog.close();
    }

    @FXML
    private void handleCloseDialog(ActionEvent actionEvent) {
        this.dialog.close();
    }

    public void load(Object item, OnlineSong... onlineSongs) {
        this.item = item;
        this.onlineSongsList = FXCollections.observableArrayList(onlineSongs);
        loadDialog();
    }

    private void loadDialog() {
        playlists.getChildren().remove(2, playlists.getChildren().size());

        for (Playlist playlist : Inventory.getCachedPlaylist()) {
            playlists.getChildren().add(getPlaylistButton(playlist.getPlaylistName()));
        }
        playlists.getChildren().add(btn_newPlaylist);

        this.dialog.show();
    }

}
