package org.signature.ui.audioPlayer;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import javafx.animation.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.App;
import org.signature.animations.Animation;
import org.signature.dataModel.audioPlayer.Playlist;
import org.signature.ui.audioPlayer.dialogs.*;
import org.signature.ui.audioPlayer.tabs.*;
import org.signature.util.Alerts;
import org.signature.util.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BaseController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(BaseController.class);

    private static BaseController instance = null;

    @FXML
    private BorderPane playerBase;
    @FXML
    private HBox topMenuAndSearchBox;
    @FXML
    private JFXDrawer menuDrawer;
    @FXML
    private VBox sideMenu;
    @FXML
    private StackPane searchStack;
    @FXML
    private ToggleGroup menuTabGroup;
    @FXML
    private ToggleButton btnShowSearchBar;
    @FXML
    private HBox searchBox;
    @FXML
    private TextField searchField;
    @FXML
    private ToggleButton btnRecentlyPlayed, btnSongs, btnAlbums, btnArtists, btnBrowseOnline, btnSettings;
    @FXML
    private Separator separator;
    @FXML
    private StackPane showPlaylistStack;
    @FXML
    private ToggleButton btnShowPlaylist, btnShowPlaylist2;
    @FXML
    private JFXButton btn_addPlaylist1;
    @FXML
    private VBox addedPlaylists;
    @FXML
    private StackPane tabStack;

    private VBox tab_song, tab_artist, tab_album, tab_recentlyPlayed, tab_browseOnline, tab_playlists, tab_settings, tab_albumView, tab_artistView, tab_playlistView;
    private StackPane tab_searchResult;

    private boolean topBarShown = false;
    private final BooleanProperty sideMenu_collapsed_by_button = new SimpleBooleanProperty(false);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        loadSideMenuDrawer();

        ((VBox) btn_addPlaylist1.getParent()).getChildren().removeAll(btn_addPlaylist1, addedPlaylists);

        searchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                searchBox.setEffect(new DropShadow());
            } else {
                searchBox.setEffect(null);
            }
        });

        btnShowPlaylist2.selectedProperty().bindBidirectional(btnShowPlaylist.selectedProperty());

        playerBase.setTop(null);
        playerBase.widthProperty().addListener((observable, oldValue, newValue) -> {
            double screenWidth = App.getScreenWidth();
            if (newValue.doubleValue() <= screenWidth * 0.76 && sideMenu.getWidth() == 320.0) {
                Timeline timeline = Animation.createTimelineAnimation(sideMenu, 320.0, 50.0);
                timeline.setOnFinished(event -> {
                    Utils.flipStackPane(searchStack);
                    while (searchStack.getChildren().get(1).toString().contains("HBox")) {
                        Utils.flipStackPane(searchStack);
                    }
                    separator.setVisible(false);
                    Utils.flipStackPane(showPlaylistStack);
                    while (showPlaylistStack.getChildren().get(1).toString().contains("HBox")) {
                        Utils.flipStackPane(showPlaylistStack);
                    }
                    Utils.replaceNode(showPlaylistStack.getParent(), addedPlaylists, btn_addPlaylist1);
                });
                timeline.play();

                sideMenu_collapsed_by_button.set(false);
            } else if (newValue.doubleValue() > screenWidth * 0.76 && sideMenu.getWidth() == 50.0 && !sideMenu_collapsed_by_button.get()) {
                Animation.createTimelineAnimation(sideMenu, 50.0, 320.0).play();
                Utils.flipStackPane(searchStack);
                while (!searchStack.getChildren().get(1).toString().contains("HBox")) {
                    Utils.flipStackPane(searchStack);
                }
                separator.setVisible(true);
                Utils.flipStackPane(showPlaylistStack);
                while (!showPlaylistStack.getChildren().get(1).toString().contains("HBox")) {
                    Utils.flipStackPane(showPlaylistStack);
                }
                Utils.replaceNode(showPlaylistStack.getParent(), btn_addPlaylist1, addedPlaylists);
            }

            if (newValue.doubleValue() <= Math.round(screenWidth * 0.558) && !topBarShown) {
                playerBase.setTop(topMenuAndSearchBox);
                playerBase.setLeft(null);
                TranslateTransition transition = new TranslateTransition(Duration.seconds(0.4));
                FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.4));
                ParallelTransition parallelTransition = new ParallelTransition(topMenuAndSearchBox, transition, fadeTransition);
                transition.setToY(0.0);
                fadeTransition.setToValue(1.0);
                parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
                parallelTransition.play();
                topBarShown = true;
            } else if (newValue.doubleValue() > Math.round(screenWidth * 0.558) && topBarShown) {
                if (((HBox) ((StackPane) topMenuAndSearchBox.getChildren().get(2)).getChildren().get(0)).getChildren().get(0).toString().contains("Label")) {
                    Utils.flipStackPane((StackPane) topMenuAndSearchBox.getChildren().get(2));
                }
                TranslateTransition transition = new TranslateTransition(Duration.seconds(0.4));
                FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.4));
                ParallelTransition parallelTransition = new ParallelTransition(topMenuAndSearchBox, transition, fadeTransition);
                transition.setToY(-40.0);
                fadeTransition.setToValue(0.0);
                parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
                parallelTransition.play();
                parallelTransition.setOnFinished(event -> {
                    if (menuDrawer.isOpened()) menuDrawer.close();
                    playerBase.setTop(null);
                    playerBase.setLeft(sideMenu);
                });
                topBarShown = false;
            }
        });

        if (sideMenu.getPrefWidth() == 320.0) {
            ((VBox) showPlaylistStack.getParent()).getChildren().add(addedPlaylists);
        } else {
            ((VBox) showPlaylistStack.getParent()).getChildren().add(btn_addPlaylist1);
        }

        loadDialogs();
        loadPlayerTabs();
        btnSongs.fire();
        loadPlayerConsole();

