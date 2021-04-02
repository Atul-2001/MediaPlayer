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
import org.signature.dataModel.audioPlayer.Artist;
import org.signature.dataModel.audioPlayer.OnlineSong;
import org.signature.ui.audioPlayer.BaseController;
import org.signature.ui.audioPlayer.ConsoleController;
import org.signature.ui.audioPlayer.tabs.AlbumViewTabController;
import org.signature.util.Utils;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class RecentPane extends VBox {

    private static final String DEFAULT_STYLESHEET = "model-node-style.css";

    private final AnchorPane artPane = new AnchorPane();
    private final ImageView artView = new ImageView();
    private final JFXButton btn_play = new JFXButton();
    private final JFXButton btn_addToPlaylist = new JFXButton();
    private final Label name = new Label();
    private final Label category = new Label();

    private Album album = null;
    private Artist artist = null;
    private OnlineSong onlineSong = null;

    public RecentPane(Object recent) {
        assert recent instanceof Album || recent instanceof Artist || recent instanceof OnlineSong;

        if (recent instanceof Album) {
            this.album = (Album) recent;
        } else if (recent instanceof Artist) {
            this.artist = (Artist) recent;
        } else {
            this.onlineSong = (OnlineSong) recent;
        }

        setPrefSize(160.0, 260.0);
        setMinSize(160.0, 260.0);
        setMaxSize(160.0, 260.0);
        setAlignment(Pos.TOP_CENTER);
        setSpacing(4.0);
        setFocusTraversable(true);
        getStylesheets().add(AlbumPane.class.getResource(DEFAULT_STYLESHEET).toString());

        artPane.setMinSize(160.0, 160.0);
        artPane.setPrefSize(160.0, 160.0);
        artPane.setMaxSize(160.0, 160.0);
        AlbumPane.setVgrow(artPane, Priority.NEVER);

        this.artView.setFitHeight(160.0);
        this.artView.setFitWidth(160.0);
        this.artView.setPickOnBounds(true);
        if (recent instanceof Album) {
            byte[] albumArt = album.getAlbumImage();
            if (albumArt == null || albumArt.length == 0) {
                this.artView.setImage(AlbumPane.getDefaultAlbumArt());
            } else {
                try {
                    this.artView.setImage(SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(albumArt)), null));
                } catch (NullPointerException | IOException ex) {
                    this.artView.setImage(AlbumPane.getDefaultAlbumArt());
                }
            }
        } else if (recent instanceof Artist) {
            this.artView.setImage(ArtistPane.getDefaultArtistProfile());
        } else {
            this.artView.setImage(new Image(onlineSong.getThumbnailURL()));
        }
        AnchorPane.setTopAnchor(this.artView, 0.0);
        AnchorPane.setBottomAnchor(this.artView, 0.0);
        AnchorPane.setLeftAnchor(this.artView, 0.0);
        AnchorPane.setRightAnchor(this.artView, 0.0);

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

        artPane.getChildren().addAll(this.artView, btn_play, btn_addToPlaylist);

        this.name.setAlignment(Pos.TOP_LEFT);
        this.name.setMaxWidth(Double.MAX_VALUE);
        if (recent instanceof Album) {
            this.name.setText(album.getAlbumName() == null || album.getAlbumName().isEmpty() ? "Unknown Album" : album.getAlbumName());
        } else if (recent instanceof Artist) {
            this.name.setText(artist.getName() == null || artist.getName().isEmpty() ? "Unknown Artist" : artist.getName());
        } else {
            this.name.setText(onlineSong.getTitle());
        }
        this.name.setWrapText(true);
        AlbumPane.setVgrow(this.name, Priority.ALWAYS);
        this.name.setFont(Font.font("Roboto", 16.0));

        this.category.setAlignment(Pos.TOP_LEFT);
        this.category.setMinHeight(40.0);
        this.category.setMaxWidth(Double.MAX_VALUE);
        if (recent instanceof Album) {
            String albumArtist = "Album by ";
            if (album.getAlbumArtist() == null) {
                albumArtist += album.getArtist();
            } else if (album.getAlbumArtist().isEmpty()) {
                albumArtist += album.getArtist();
            } else {
                albumArtist += album.getAlbumArtist();
            }
            this.category.setText(albumArtist);
        } else if (recent instanceof Artist) {
            this.category.setText("Artist");
        } else {
            this.category.setText(onlineSong.getChannelName());
        }
        this.category.setWrapText(true);
        AlbumPane.setVgrow(this.category, Priority.ALWAYS);
        this.category.setFont(Font.font("Roboto Light", 14.0));

        getChildren().addAll(this.artPane, this.name, this.category);
        init();
    }

    private void init() {
        this.setOnMouseEntered(event -> {
            artPane.setEffect(Utils.createNodeHoverEffect());
            btn_play.setVisible(true);
            btn_addToPlaylist.setVisible(true);
        });

        this.setOnMouseExited(event -> {
            artPane.setEffect(null);
            btn_play.setVisible(false);
            btn_addToPlaylist.setVisible(false);
        });

        this.setOnMouseClicked(event -> {
            if (album != null) {
                if (AlbumViewTabController.getInstance().loadAlbum(album, false, null)) {
                    BaseController.getInstance().handleShowAlbumView();
                }
            } else if (artist != null) {
            }
        });

        this.btn_play.setOnAction(event -> {
            if (album != null) {
                ConsoleController.getInstance().load(album);
            } else if (artist != null) {
                ConsoleController.getInstance().load(artist);
            } else if (onlineSong != null) {
                ConsoleController.getInstance().addToNowPlaying(onlineSong);
            }
        });
    }
}
