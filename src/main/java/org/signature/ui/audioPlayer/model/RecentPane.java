package org.signature.ui.audioPlayer.model;

import com.jfoenix.controls.JFXButton;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import org.signature.dataModel.audioPlayer.Album;
import org.signature.dataModel.audioPlayer.Artist;
import org.signature.ui.audioPlayer.BaseController;
import org.signature.ui.audioPlayer.ConsoleController;
import org.signature.ui.audioPlayer.Inventory;
import org.signature.ui.audioPlayer.tabs.AlbumViewTabController;
import org.signature.ui.audioPlayer.tabs.FavouriteTabController;
import org.signature.util.Utils;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class RecentPane extends VBox {

    private static final String DEFAULT_STYLESHEET = "model-node-style.css";

    private final AnchorPane artPane = new AnchorPane();
    private final ImageView artView = new ImageView();
    private final JFXButton btn_favourite = new JFXButton();
    private final JFXButton btn_play = new JFXButton();
    private final JFXButton btn_addToPlaylist = new JFXButton();
    private final Label name = new Label();
    private final Label category = new Label();

    private Album album;
    private Artist albumsArtist;
    private Artist artist;

    private boolean isFavourite = false;

    public RecentPane(Object recent) {
        assert recent instanceof Album || recent instanceof Artist;

        if (recent instanceof Album) {
            this.album = (Album) recent;
            this.albumsArtist = Inventory.getArtist(album.getArtist());
        } else {
            this.artist = (Artist) recent;
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
        } else {
            this.artView.setImage(ArtistPane.getDefaultArtistProfile());
        }
        AnchorPane.setTopAnchor(this.artView, 0.0);
        AnchorPane.setBottomAnchor(this.artView, 0.0);
        AnchorPane.setLeftAnchor(this.artView, 0.0);
        AnchorPane.setRightAnchor(this.artView, 0.0);

        btn_favourite.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn_favourite.setMinSize(40.0, 40.0);
        btn_favourite.setPrefSize(40.0, 40.0);
        btn_favourite.setMaxSize(40.0, 40.0);
        btn_favourite.getStyleClass().add("btn-controls");
        btn_favourite.setVisible(false);
        SVGPath btn_favourite_icon = new SVGPath();
        btn_favourite_icon.setContent("M16.5 3c-1.74 0-3.41.81-4.5 2.09C10.91 3.81 9.24 3 7.5 3 4.42 3 2 5.42 2 8.5c0 3.78 3.4 6.86 8.55 11.54L12 21.35l1.45-1.32C18.6 15.36 22 12.28 22 8.5 22 5.42 19.58 3 16.5 3zm-4.4 15.55l-.1.1-.1-.1C7.14 14.24 4 11.39 4 8.5 4 6.5 5.5 5 7.5 5c1.54 0 3.04.99 3.57 2.36h1.87C13.46 5.99 14.96 5 16.5 5c2 0 3.5 1.5 3.5 3.5 0 2.89-3.14 5.74-7.9 10.05z");
        btn_favourite_icon.setScaleX(1.2);
        btn_favourite_icon.setScaleY(1.2);
        btn_favourite.setGraphic(btn_favourite_icon);
        AnchorPane.setTopAnchor(btn_favourite, 40.0);
        AnchorPane.setBottomAnchor(btn_favourite, 80.0);
        AnchorPane.setLeftAnchor(btn_favourite, 60.0);
        AnchorPane.setRightAnchor(btn_favourite, 60.0);

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
        AnchorPane.setTopAnchor(btn_play, 80.0);
        AnchorPane.setBottomAnchor(btn_play, 40.0);
        AnchorPane.setLeftAnchor(btn_play, 20.0);
        AnchorPane.setRightAnchor(btn_play, 100.0);

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
        AnchorPane.setTopAnchor(btn_addToPlaylist, 80.0);
        AnchorPane.setBottomAnchor(btn_addToPlaylist, 40.0);
        AnchorPane.setLeftAnchor(btn_addToPlaylist, 100.0);
        AnchorPane.setRightAnchor(btn_addToPlaylist, 20.0);

        artPane.getChildren().addAll(this.artView, btn_favourite, btn_play, btn_addToPlaylist);

        this.name.setAlignment(Pos.TOP_LEFT);
        this.name.setMaxWidth(Double.MAX_VALUE);
        if (recent instanceof Album) {
            this.name.setText(album.getAlbumName() == null || album.getAlbumName().isEmpty() ? "Unknown Album" : album.getAlbumName());
        } else {
            this.name.setText(artist.getName() == null || artist.getName().isEmpty() ? "Unknown Artist" : artist.getName());
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
                albumArtist += albumsArtist.getName();
            } else if (album.getAlbumArtist().isEmpty()) {
                albumArtist += albumsArtist.getName();
            } else {
                albumArtist += album.getAlbumArtist();
            }
            this.category.setText(albumArtist);
        } else {
            this.category.setText("Artist");
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
            btn_favourite.setVisible(true);
            btn_play.setVisible(true);
            btn_addToPlaylist.setVisible(true);
        });

        this.setOnMouseExited(event -> {
            artPane.setEffect(null);
            btn_favourite.setVisible(false);
            btn_play.setVisible(false);
            btn_addToPlaylist.setVisible(false);
        });

        this.setOnMouseClicked(event -> {
            if (artist == null) {
                if (AlbumViewTabController.getInstance().loadAlbum(album)) {
                    BaseController.getInstance().handleShowAlbumView();
                }
            } else {
            }
        });

        if (artist == null) {
            if (album.getFavourite().get()) {
                SVGPath yFavourite = new SVGPath();
                yFavourite.setContent("M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z");
                yFavourite.setFill(Color.rgb(219, 50, 54, 0.8));
                btn_favourite.setGraphic(yFavourite);
                isFavourite = true;
            }
            this.btn_favourite.setOnAction(this::handleSetFavourite);
            album.getFavourite().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    SVGPath yFavourite = new SVGPath();
                    yFavourite.setContent("M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z");
                    yFavourite.setFill(Color.rgb(219, 50, 54, 0.8));
                    btn_favourite.setGraphic(yFavourite);
                } else {
                    SVGPath nFavourite = new SVGPath();
                    nFavourite.setContent("M16.5 3c-1.74 0-3.41.81-4.5 2.09C10.91 3.81 9.24 3 7.5 3 4.42 3 2 5.42 2 8.5c0 3.78 3.4 6.86 8.55 11.54L12 21.35l1.45-1.32C18.6 15.36 22 12.28 22 8.5 22 5.42 19.58 3 16.5 3zm-4.4 15.55l-.1.1-.1-.1C7.14 14.24 4 11.39 4 8.5 4 6.5 5.5 5 7.5 5c1.54 0 3.04.99 3.57 2.36h1.87C13.46 5.99 14.96 5 16.5 5c2 0 3.5 1.5 3.5 3.5 0 2.89-3.14 5.74-7.9 10.05z");
                    btn_favourite.setGraphic(nFavourite);
                }
            });
        } else {
            if (artist.getFavourite().get()) {
                SVGPath yFavourite = new SVGPath();
                yFavourite.setContent("M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z");
                yFavourite.setFill(Color.rgb(219, 50, 54, 0.8));
                btn_favourite.setGraphic(yFavourite);
                isFavourite = true;
            }
            this.btn_favourite.setOnAction(this::handleSetFavourite);
            artist.getFavourite().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    SVGPath yFavourite = new SVGPath();
                    yFavourite.setContent("M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z");
                    yFavourite.setFill(Color.rgb(219, 50, 54, 0.8));
                    btn_favourite.setGraphic(yFavourite);
                } else {
                    SVGPath nFavourite = new SVGPath();
                    nFavourite.setContent("M16.5 3c-1.74 0-3.41.81-4.5 2.09C10.91 3.81 9.24 3 7.5 3 4.42 3 2 5.42 2 8.5c0 3.78 3.4 6.86 8.55 11.54L12 21.35l1.45-1.32C18.6 15.36 22 12.28 22 8.5 22 5.42 19.58 3 16.5 3zm-4.4 15.55l-.1.1-.1-.1C7.14 14.24 4 11.39 4 8.5 4 6.5 5.5 5 7.5 5c1.54 0 3.04.99 3.57 2.36h1.87C13.46 5.99 14.96 5 16.5 5c2 0 3.5 1.5 3.5 3.5 0 2.89-3.14 5.74-7.9 10.05z");
                    btn_favourite.setGraphic(nFavourite);
                }
            });
        }

        this.btn_play.setOnAction(event -> {
            if (artist == null) {
                ConsoleController.getInstance().load(album);
            } else {
                ConsoleController.getInstance().load(artist);
            }
        });
    }

    private void handleSetFavourite(ActionEvent actionEvent) {
        if (isFavourite) {
            if (artist == null) {
                album.setFavourite(false);
                FavouriteTabController.getInstance().removeAlbum(album);
            } else {
                artist.setFavourite(false);
                FavouriteTabController.getInstance().removeArtist(artist);
            }
            isFavourite = false;
        } else {
            if (artist == null) {
                album.setFavourite(true);
                FavouriteTabController.getInstance().addAlbum(album);
            } else {
                artist.setFavourite(true);
                FavouriteTabController.getInstance().addArtist(artist);
            }
            isFavourite = true;
        }
    }
}
