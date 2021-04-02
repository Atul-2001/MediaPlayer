package org.signature.ui.audioPlayer.model;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.signature.dataModel.audioPlayer.Artist;
import org.signature.ui.audioPlayer.BaseController;
import org.signature.ui.audioPlayer.ConsoleController;
import org.signature.ui.audioPlayer.tabs.ArtistViewTabController;
import org.signature.ui.audioPlayer.tabs.RecentlyPlayedTabController;
import org.signature.util.Utils;

public class ArtistPane extends VBox {

    private final Artist artist;
    private static final String DEFAULT_STYLESHEET = "model-node-style.css";
    private static final String DEFAULT_ARTIST_PROFILE = "artist_white.png";

    private final AnchorPane profilePane = new AnchorPane();
    private final Circle artistProfile = new Circle();
    private final JFXButton btn_play = new JFXButton();
    private final JFXButton btn_addToPlaylist = new JFXButton();
    private final Label artistName = new Label();

    public ArtistPane(Artist artist) {
        assert artist != null;
        this.artist = artist;
        setPrefSize(180.0, 230.0);
        setMinSize(180.0, 230.0);
        setMaxSize(180.0, 230.0);
        setAlignment(Pos.TOP_CENTER);
        setSpacing(4.0);
        setFocusTraversable(true);
        setPadding(new Insets(10.0, 6.0, 6.0, 6.0));
        getStylesheets().add(ArtistPane.class.getResource(DEFAULT_STYLESHEET).toString());

        profilePane.setMinSize(160.0, 160.0);
        profilePane.setPrefSize(160.0, 160.0);
        profilePane.setMaxSize(160.0, 160.0);
        ArtistPane.setVgrow(profilePane, Priority.NEVER);

//        artistProfile.setFill(Color.TRANSPARENT);
        artistProfile.setFill(new ImagePattern(new Image(ArtistPane.class.getResourceAsStream(DEFAULT_ARTIST_PROFILE))));
        artistProfile.setRadius(80.0);
        artistProfile.setStroke(Color.LIGHTGREY);
        artistProfile.setStrokeType(StrokeType.INSIDE);
        AnchorPane.setTopAnchor(artistProfile, 0.0);
        AnchorPane.setBottomAnchor(artistProfile, 0.0);
        AnchorPane.setLeftAnchor(artistProfile, 0.0);
        AnchorPane.setRightAnchor(artistProfile, 0.0);

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

        profilePane.getChildren().addAll(artistProfile, btn_play, btn_addToPlaylist);

        this.artistName.setAlignment(Pos.TOP_CENTER);
        this.artistName.setMinSize(160.0, 50.0);
        this.artistName.setPrefSize(160.0, 50.0);
        this.artistName.setMaxSize(160.0, 50.0);
        this.artistName.setTextAlignment(TextAlignment.CENTER);
        this.artistName.setFont(Font.font("Roboto", 16.0));
        this.artistName.setWrapText(true);
        this.artistName.setText(artist.getName() == null || artist.getName().isEmpty() ? "Unknown Artist" : artist.getName());
        ArtistPane.setVgrow(this.artistName, Priority.ALWAYS);

        getChildren().addAll(this.profilePane, this.artistName);
        init();
    }

    private void init() {
        this.setOnMouseEntered(event -> {
            artistProfile.setEffect(Utils.createNodeHoverEffect());
            btn_play.setVisible(true);
            btn_addToPlaylist.setVisible(true);
        });

        this.setOnMouseExited(event -> {
            artistProfile.setEffect(null);
            btn_play.setVisible(false);
            btn_addToPlaylist.setVisible(false);
        });

        this.setOnMouseClicked(event -> {
            if (ArtistViewTabController.getInstance().loadArtist(artist, false, null)) {
                BaseController.getInstance().handleShowArtistView();
            }
        });

        this.btn_play.setOnAction(event -> {
            ConsoleController.getInstance().load(artist);
            RecentlyPlayedTabController.getInstance().addRecentlyPlayed(artist);
        });

        this.btn_addToPlaylist.setOnAction(event -> ConsoleController.getInstance().addToNowPlaying(artist));
    }

    public Artist getArtist() {
        return artist;
    }

    public static String getStylesheet() {
        return DEFAULT_STYLESHEET;
    }

    public static Image getDefaultArtistProfile() {
        return new Image(ArtistPane.class.getResourceAsStream(DEFAULT_ARTIST_PROFILE));
    }

    public String getArtistName() {
        return artistName.getText();
    }
}
