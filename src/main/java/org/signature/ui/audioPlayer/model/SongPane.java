package org.signature.ui.audioPlayer.model;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import org.signature.App;
import org.signature.dataModel.audioPlayer.Album;
import org.signature.dataModel.audioPlayer.Artist;
import org.signature.dataModel.audioPlayer.Song;
import org.signature.ui.audioPlayer.ConsoleController;
import org.signature.ui.audioPlayer.Inventory;
import org.signature.ui.audioPlayer.tabs.FavouriteTabController;
import org.signature.ui.audioPlayer.tabs.RecentlyPlayedTabController;
import org.signature.ui.audioPlayer.tabs.SongTabController;
import org.signature.util.Utils;

public class SongPane extends HBox {

    private final Song song;
    private final Album album;
    private final String DEFAULT_STYLESHEET = "model-node-style.css";

    private final JFXCheckBox selectSong = new JFXCheckBox();
    private final JFXButton btn_favourite = new JFXButton();
    private final HBox songInfoAndControlPane = new HBox();
    private final VBox songInfoPane = new VBox();
    private final Label songName = new Label();
    private final HBox artistAlbumInfo = new HBox();
    private final Label songAlbum = new Label();
    private final Label songArtist = new Label();
    private final JFXButton btn_play = new JFXButton();
    private final JFXButton btn_addToPlaylist = new JFXButton();
    private final Label songArtistName = new Label();
    private final Label songAlbumName = new Label();
    private final Label songReleaseDate = new Label();
    private final Label songGenre = new Label();
    private final Label songLength = new Label();

    private final String creationTime; // creation time of file (in millis);

    private boolean isBind = false, isFavourite = false, resizedStage1 = false, resizedStage2 = false, resizedStage3 = false;
    private final double songTabWidth = App.getScreenWidth()-320;

