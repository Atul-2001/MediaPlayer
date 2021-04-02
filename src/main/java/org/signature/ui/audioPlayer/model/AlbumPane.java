package org.signature.ui.audioPlayer.model;

import com.jfoenix.controls.JFXButton;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import org.signature.dataModel.audioPlayer.Album;
import org.signature.ui.audioPlayer.BaseController;
import org.signature.ui.audioPlayer.ConsoleController;
import org.signature.ui.audioPlayer.Inventory;
import org.signature.ui.audioPlayer.tabs.AlbumViewTabController;
import org.signature.ui.audioPlayer.tabs.RecentlyPlayedTabController;
import org.signature.util.Utils;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class AlbumPane extends VBox {

    private final Album album;
    private static final String DEFAULT_STYLESHEET = "model-node-style.css";
    private static final String DEFAULT_ALBUM_ART = "album_white.png";

    private final AnchorPane albumArtPane = new AnchorPane();
    private final ImageView albumArtView = new ImageView();
    private final JFXButton btn_play = new JFXButton();
    private final JFXButton btn_addToPlaylist = new JFXButton();
    private final Label albumName = new Label();
    private final Label albumArtist = new Label();

    private final String genre;
    private final String releaseYear;
    private final String creationTime; // creation time of song file (in millis)

    public AlbumPane(Album album) {
        assert album != null;
        this.album = album;

        setPrefSize(160.0, 260.0);
        setMinSize(160.0, 260.0);
        setMaxSize(160.0, 260.0);
        setAlignment(Pos.TOP_CENTER);
        setSpacing(4.0);
        setFocusTraversable(true);
        getStylesheets().add(AlbumPane.class.getResource(DEFAULT_STYLESHEET).toString());

        albumArtPane.setMinSize(160.0, 160.0);
        albumArtPane.setPrefSize(160.0, 160.0);
        albumArtPane.setMaxSize(160.0, 160.0);
        AlbumPane.setVgrow(albumArtPane, Priority.NEVER);

        this.albumArtView.setFitHeight(160.0);
        this.albumArtView.setFitWidth(160.0);
        this.albumArtView.setPickOnBounds(true);
        byte[] albumArt = album.getAlbumImage();
        if (albumArt == null || albumArt.length == 0) {
            this.albumArtView.setImage(new Image(AlbumPane.class.getResourceAsStream(DEFAULT_ALBUM_ART)));
        } else {
            try {
                this.albumArtView.setImage(SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(albumArt)), null));
            } catch (NullPointerException | IOException ex) {
                this.albumArtView.setImage(new Image(AlbumPane.class.getResourceAsStream(DEFAULT_ALBUM_ART)));
            }
        }
        AnchorPane.setTopAnchor(this.albumArtView, 0.0);
        AnchorPane.setBottomAnchor(this.albumArtView, 0.0);
        AnchorPane.setLeftAnchor(this.albumArtView, 0.0);
        AnchorPane.setRightAnchor(this.albumArtView, 0.0);

        btn_play.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn_play.setMinSize(40.0, 40.0);
        btn_play.setPrefSize(40.0, 40.0);
        btn_play.setMaxSize(40.0, 40.0);
        btn_play.getStyleClass().add("btn-controls");
        btn_play.setVisible(false);
        SVGPath btn_play_icon = new SVGPath();
        btn_play_icon.setContent("M8 5v14l11-7z");
        btn_play_icon.setScaleX(1.2);
        btn_play_icon.setScaleY(1.2);
        btn_play.setGraphic(btn_play_icon);
        AnchorPane.setTopAnchor(btn_play, 60.0);
        AnchorPane.setBottomAnchor(btn_play, 60.0);
        AnchorPane.setLeftAnchor(btn_play, 30.0);
        AnchorPane.setRightAnchor(btn_play, 90.0);

        btn_addToPlaylist.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn_addToPlaylist.setMinSize(40.0, 40.0);
        btn_addToPlaylist.setPrefSize(40.0, 40.0);
        btn_addToPlaylist.setMaxSize(40.0, 40.0);
        btn_addToPlaylist.getStyleClass().add("btn-controls");
        btn_addToPlaylist.setVisible(false);
        SVGPath btn_addToPlaylist_icon = new SVGPath();
        btn_addToPlaylist_icon.setContent("M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z");
        btn_addToPlaylist_icon.setScaleX(1.2);
        btn_addToPlaylist_icon.setScaleY(1.2);
        btn_addToPlaylist.setGraphic(btn_addToPlaylist_icon);
        AnchorPane.setTopAnchor(btn_addToPlaylist, 60.0);
        AnchorPane.setBottomAnchor(btn_addToPlaylist, 60.0);
        AnchorPane.setLeftAnchor(btn_addToPlaylist, 90.0);
        AnchorPane.setRightAnchor(btn_addToPlaylist, 30.0);

        albumArtPane.getChildren().addAll(this.albumArtView, btn_play, btn_addToPlaylist);

        this.albumName.setAlignment(Pos.TOP_LEFT);
        this.albumName.setMaxWidth(Double.MAX_VALUE);
        this.albumName.setText(album.getAlbumName() == null || album.getAlbumName().isEmpty() ? "Unknown Album" : album.getAlbumName());
        this.albumName.setWrapText(true);
        AlbumPane.setVgrow(this.albumName, Priority.ALWAYS);
        this.albumName.setFont(Font.font("Roboto", 16.0));

        this.albumArtist.setAlignment(Pos.TOP_LEFT);
        this.albumArtist.setMinHeight(40.0);
        this.albumArtist.setMaxWidth(Double.MAX_VALUE);
        String albumArtist;
        if (album.getAlbumArtist() == null) {
            albumArtist = album.getArtist();
        } else if (album.getAlbumArtist().isEmpty()) {
            albumArtist = album.getArtist();
        } else {
            albumArtist = album.getAlbumArtist();
        }
        this.albumArtist.setText(albumArtist == null || albumArtist.isEmpty() ? "Unknown Artist" : albumArtist);
        this.albumArtist.setWrapText(true);
        AlbumPane.setVgrow(this.albumArtist, Priority.ALWAYS);
        this.albumArtist.setFont(Font.font("Roboto Light", 14.0));

        getChildren().addAll(this.albumArtPane, this.albumName, this.albumArtist);

        String genre = Inventory.getGenreDescription(album.getGenre());
        this.genre = genre == null || genre.isEmpty() ? "Unknown Genre" : genre;
        this.releaseYear = album.getReleaseYear() == null ? "" : album.getReleaseYear();
        this.creationTime = album.getCreationTime() == null ? "" : album.getCreationTime();
        init();
    }

    private void init() {
        this.setOnMouseEntered(event -> {
            albumArtPane.setEffect(Utils.createNodeHoverEffect());
            btn_play.setVisible(true);
            btn_addToPlaylist.setVisible(true);
        });

        this.setOnMouseExited(event -> {
            albumArtPane.setEffect(null);
            btn_play.setVisible(false);
            btn_addToPlaylist.setVisible(false);
        });

        this.setOnMouseClicked(event -> {
            if (this.getParent().getParent().toString().contains("VBox")) {
                if (AlbumViewTabController.getInstance().loadAlbum(album, true, Inventory.getArtist(this.album.getArtist()))) {
                    BaseController.getInstance().handleShowAlbumView();
                }
            } else {
                if (AlbumViewTabController.getInstance().loadAlbum(album, false, null)) {
                    BaseController.getInstance().handleShowAlbumView();
                }
            }
        });

        this.btn_play.setOnAction(event -> {
            ConsoleController.getInstance().load(album);
            RecentlyPlayedTabController.getInstance().addRecentlyPlayed(album);
        });

        this.btn_addToPlaylist.setOnAction(event -> ConsoleController.getInstance().addToNowPlaying(album));
    }

    public Album getAlbum() {
        return album;
    }

    public static String getStylesheet() {
        return DEFAULT_STYLESHEET;
    }

    public String getAlbumName() {
        return albumName.getText();
    }

    public String getArtist() {
        return albumArtist.getText();
    }

    public static Image getDefaultAlbumArt() {
        return new Image(AlbumPane.class.getResourceAsStream(DEFAULT_ALBUM_ART));
    }

    public String getGenre() {
        return genre;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public String getCreationTime() {
        return creationTime;
    }
}
