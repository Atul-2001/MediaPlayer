package org.signature.ui.audioPlayer.dialogs;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.dataModel.audioPlayer.*;
import org.signature.ui.MainWindowController;
import org.signature.ui.audioPlayer.BaseController;
import org.signature.ui.audioPlayer.Inventory;
import org.signature.ui.audioPlayer.model.AlbumPane;
import org.signature.ui.audioPlayer.tabs.PlaylistTabController;
import org.signature.ui.audioPlayer.tabs.PlaylistViewTabController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddPlaylistDialogController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(AddPlaylistDialogController.class);

    private static AddPlaylistDialogController instance = null;

    @FXML
    private BorderPane root;
    @FXML
    private TextField playlistName;
    @FXML
    private JFXButton createPlaylistButton;
    @FXML
    private JFXButton cancelButton;

    private JFXDialog dialog;
    private Object item;
    private List<OnlineSong> onlineSongList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        createPlaylistButton.disableProperty().bind(playlistName.textProperty().isEmpty());

        this.dialog = new JFXDialog(MainWindowController.getInstance().getRoot(), root, JFXDialog.DialogTransition.CENTER);
        this.dialog.setMinSize(400, 400);

        cancelButton.setOnAction(event -> this.dialog.close());

//        LOGGER.log(Level.INFO, "Add Playlist Dialog Loaded !!");
    }

    public static AddPlaylistDialogController getInstance() {
        return instance;
    }

    public BorderPane getRoot() {
        return root;
    }

    @FXML
    private void handleAddPlaylist(ActionEvent actionEvent) {
        try {
            if (Inventory.getPlaylist(playlistName.getText()) == null) {
                Playlist playlist = new Playlist(playlistName.getText());

                if (item != null) {
                    if (item instanceof Song) {
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
                            } catch (IOException ignored) {
                            }
                        } else {
                            thumbnail = album.getAlbumImage();
                        }

                        playlist = new Playlist(playlistName.getText(), new PlaylistSong(thumbnail, song.getTitle(),
                                song.getArtist(), song.getAlbum(), song.getYearOfRelease(),
                                song.getGenre(), song.getLength(), song.getLocation()));
                    } else if (item instanceof Album) {
                        Album album = (Album) item;

                        byte[] thumbnail = new byte[0];
                        if (album.getAlbumImage() == null || album.getAlbumImage().length == 0) {
                            try {
                                Image img = new Image(AlbumPane.class.getResourceAsStream("album_white.png"));
                                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(img, null);
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                                thumbnail = byteArrayOutputStream.toByteArray();
                            } catch (IOException ignored) {
                            }
                        } else {
                            thumbnail = album.getAlbumImage();
                        }

                        List<PlaylistSong> playlistSongs = new ArrayList<>();
                        for (Song song : Inventory.getSongs(album)) {
                            playlistSongs.add(new PlaylistSong(thumbnail, song.getTitle(),
                                    song.getArtist(), song.getAlbum(), song.getYearOfRelease(),
                                    song.getGenre(), song.getLength(), song.getLocation()));
                        }
                        playlist = new Playlist(playlistName.getText(), playlistSongs);
                    } else if (item instanceof Artist) {
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
                                } catch (IOException ignored) {
                                }
                            } else {
                                thumbnail = album.getAlbumImage();
                            }

                            for (Song song : Inventory.getSongs(album)) {
                                playlistSongs.add(new PlaylistSong(thumbnail, song.getTitle(),
                                        song.getArtist(), song.getAlbum(), song.getYearOfRelease(),
                                        song.getGenre(), song.getLength(), song.getLocation()));
                            }
                        }
                        playlist = new Playlist(playlistName.getText(), playlistSongs);
                    } else if (item instanceof OnlineSong) {
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
                            } catch (Exception ignored2) {
                            }
                        }

                        playlist = new Playlist(playlistName.getText(), new PlaylistSong(thumbnail, onlineSong.getTitle(),
                                onlineSong.getChannelName(), "Youtube", null,
                                "youtube.com", onlineSong.getLength(), onlineSong.getURL()));

                    } else if (item instanceof Playlist) {
                        playlist = new Playlist(playlistName.getText(), ((Playlist) item).getSongList());
                    } else if (item instanceof PlaylistSong) {
                        playlist = new Playlist(playlistName.getText(), (PlaylistSong) item);
                    }
                } else if (onlineSongList.size() > 0) {
                    List<PlaylistSong> playlistSongs = new ArrayList<>();
                    byte[] thumbnail = new byte[0];

                    for (OnlineSong onlineSong : onlineSongList) {
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
                            } catch (Exception ignored2) {
                            }
                        }

                        playlistSongs.add(new PlaylistSong(thumbnail, onlineSong.getTitle(),
                                onlineSong.getChannelName(), "Youtube", null,
                                "youtube.com", onlineSong.getLength(), onlineSong.getURL()));
                    }
                    playlist = new Playlist(playlistName.getText(), playlistSongs);
                }

                PlaylistTabController.getInstance().addPlaylist(playlist);
                playlistName.clear();
                cancelButton.fire();
                PlaylistViewTabController.getInstance().loadPlaylist(playlist);
                PlaylistViewTabController.getInstance().setViewCalledByPlaylistTab(false);
                PlaylistViewTabController.getInstance().setViewCalledByRecentTab(false);
                BaseController.getInstance().handleShowPlaylistView();
            } else {
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.initOwner(root.getScene().getWindow());
                dialog.initStyle(StageStyle.UNDECORATED);
                dialog.setResizable(false);
                dialog.getDialogPane().setMaxWidth(500.0);

                Label header = new Label("Playlist already exist!");
                header.setFont(Font.font("Roboto", 20));
                header.setStyle("-fx-background-color: linear-gradient(to right, #badbf9 0%, #E0C3FC 42%, #f8fbc0 100%); -fx-padding: 20 0 10 20");
                dialog.getDialogPane().setHeader(header);

                Label content = new Label();
                content.setText("Playlist \"" + playlistName.getText() + "\", already exist on this device.\nPlease choose another name for your playlist.");
                content.setWrapText(true);
                content.setMaxWidth(480.0);
                content.setMaxHeight(Double.MAX_VALUE);
                content.setFont(Font.font("Roboto", 18));
                content.setStyle("-fx-background-color: linear-gradient(to right, #badbf9 0%, #E0C3FC 42%, #f8fbc0 100%); -fx-padding: 10 20 40 20");
                dialog.getDialogPane().setContent(content);

                dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            }
        } catch (NullPointerException e) {
            LOGGER.log(Level.ERROR, e);
        }
    }

    /**
     * Loads create playlist dialog with {@code items} added to it if required.
     *
     * @param itemToAddInPlaylist The item to add to playlist while creating.
     * @param onlineSongs         It is youtube playlist which will be added to the new playlist
     */
    public void load(@NamedArg("Item to be added in new Playlist") Object itemToAddInPlaylist, @NamedArg("Youtube playlist to be added in new Playlist") OnlineSong... onlineSongs) {
        this.item = itemToAddInPlaylist;
        this.onlineSongList = FXCollections.observableArrayList(onlineSongs);
        this.dialog.show();
    }
}