    public SongPane(Song song) {
        assert song != null;
        this.song = song;
        this.album = Inventory.getAlbum(song.getAlbum());
        Artist artist = Inventory.getArtist(album.getArtist());

        setAlignment(Pos.CENTER_LEFT);
        setMinSize(914.0, 50.0);
        setPrefSize(914.0, 50.0);
        setMaxSize(Double.MAX_VALUE, 50.0);
        setFocusTraversable(true);
        getStylesheets().add(SongPane.class.getResource(DEFAULT_STYLESHEET).toString());

        selectSong.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        selectSong.setMinSize(20.0, 20.0);
        selectSong.setPrefSize(20.0, 20.0);
        selectSong.setMaxSize(20.0, 20.0);
        selectSong.setVisible(false);
        SongPane.setMargin(selectSong, new Insets(0, 10, 0, 12));

        btn_favourite.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn_favourite.setAlignment(Pos.CENTER);
        btn_favourite.setMinSize(40.0, 40.0);
        btn_favourite.setPrefSize(40.0, 40.0);
        btn_favourite.setMaxSize(40.0, 40.0);
        SVGPath btn_favourite_icon = new SVGPath();
        btn_favourite_icon.setContent("M16.5 3c-1.74 0-3.41.81-4.5 2.09C10.91 3.81 9.24 3 7.5 3 4.42 3 2 5.42 2 8.5c0 3.78 3.4 6.86 8.55 11.54L12 21.35l1.45-1.32C18.6 15.36 22 12.28 22 8.5 22 5.42 19.58 3 16.5 3zm-4.4 15.55l-.1.1-.1-.1C7.14 14.24 4 11.39 4 8.5 4 6.5 5.5 5 7.5 5c1.54 0 3.04.99 3.57 2.36h1.87C13.46 5.99 14.96 5 16.5 5c2 0 3.5 1.5 3.5 3.5 0 2.89-3.14 5.74-7.9 10.05z");
        btn_favourite.setGraphic(btn_favourite_icon);
        SongPane.setMargin(btn_favourite, new Insets(0, 2.0, 0, 0));

    /*----------------------------------------------------------------------------------------------------------------*/
        songInfoAndControlPane.setAlignment(Pos.CENTER_LEFT);
        songInfoAndControlPane.setMinSize(206.0, 50.0);
        songInfoAndControlPane.setPrefSize(240.0, 50.0);
        songInfoAndControlPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        songInfoAndControlPane.setSpacing(2.0);
        SongPane.setHgrow(songInfoAndControlPane, Priority.ALWAYS);
        SongPane.setMargin(songInfoAndControlPane, new Insets(0, 20.0, 0, 0));

        /*---Children of songInfoAndControlPane---Start---------------------------------------------------------------*/
        songInfoPane.setMinSize(120.0, 50.0);
        songInfoPane.setPrefSize(134.0, 50.0);
        songInfoPane.setMaxSize(Double.MAX_VALUE, 50.0);
        HBox.setHgrow(songInfoPane, Priority.ALWAYS);
        HBox.setMargin(songInfoPane, new Insets(0, 0, 0, 2.0));

        /*---Children of songInfoPane---Start-------------------------------------------------------------------------*/
        this.songName.setMinSize(120.0, 50.0);
        this.songName.setPrefSize(134.0, 50.0);
        this.songName.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.songName.setTextOverrun(OverrunStyle.CLIP);
        this.songName.setText(song.getTitle() == null || song.getTitle().isEmpty() ? "Unknown Song" : song.getTitle());
        this.songName.setFont(Font.font("Roboto Black", 14.0));
        VBox.setVgrow(this.songName, Priority.ALWAYS);

        artistAlbumInfo.setAlignment(Pos.CENTER_LEFT);
        artistAlbumInfo.setMinSize(120.0, 18.0);
        artistAlbumInfo.setMinSize(134.0, 18.0);
        artistAlbumInfo.setMinSize(Double.MAX_VALUE, Double.MAX_VALUE);
        artistAlbumInfo.setSpacing(2.0);
        VBox.setVgrow(artistAlbumInfo, Priority.NEVER);

        /*---Children of artistAlbumInfo---Start----------------------------------------------------------------------*/
        this.songAlbum.setMaxHeight(Double.MAX_VALUE);
        this.songAlbum.setFont(Font.font("Roboto", 12.0));
        this.songAlbum.setTextFill(Color.GREY);
        this.songAlbum.setText(album.getAlbumName() == null || album.getAlbumName().isEmpty() ? "Unknown Album" : album.getAlbumName());
        HBox.setHgrow(this.songAlbum, Priority.ALWAYS);

        Label dotSeparator = new Label();
        dotSeparator.setAlignment(Pos.CENTER);
        dotSeparator.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        dotSeparator.setMinSize(18.0, 18.0);
        dotSeparator.setPrefSize(18.0, 18.0);
        dotSeparator.setMaxSize(18.0, 18.0);
        HBox.setHgrow(dotSeparator, Priority.NEVER);
        SVGPath dotSeparatorIcon = new SVGPath();
        dotSeparatorIcon.setContent("M 18 18 m -3, 0 a 3,3 0 1,0 6,0 a 3,3 0 1,0 -6,0");
        dotSeparatorIcon.setFill(Color.GREY);
        dotSeparator.setGraphic(dotSeparatorIcon);

        this.songArtist.setMaxHeight(Double.MAX_VALUE);
        this.songArtist.setFont(Font.font("Roboto", 12.0));
        this.songArtist.setTextFill(Color.GREY);
        this.songArtist.setText(artist.getName() == null || artist.getName().isEmpty() ? "Unknown Artist" : artist.getName());
        HBox.setHgrow(this.songArtist, Priority.ALWAYS);

        artistAlbumInfo.getChildren().addAll(this.songAlbum, dotSeparator, this.songArtist);
        /*---Children of artistAlbumInfo---End------------------------------------------------------------------------*/

        songInfoPane.getChildren().addAll(this.songName, artistAlbumInfo);
        /*---Children of songInfoPane---End---------------------------------------------------------------------------*/

        btn_play.setAlignment(Pos.CENTER);
        btn_play.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn_play.setMinSize(40.0, 40.0);
        btn_play.setPrefSize(40.0, 40.0);
        btn_play.setMaxSize(40.0, 40.0);
        SVGPath btn_play_icon = new SVGPath();
        btn_play_icon.setContent("M8 5v14l11-7z");
        btn_play.setGraphic(btn_play_icon);
        HBox.setHgrow(btn_play, Priority.NEVER);

        btn_addToPlaylist.setAlignment(Pos.CENTER);
        btn_addToPlaylist.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn_addToPlaylist.setMinSize(40.0, 40.0);
        btn_addToPlaylist.setPrefSize(40.0, 40.0);
        btn_addToPlaylist.setMaxSize(40.0, 40.0);
        SVGPath btn_addToPlaylist_icon = new SVGPath();
        btn_addToPlaylist_icon.setContent("M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z");
        btn_addToPlaylist.setGraphic(btn_addToPlaylist_icon);
        HBox.setHgrow(btn_addToPlaylist, Priority.NEVER);

        songInfoAndControlPane.getChildren().addAll(songInfoPane, btn_play, btn_addToPlaylist);
        /*---Children of songInfoAndControlPane---End-----------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/

        this.songArtistName.setMinSize(102.0, 50.0);
        this.songArtistName.setPrefSize(176.0, 50.0);
        this.songArtistName.setMaxSize(Double.MAX_VALUE, 50.0);
        this.songArtistName.setFont(Font.font("Roboto", 14.0));
        this.songArtistName.setTextOverrun(OverrunStyle.CLIP);
        this.songArtistName.setText(artist.getName() == null || artist.getName().isEmpty() ? "Unknown Artist" : artist.getName());
        SongPane.setHgrow(this.songArtistName, Priority.ALWAYS);
        SongPane.setMargin(this.songArtistName, new Insets(0, 12.0, 0, 0));

        this.songAlbumName.setMinSize(102.0, 50.0);
        this.songAlbumName.setPrefSize(176.0, 50.0);
        this.songAlbumName.setMaxSize(Double.MAX_VALUE, 50.0);
        this.songAlbumName.setFont(Font.font("Roboto", 14.0));
        this.songAlbumName.setTextOverrun(OverrunStyle.CLIP);
        this.songAlbumName.setText(album.getAlbumName() == null || album.getAlbumName().isEmpty() ? "Unknown Album" : album.getAlbumName());
        SongPane.setHgrow(this.songAlbumName, Priority.ALWAYS);
        SongPane.setMargin(this.songAlbumName, new Insets(0, 28.0, 0, 0));

        this.songReleaseDate.setAlignment(Pos.CENTER);
        this.songReleaseDate.setMinSize(34.0, 50.0);
        this.songReleaseDate.setPrefSize(50.0, 50.0);
        this.songReleaseDate.setMaxSize(50.0, 50.0);
        this.songReleaseDate.setFont(Font.font("Roboto", 14.0));
        this.songReleaseDate.setText(song.getYearOfRelease());
        SongPane.setHgrow(this.songReleaseDate, Priority.NEVER);
        SongPane.setMargin(this.songReleaseDate, new Insets(0, 28.0, 0, 0));

        this.songGenre.setMinSize(102.0, 50.0);
        this.songGenre.setPrefSize(176.0, 50.0);
        this.songGenre.setMaxSize(Double.MAX_VALUE, 50.0);
        this.songGenre.setFont(Font.font("Roboto", 14.0));
        this.songGenre.setTextOverrun(OverrunStyle.CLIP);
        String songGenre = Inventory.getGenreDescription(song.getGenre());
        this.songGenre.setText(songGenre == null || songGenre.isEmpty() ? "Unknown Genre" : songGenre);
        SongPane.setHgrow(this.songGenre, Priority.ALWAYS);
        SongPane.setMargin(this.songGenre, new Insets(0, 12.0, 0, 0));

        this.songLength.setAlignment(Pos.CENTER_RIGHT);
        this.songLength.setMinSize(60.0, 50.0);
        this.songLength.setPrefSize(60.0, 50.0);
        this.songLength.setMaxSize(60.0, 50.0);
        this.songLength.setFont(Font.font("Roboto", 14.0));
        this.songLength.setText(Utils.getDuration(song.getLength()));
        SongPane.setHgrow(this.songLength, Priority.NEVER);
        SongPane.setMargin(this.songLength, new Insets(0, 12.0, 0, 0));

        getChildren().addAll(this.selectSong, this.btn_favourite, this.songInfoAndControlPane, this.songArtistName, this.songAlbumName, this.songReleaseDate, this.songGenre, this.songLength);

        this.creationTime = song.getCreationTime() == null ? "" : song.getCreationTime();
        init();
    }

    private void init() {
        songInfoAndControlPane.getChildren().removeAll(btn_play, btn_addToPlaylist);
        songInfoPane.getChildren().removeAll(artistAlbumInfo);

        this.setOnMouseEntered(event -> {
            if (!isBind) selectSong.setVisible(true);
            songInfoAndControlPane.getChildren().addAll(btn_play, btn_addToPlaylist);
        });

        this.setOnMouseExited(event -> {
            if (!isBind) selectSong.setVisible(false);
            songInfoAndControlPane.getChildren().removeAll(btn_play, btn_addToPlaylist);
        });

        btn_play.setOnAction(event -> {
            ConsoleController.getInstance().load(Inventory.getCachedSongs(), song);
            RecentlyPlayedTabController.getInstance().addRecentlyPlayed(album);
        });

        selectSong.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!SongTabController.getInstance().getShowSelectionBox().get()) {
                SongTabController.getInstance().getShowSelectionBox().set(true);
            }

            if (newValue) {
                SongTabController.getInstance().addSelectedSongs();
            } else {
                SongTabController.getInstance().removeSelectedSong();
            }
        });

        SongTabController.getInstance().getShowSelectionBox().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                isBind = true;
                selectSong.visibleProperty().bind(observable);
            } else {
                selectSong.visibleProperty().unbind();
                isBind = false;
            }
        });

        SongTabController.getInstance().getTabWidth().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() <= Math.round(songTabWidth * 0.7286) && !resizedStage3 && App.getStage().getWidth() <= Math.round(App.getScreenWidth() * 0.558)) {
                getChildren().removeAll(songArtistName, songReleaseDate, songGenre);
                songInfoPane.getChildren().addAll(artistAlbumInfo);
                resizedStage3 = true;
            } else if (newValue.doubleValue() > Math.round(songTabWidth * 0.7286) && resizedStage3 && App.getStage().getWidth() > Math.round(App.getScreenWidth() * 0.558)) {
                getChildren().add(3, songArtistName);
                getChildren().add(4, songGenre);
                songInfoPane.getChildren().removeAll(artistAlbumInfo);
                resizedStage3 = false;
            }

            if (newValue.doubleValue() <= Math.round(songTabWidth * 0.723) && !resizedStage2 && App.getStage().getWidth() > Math.round(App.getScreenWidth() * 0.558)) {
                getChildren().removeAll(songReleaseDate);
                resizedStage2 = true;
            } else if (newValue.doubleValue() > Math.round(songTabWidth * 0.723) && resizedStage2 && App.getStage().getWidth() > Math.round(App.getScreenWidth() * 0.558)) {
                getChildren().add(4, songReleaseDate);
                resizedStage2 = false;
            }

            if (newValue.doubleValue() <= Math.round(songTabWidth * 0.87) && !resizedStage1) {
                getChildren().removeAll(songAlbumName);
                resizedStage1 = true;
            } else if (newValue.doubleValue() > Math.round(songTabWidth * 0.87) && resizedStage1) {
                getChildren().add(4, songAlbumName);
                resizedStage1 = false;
            }
        });

        songName.widthProperty().addListener((observable, oldValue, newValue) -> SongTabController.getInstance().setSongNameFieldWidth(newValue.intValue()));
        songName.prefWidthProperty().bind(SongTabController.getInstance().songNameFieldWidthProperty());

        songArtistName.widthProperty().addListener((observable, oldValue, newValue) -> SongTabController.getInstance().setArtistNameFieldWidth(newValue.intValue()));
        songArtistName.prefWidthProperty().bind(SongTabController.getInstance().artistNameFieldWidthProperty());

        songAlbumName.widthProperty().addListener((observable, oldValue, newValue) -> SongTabController.getInstance().setAlbumNameFieldWidth(newValue.intValue()));
        songAlbumName.prefWidthProperty().bind(SongTabController.getInstance().albumNameFieldWidthProperty());

        songGenre.widthProperty().addListener((observable, oldValue, newValue) -> SongTabController.getInstance().setGenreFieldWidth(newValue.intValue()));
        songGenre.prefWidthProperty().bind(SongTabController.getInstance().genreFieldWidthProperty());

        if (song.getFavourite().get()) {
            SVGPath yFavourite = new SVGPath();
            yFavourite.setContent("M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z");
            yFavourite.setFill(Color.rgb(219, 50, 54, 0.8));
            btn_favourite.setGraphic(yFavourite);
            isFavourite = true;
        }
        btn_favourite.setOnAction(this::handleSetFavourite);
        song.getFavourite().addListener((observable, oldValue, newValue) -> {
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

        if (song.isPlayingProperty().get()) {
            setStyle("-fx-background-color: rgba(72, 133, 237, 0.4);");
        }
        song.isPlayingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                setStyle("-fx-background-color: rgba(72, 133, 237, 0.4);");
            } else {
                setStyle(null);
            }
        });
    }

    private void handleSetFavourite(ActionEvent actionEvent) {
        if (isFavourite) {
            song.setFavourite(false);
            FavouriteTabController.getInstance().removeSong(song);
            isFavourite = false;
        } else {
            song.setFavourite(true);
            FavouriteTabController.getInstance().addSong(song);
            isFavourite = true;
        }
    }

    public Song getSong() {
        return song;
    }

    public String getStyleSheet() {
        return DEFAULT_STYLESHEET;
    }

    public String getSongTitle() {
        return songName.getText();
    }

    public String getArtist() {
        return songArtistName.getText();
    }

    public String getAlbum() {
        return songAlbumName.getText();
    }

    public String getGenre() {
        return songGenre.getText();
    }

    public String getCreationTime() {
        return creationTime;
    }
}