//        LOGGER.log(Level.INFO, "Audio Player Loaded !!");
    }

    public static BaseController getInstance() {
        return instance;
    }

    public BorderPane getRoot() {
        return playerBase;
    }

    public ToggleButton getBtnRecentlyPlayed() {
        return btnRecentlyPlayed;
    }

    public ToggleButton getBtnSongs() {
        return btnSongs;
    }

    public ToggleButton getBtnAlbums() {
        return btnAlbums;
    }

    public ToggleButton getBtnArtists() {
        return btnArtists;
    }

    public ToggleButton getBtnShowPlaylist() {
        return btnShowPlaylist;
    }

    private void loadSideMenuDrawer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SideMenuContent.fxml"));
            menuDrawer.setSidePane((VBox) loader.load());
            menuDrawer.minHeightProperty().bind(playerBase.heightProperty());
            DrawerWindowController controller = loader.getController();

            controller.getBtnRecentlyPlayed().selectedProperty().bindBidirectional(btnRecentlyPlayed.selectedProperty());
            controller.getBtnSongs().selectedProperty().bindBidirectional(btnSongs.selectedProperty());
            controller.getBtnAlbums().selectedProperty().bindBidirectional(btnAlbums.selectedProperty());
            controller.getBtnArtists().selectedProperty().bindBidirectional(btnArtists.selectedProperty());
            controller.getBtnBrowseOnline().selectedProperty().bindBidirectional(btnBrowseOnline.selectedProperty());
            controller.getBtnShowPlaylist().selectedProperty().bindBidirectional(btnShowPlaylist.selectedProperty());
            controller.getBtnSettings().selectedProperty().bindBidirectional(btnSettings.selectedProperty());

            controller.getBtnRecentlyPlayed().setOnAction(this::handleRecentlyPlayed);
            controller.getBtnSongs().setOnAction(this::handleShowSongs);
            controller.getBtnAlbums().setOnAction(this::handleShowAlbums);
            controller.getBtnArtists().setOnAction(this::handleShowArtists);
            controller.getBtnBrowseOnline().setOnAction(this::handleShowBrowseOnline);
            controller.getBtnShowPlaylist().setOnAction(this::handleShowPlaylist);
            controller.getBtnSettings().setOnAction(this::handleShowSettings);
        } catch (IOException | NullPointerException ex) {
            LOGGER.log(Level.ERROR, ex.getClass() + " " + ex.getLocalizedMessage(), ex);
            Alerts.loadTimeError(ex);
        }
    }

    private void loadDialogs() {
        try {
            FXMLLoader.load(AboutSongDialogController.class.getResource("About.fxml"));
            FXMLLoader.load(AboutSongDialogController.class.getResource("AboutSongDialog.fxml"));
            FXMLLoader.load(AddMusicDialogController.class.getResource("AddLocalMusicDialog.fxml"));
            FXMLLoader.load(AddPlaylistDialogController.class.getResource("AddPlaylistDialog.fxml"));
            FXMLLoader.load(AboutSongDialogController.class.getResource("AddToListDialog.fxml"));
            FXMLLoader.load(DownloadsController.class.getResource("Downloads.fxml"));
            FXMLLoader.load(AboutSongDialogController.class.getResource("EditAlbumInfoDialog.fxml"));
            FXMLLoader.load(AboutSongDialogController.class.getResource("EditSongInfoDialog.fxml"));
            FXMLLoader.load(AboutSongDialogController.class.getResource("Equalizer.fxml"));
            FXMLLoader.load(AboutSongDialogController.class.getResource("Feedback.fxml"));
            FXMLLoader.load(AboutSongDialogController.class.getResource("Help.fxml"));
            FXMLLoader.load(PlayingListDialogController.class.getResource("PlayingListDialog.fxml"));
            App.updateProgress(5.0);
        } catch (NullPointerException | IOException ex) {
            LOGGER.log(Level.ERROR, ex.getClass() + " " + ex.getLocalizedMessage(), ex);
            Alerts.loadTimeError(ex);
        }
    }

    private void loadPlayerTabs() {
        try {
            tab_song = FXMLLoader.load(SongTabController.class.getResource("Tab_Song.fxml"));
            tab_song.setVisible(false);

            tab_album = FXMLLoader.load(AlbumTabController.class.getResource("Tab_Album.fxml"));
            tab_album.setVisible(false);

            tab_artist = FXMLLoader.load(ArtistTabController.class.getResource("Tab_Artist.fxml"));
            tab_artist.setVisible(false);

            tab_playlists = FXMLLoader.load(PlaylistTabController.class.getResource("Tab_Playlists.fxml"));
            tab_playlists.setVisible(false);
            App.updateProgress(5.0);

            tab_searchResult = FXMLLoader.load(SearchResultTabController.class.getResource("Tab_SearchResult.fxml"));
            tab_searchResult.setVisible(false);
            tab_searchResult.visibleProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.equals(Boolean.FALSE)) {
                    searchField.clear();
                }
            });

            tab_recentlyPlayed = FXMLLoader.load(RecentlyPlayedTabController.class.getResource("Tab_RecentlyPlayed.fxml"));
            tab_recentlyPlayed.setVisible(false);
            App.updateProgress(5.0);

            tab_browseOnline = FXMLLoader.load(BrowseOnlineTabController.class.getResource("Tab_BrowseOnline.fxml"));
            tab_browseOnline.setVisible(false);

            tab_settings = FXMLLoader.load(SettingsTabController.class.getResource("Tab_Settings.fxml"));
            tab_settings.setVisible(false);

            tab_albumView = FXMLLoader.load(AlbumViewTabController.class.getResource("Tab_AlbumView.fxml"));
            tab_albumView.setVisible(false);

            tab_artistView = FXMLLoader.load(ArtistViewTabController.class.getResource("Tab_ArtistView.fxml"));
            tab_artistView.setVisible(false);

            tab_playlistView = FXMLLoader.load(PlaylistViewTabController.class.getResource("Tab_PlaylistView.fxml"));
            tab_playlistView.setVisible(false);

            tabStack.getChildren().addAll(tab_searchResult, tab_recentlyPlayed, tab_song, tab_album, tab_artist, tab_browseOnline, tab_playlists, tab_settings, tab_albumView, tab_artistView, tab_playlistView);
        } catch (NullPointerException | IOException ex) {
            LOGGER.log(Level.ERROR, ex.getClass() + " " + ex.getLocalizedMessage(), ex);
            Alerts.loadTimeError(ex);
        }
    }

    private void loadPlayerConsole() {
        try {
            Node playerConsole = FXMLLoader.load(ConsoleController.class.getResource("AudioPlayerConsole.fxml"));
            playerBase.setBottom(playerConsole);

            TranslateTransition transition = new TranslateTransition(Duration.seconds(2.8));
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2.8));
            ParallelTransition parallelTransition = new ParallelTransition(playerConsole, transition, fadeTransition);
            transition.setToY(0.0);
            fadeTransition.setToValue(1.0);
            parallelTransition.setInterpolator(Interpolator.EASE_BOTH);
            parallelTransition.play();

            FXMLLoader.load(MiniPlayerViewController.class.getResource("MiniPlayerView.fxml"));
            App.updateProgress(5.0);
        } catch (Exception ex) {
            LOGGER.log(Level.ERROR, " Failed to load player console!\n" + ex.getClass() + ex.getLocalizedMessage(), ex);
            Alerts.loadTimeError(ex);
        }
    }

    @FXML
    private void handleSideMenu(ActionEvent actionEvent) {
        if (playerBase.getLeft() == null) {
            if (menuDrawer.isOpened()) {
                menuDrawer.close();
            } else {
                menuDrawer.open();
            }
        } else {
            if (sideMenu.getPrefWidth() == 320.0) {
                Timeline timeline = Animation.createTimelineAnimation(sideMenu, 320.0, 50.0);
                timeline.setOnFinished(event -> {
                    Utils.flipStackPane(searchStack);
                    separator.setVisible(false);
                    Utils.flipStackPane(showPlaylistStack);
                    Utils.replaceNode(showPlaylistStack.getParent(), addedPlaylists, btn_addPlaylist1);
//                    Utils.flipStackPane(playlistStack);
                });
                timeline.play();
            } else if (sideMenu.getPrefWidth() == 50.0) {
                Animation.createTimelineAnimation(sideMenu, 50.0, 320.0).play();
                Utils.flipStackPane(searchStack);
                separator.setVisible(true);
                Utils.flipStackPane(showPlaylistStack);
                Utils.replaceNode(showPlaylistStack.getParent(), btn_addPlaylist1, addedPlaylists);
//                Utils.flipStackPane(playlistStack);
            }
            sideMenu_collapsed_by_button.set(true);
        }
    }

    @FXML
    private void handleShowSearchBar() {
        if (playerBase.getLeft() == null) {
            Utils.flipStackPane((StackPane) topMenuAndSearchBox.getChildren().get(2));
        } else {
            handleSideMenu(null);
            searchField.requestFocus();
            btnShowSearchBar.setSelected(false);
        }
    }

    @FXML
    private void handleRecentlyPlayed(ActionEvent actionEvent) {
        Utils.swapTabStack(tabStack, tab_recentlyPlayed);
        if (!btnShowPlaylist.isSelected()) {
            btnRecentlyPlayed.setSelected(true);
        }
        if (menuDrawer.isOpened()) {
            menuDrawer.close();
        }
    }

    @FXML
    private void handleShowSongs(ActionEvent actionEvent) {
        Utils.swapTabStack(tabStack, tab_song);
        if (!btnSongs.isSelected()) {
            btnSongs.setSelected(true);
        }
        if (menuDrawer.isOpened()) {
            menuDrawer.close();
        }
    }

    @FXML
    private void handleShowAlbums(ActionEvent actionEvent) {
        Utils.swapTabStack(tabStack, tab_album);
        if (!btnAlbums.isSelected()) {
            btnAlbums.setSelected(true);
        }
        if (menuDrawer.isOpened()) {
            menuDrawer.close();
        }
    }

    @FXML
    private void handleShowArtists(ActionEvent actionEvent) {
        Utils.swapTabStack(tabStack, tab_artist);
        if (!btnArtists.isSelected()) {
            btnArtists.setSelected(true);
        }
        if (menuDrawer.isOpened()) {
            menuDrawer.close();
        }
    }

    @FXML
    private void handleShowBrowseOnline(ActionEvent actionEvent) {
        Utils.swapTabStack(tabStack, tab_browseOnline);
        if (!btnBrowseOnline.isSelected()) {
            btnBrowseOnline.setSelected(true);
        }
        if (menuDrawer.isOpened()) {
            menuDrawer.close();
        }
    }

    @FXML
    private void handleShowPlaylist(ActionEvent actionEvent) {
        Utils.swapTabStack(tabStack, tab_playlists);
        if (!btnShowPlaylist.isSelected()) {
            btnShowPlaylist.setSelected(true);
        }
        if (menuDrawer.isOpened()) {
            menuDrawer.close();
        }
    }

    @FXML
    private void handleAddPlaylist(ActionEvent actionEvent) {
        AddPlaylistDialogController.getInstance().load(null);
    }

    @FXML
    private void handleShowSettings(ActionEvent actionEvent) {
        Utils.swapTabStack(tabStack, tab_settings);
        if (!btnSettings.isSelected()) {
            btnSettings.setSelected(true);
        }
        if (menuDrawer.isOpened()) {
            menuDrawer.close();
        }
    }

    @FXML
    private void handleSearchField(ActionEvent actionEvent) {
        String query = searchField.getText();
        if (!query.isEmpty()) {
            SearchResultTabController.getInstance().setQueryString(query);
            SearchResultTabController.getInstance().setSongs(Inventory.getSongs(query));
            SearchResultTabController.getInstance().setAlbums(Inventory.getAlbums(query));
            SearchResultTabController.getInstance().setArtists(Inventory.getArtists(query));
            Utils.swapTabStack(tabStack, tab_searchResult);
        }
    }

    public void handleShowAlbumView() {
        Utils.swapTabStack(tabStack, tab_albumView);
    }

    public void handleShowArtistView() {
        Utils.swapTabStack(tabStack, tab_artistView);
    }

    public void handleShowPlaylistView() {
        Utils.swapTabStack(tabStack, tab_playlistView);
    }

    public void addPlaylist(Playlist playlist) {
        ToggleButton playListViewButton = new ToggleButton();
        playListViewButton.textProperty().bind(playlist.playlistNameProperty());
        playListViewButton.setAlignment(Pos.CENTER_LEFT);
        playListViewButton.setFont(Font.font("Roboto", 18));
        playListViewButton.setToggleGroup(menuTabGroup);
        playListViewButton.setGraphicTextGap(16);
        SVGPath icon = new SVGPath();
        icon.setContent("M15 6H3v2h12V6zm0 4H3v2h12v-2zM3 16h8v-2H3v2zM17 6v8.18c-.31-.11-.65-.18-1-.18-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3V8h3V6h-5z");
        playListViewButton.setGraphic(icon);
        playListViewButton.setPadding(new Insets(0, 0, 0, 10));

        playListViewButton.minWidth(50);
        playListViewButton.minHeightProperty().bind(playListViewButton.prefHeightProperty());

        playListViewButton.prefWidthProperty().bind(playListViewButton.widthProperty());
        playListViewButton.setPrefHeight(50);

        playListViewButton.setMaxWidth(Double.MAX_VALUE);
        playListViewButton.maxHeightProperty().bind(playListViewButton.prefHeightProperty());

        playListViewButton.setOnAction(event -> {
            PlaylistViewTabController.getInstance().loadPlaylist(playlist);
            PlaylistViewTabController.getInstance().setViewCalledByPlaylistTab(false);
            PlaylistViewTabController.getInstance().setViewCalledByRecentTab(false);
            Utils.swapTabStack(tabStack, tab_playlistView);
            if (!playListViewButton.isSelected()) {
                playListViewButton.setSelected(true);
            }
        });

        addedPlaylists.getChildren().add(playListViewButton);
    }

    public void removePlaylist(Playlist playlist) {
        addedPlaylists.getChildren().removeIf(node -> ((ToggleButton) node).getText().equalsIgnoreCase(playlist.getPlaylistName()));
    }
}
