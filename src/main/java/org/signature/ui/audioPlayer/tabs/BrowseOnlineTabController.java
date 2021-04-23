package org.signature.ui.audioPlayer.tabs;

import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.CacheHint;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.signature.dataModel.audioPlayer.*;
import org.signature.ui.MainWindowController;
import org.signature.ui.audioPlayer.AudioPlayer;
import org.signature.ui.audioPlayer.BaseController;
import org.signature.ui.audioPlayer.Inventory;
import org.signature.ui.audioPlayer.dialogs.AddToListDialogController;
import org.signature.ui.audioPlayer.model.OnlineSongPane;
import org.signature.util.Alerts;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class BrowseOnlineTabController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(BrowseOnlineTabController.class);

    private static BrowseOnlineTabController instance = null;

    private final String YOUTUBE_API_KEY = "AIzaSyCGksHrhcZKOYHRSEyGu9hne3P6p-DGnYU";
    private final String YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch?v=";
    private final String DEFAULT_MUSIC_FOLDER = System.getProperty("user.home").concat(File.separator).concat("Music");

    @FXML
    private VBox tab_browse_song;
    @FXML
    private JFXProgressBar searchProgressBar;
    @FXML
    private TextField query_field;
    @FXML
    private JFXButton searchButton;
    @FXML
    private ListView<HBox> searchResultList;

    JFXAutoCompletePopup<String> youtubeAutoComplete = new JFXAutoCompletePopup<>();

    private final ObservableList<OnlineSong> ONLINE_SONG_SEARCH_RESULT = FXCollections.observableArrayList();
    private boolean isListUpdated = false;

    private boolean isYouTubeSearching = false;
    private int youtubePageNo = 1;
    private String youtubeQueryURLStr;
    private String youtubeNextPageToken;
    private String oldSearchQuery = "";

    private static final ExecutorService browseOnlineServicePool = Executors.newCachedThreadPool(new ThreadFactory() {
        final AtomicInteger counter = new AtomicInteger();

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "Browse online service pool--Thread-" + counter.incrementAndGet());
        }
    });

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        youtubeAutoComplete.setSelectionHandler(event -> {
            query_field.setText(event.getObject());
            searchYouTube();
        });

        query_field.textProperty().addListener((observable, oldValue, newValue) -> {

            if (isYouTubeSearching) return;

            browseOnlineServicePool.submit(new Task<Void>() {
                @Override
                protected Void call() {
                    try {
                        Platform.runLater(() -> {
                            youtubeAutoComplete.getSuggestions().clear();
                            youtubeAutoComplete.getSuggestions().add(newValue);
                        });

                        String[] suggestions = getYoutubeSuggestions(newValue);
                        if (suggestions == null) {
                            Platform.runLater(() -> youtubeAutoComplete.hide());
                        } else if (newValue.equals(query_field.getText())) {
                            if (!isYouTubeSearching) {
                                if (!youtubeAutoComplete.isShowing())
                                    Platform.runLater(() -> youtubeAutoComplete.show(query_field));
                                Platform.runLater(() -> youtubeAutoComplete.getSuggestions().setAll(suggestions));
                            }
                        }
                    } catch (Exception e) {
                        Platform.runLater(() -> youtubeAutoComplete.hide());
                        LOGGER.log(Level.ERROR, e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                    return null;
                }
            });
        });

    }

    public static BrowseOnlineTabController getInstance() {
        return instance;
    }

    public VBox getRoot() {
        return this.tab_browse_song;
    }

    public boolean isListUpdated() {
        return this.isListUpdated;
    }

    public void setListUpdated(boolean value) {
        this.isListUpdated = value;
    }

    public ObservableList<OnlineSong> getOnlineSongSearchResultList() {
        return this.ONLINE_SONG_SEARCH_RESULT;
    }

    public static ExecutorService getBrowseOnlineServicePool() {
        return browseOnlineServicePool;
    }

    private String[] getYoutubeSuggestions(String searchTerm) throws Exception {
        if (searchTerm.equals("")) return null;

        String query = "http://suggestqueries.google.com/complete/search?client=firefox&ds=yt&q=" + URLEncoder.encode(searchTerm, StandardCharsets.UTF_8);

        InputStream s = new URL(query).openStream();

        StringBuilder response = new StringBuilder();

        while (true) {
            int cInt = s.read();
            if (cInt == -1) break;

            char c = (char) cInt;
            response.append(c);
        }

        String responseStr = response.toString();

        JSONArray largeJSON = new JSONArray(responseStr);
        JSONArray queriesArray = largeJSON.getJSONArray(1);

        String[] toReturn = new String[queriesArray.length() + 1];
        toReturn[0] = searchTerm;
        for (int i = 0; i < queriesArray.length(); i++) {
            toReturn[i + 1] = queriesArray.getString(i);
        }

        return toReturn;
    }

    public void handleSearchSong(ActionEvent actionEvent) {
        searchYouTube();
    }

    private void searchYouTube() {
        browseOnlineServicePool.submit(new Task<Void>() {
            @Override
            protected Void call() {
                ExecutorService mediaPool = AudioPlayer.getInstance().getMediaPool();

                isYouTubeSearching = true;
                Platform.runLater(() -> {
                    searchProgressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
                    searchButton.setDisable(true);
                    query_field.setDisable(true);
                    youtubeAutoComplete.hide();
                });

                try {
                    String query = query_field.getText();
                    if (query == null || query.isEmpty()) {
                        query = "bollywood songs";
                    }
                    String youtubeSearchQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

                    if (!oldSearchQuery.equals(youtubeSearchQuery)) {
                        oldSearchQuery = youtubeSearchQuery;
                        youtubePageNo = 1;
                        ONLINE_SONG_SEARCH_RESULT.clear();
                    }

                    if (youtubePageNo == 1) {
                        youtubeQueryURLStr = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + youtubeSearchQuery + "&maxResults=10&key=" + YOUTUBE_API_KEY;
                    } else {
                        youtubeQueryURLStr = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + youtubeSearchQuery + "&maxResults=10&pageToken=" + youtubeNextPageToken + "&key=" + YOUTUBE_API_KEY;
                    }

                    JSONObject youtubeResponse = new JSONObject(returnHTTPRawResponse(youtubeQueryURLStr));

                    if (youtubeResponse.has("nextPageToken"))
                        youtubeNextPageToken = youtubeResponse.getString("nextPageToken");
                    else
                        youtubeNextPageToken = "";

                    JSONArray resultsArray = youtubeResponse.getJSONArray("items");

                    if (youtubePageNo == 1) {
                        Platform.runLater(() -> {
                            searchResultList.getItems().clear();
                            searchResultList.setVisible(false);
                        });
                    } else {
                        Platform.runLater(() -> searchResultList.getItems().remove(searchResultList.getItems().size() - 1));
                    }

                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject eachItem = resultsArray.getJSONObject(i);

                        JSONObject idObj = eachItem.getJSONObject("id");
                        String kindObj = idObj.getString("kind");

                        if (kindObj.equals("youtube#video")) {
                            JSONObject snippet = eachItem.getJSONObject("snippet");

                            String title = snippet.getString("title");
                            String channelTitle = snippet.getString("channelTitle");

                            JSONObject thumbnail = snippet.getJSONObject("thumbnails");

                            String thumbnailURL = thumbnail.getJSONObject("high").getString("url");

                            String videoId = idObj.getString("videoId");

                            String videoURL = YOUTUBE_VIDEO_URL.concat(videoId);

                            OnlineSong onlineSong = null;
                            OnlineSongPane onlineSongPane = null;

                            if (isSongPresent(title, channelTitle)) {
                                onlineSong = new OnlineSong(false, title, channelTitle, 0, localSongLocation, thumbnailURL);
                                onlineSong.setLocalSourceAvailable(true);

                                OnlineSong finalOnlineSong = onlineSong;
                                mediaPool.submit(() -> {
                                    Media media = new Media(localSongLocation);
                                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                                    mediaPlayer.setOnReady(() -> {
                                        finalOnlineSong.setLength(media.getDuration().toMillis());
                                        mediaPlayer.dispose();
                                    });
                                });

                                onlineSongPane = new OnlineSongPane(finalOnlineSong);
                                onlineSongPane.setDownloadDisable(true);
                            } else {
                                onlineSong = new OnlineSong(false, title, channelTitle, 0, videoURL, thumbnailURL);

                                OnlineSong finalOnlineSong = onlineSong;
                                mediaPool.submit(() -> {
                                    Media media = new Media(videoURL);
                                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                                    mediaPlayer.setOnReady(() -> {
                                        finalOnlineSong.setLength(media.getDuration().toMillis());
                                        mediaPlayer.dispose();
                                    });
                                });

                                onlineSongPane = new OnlineSongPane(finalOnlineSong);
                            }
                            ONLINE_SONG_SEARCH_RESULT.add(onlineSong);

                            OnlineSongPane finalOnlineSongPane = onlineSongPane;
                            Platform.runLater(() -> searchResultList.getItems().add(finalOnlineSongPane));

                        } else if (kindObj.equals("youtube#playlist")) {
                            JSONObject snippet = eachItem.getJSONObject("snippet");
                            if (snippet.has("thumbnails")) {
                                String title = snippet.getString("title");
                                String channelTitle = snippet.getString("channelTitle");

                                String playlistID = idObj.getString("playlistId");

                                /*
                                For some reason the YouTube API (tested 16/01/2020)
                                doesn't return thumnails of unavailable videos, so its better to omit them...
                                 */

                                JSONObject thumbnail = snippet.getJSONObject("thumbnails");
                                String thumbnailURL = thumbnail.getJSONObject("high").getString("url");

                                OnlineSong onlineSong = new OnlineSong(true, "Playlist : " + title, channelTitle, 0.0, playlistID, thumbnailURL);
                                OnlineSongPane onlineSongPane = new OnlineSongPane(onlineSong);

                                Platform.runLater(() -> searchResultList.getItems().add(onlineSongPane));
                            }
                        }
                    }

                    if (searchResultList.getItems().size() > 0 && !youtubeNextPageToken.equals("")) {
                        JFXButton loadMoreButton = new JFXButton("Load More");
                        loadMoreButton.setFont(Font.font("Roboto", 14));
                        loadMoreButton.setCache(true);
                        loadMoreButton.setCacheHint(CacheHint.SPEED);
                        loadMoreButton.setTextFill(Color.rgb(72, 133, 237));
                        Platform.runLater(() -> searchResultList.getItems().add(new HBox(loadMoreButton)));
                        loadMoreButton.setOnMouseClicked(event -> {
                            youtubePageNo++;
                            searchYouTube();
                        });
                    }

                    Platform.runLater(() -> searchResultList.setVisible(true));
                } catch (Exception e) {
                    if (e.getLocalizedMessage().contains("403 for URL")) {
                        Alerts.showErrorAlert("Error 403", "It seems Media Player was unable to connect to YouTube with your given API Key.\n\nThis was probably caused due to exceeded limit!\n\n\nGo to the following link for more info : \n" + e.getLocalizedMessage().replace("Server returned HTTP response code: 403 for URL: ", ""));
                    } else {
                        Alerts.showErrorAlert("Uh Oh!", "Unable to connect to YouTube. Make sure you're connected to the internet. For more info, check stacktrace");
                    }
                    LOGGER.log(Level.ERROR, "", e);
                } finally {
                    Platform.runLater(() -> {
                        searchResultList.setDisable(false);
                        searchButton.setDisable(false);
                        query_field.setDisable(false);
                        youtubeAutoComplete.hide();
                    });
                }

                isYouTubeSearching = false;
                isListUpdated = true;
                Platform.runLater(() -> searchProgressBar.setProgress(0.0));
                return null;
            }
        });
    }

    public String returnHTTPRawResponse(String urlPassed) throws Exception {
        StringBuilder tbr = new StringBuilder();

        try {
            InputStreamReader r = new InputStreamReader(new URL(urlPassed).openStream());

            while (true) {
                int c = r.read();
                if (c == -1) break;
                else {
                    tbr.append((char) c);
                }
            }

            r.close();
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, e.getLocalizedMessage());
            System.out.println(e.getLocalizedMessage());
        }

        return new String(tbr);
    }

    private String localSongLocation = null;

    public boolean isSongPresent(String title, String channel) {
        List<Artist> artists = Inventory.getArtists(channel);
        if (artists != null) {
            for (Artist artist : artists) {
                if (artist.getName().equalsIgnoreCase(channel)) {
                    List<Album> albums = Inventory.getAlbums(artist);
                    for (Album album : albums) {
                        List<Song> songs = Inventory.getSongs(album);
                        for (Song song : songs) {
                            if (song.getLocation().equals(DEFAULT_MUSIC_FOLDER.concat(File.separator).concat(title)) && song.getTitle().equalsIgnoreCase(title)) {
                                localSongLocation = song.getLocation();
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public ArrayList<OnlineSong> getSongsInYouTubePublicPlaylist(String playlistID) throws Exception {
        ArrayList<OnlineSong> playlistOnlineSongs = new ArrayList<>();

        String youtubeURL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&playlistId=" + playlistID + "&key=" + YOUTUBE_API_KEY;
        String nextPageToken = "";
        while (!nextPageToken.equals("done")) {
            if (!nextPageToken.equals(""))
                youtubeURL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&pageToken=" + nextPageToken + "&playlistId=" + playlistID + "&key=" + YOUTUBE_API_KEY;

            InputStreamReader bf = new InputStreamReader(new URL(youtubeURL).openStream());

            StringBuilder response = new StringBuilder();
            while (true) {
                int c = bf.read();
                if (c == -1) break;
                response.append((char) c);
            }

            bf.close();

            JSONObject responseJSON = new JSONObject(response.toString());

            if (responseJSON.has("nextPageToken"))
                nextPageToken = responseJSON.getString("nextPageToken");
            else
                nextPageToken = "done";

            JSONArray items = responseJSON.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject eachSong = items.getJSONObject(i);
                JSONObject snippet = eachSong.getJSONObject("snippet");
                JSONObject idObj = snippet.getJSONObject("resourceId");
                if (eachSong.getString("kind").equals("youtube#playlistItem")) {
                    String title = snippet.getString("title");
                    String channelTitle = snippet.getString("channelTitle");

                    if (!snippet.has("thumbnails")) continue;

                    JSONObject thumbnail = snippet.getJSONObject("thumbnails");

                    String defaultThumbnailURL = thumbnail.getJSONObject("high").getString("url");

                    String videoId = idObj.getString("videoId");

                    String videoURL = YOUTUBE_VIDEO_URL.concat(videoId);

                    OnlineSong onlineSong = new OnlineSong();
                    onlineSong.setPlaylist(false);
                    onlineSong.setURL(videoURL);
                    onlineSong.setThumbnailURL(defaultThumbnailURL);
                    onlineSong.setTitle(title);
                    onlineSong.setChannelName(channelTitle);

                    AudioPlayer.getInstance().getMediaPool().submit(() -> {
                        Media media = new Media(videoURL);
                        MediaPlayer mediaPlayer = new MediaPlayer(media);
                        mediaPlayer.setOnReady(() -> {
                            onlineSong.setLength(media.getDuration().toMillis());
                            mediaPlayer.dispose();
                        });
                    });

                    playlistOnlineSongs.add(onlineSong);
                }
            }
        }

        return playlistOnlineSongs;
    }

    public void saveYouTubePlaylistSongsToLocalPlaylist(String playlistID, Playlist playlist) {
        if (playlistID == null || playlistID.isEmpty()) {

            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            dialogLayout.getStylesheets().add(BaseController.class.getResource("audio-player-style.css").toString());
            dialogLayout.getStyleClass().add("import-song-dialog");

            Label headingLabel = new Label("Import Music from YouTube Playlist");
            headingLabel.setFont(Font.font("Roboto", 26.0));
            dialogLayout.setHeading(headingLabel);

            Label msgLabel = new Label("Enter YouTube Playlist ID below (Must be PUBLIC)");
            msgLabel.setFont(Font.font("Roboto", 16.0));
            msgLabel.setWrapText(true);

            TextField playlistIdField = new TextField();
            playlistIdField.setPromptText("Playlist ID");
            playlistIdField.setFont(Font.font("Roboto", 16.0));
            playlistIdField.getStyleClass().add("dialog-text-field");

            VBox root = new VBox(msgLabel, playlistIdField);

            dialogLayout.setBody(root);

            JFXSpinner progressSpinner = new JFXSpinner();
            progressSpinner.setProgress(-1);
            progressSpinner.setCache(true);
            progressSpinner.setCacheHint(CacheHint.SPEED);
            progressSpinner.setPrefSize(24, 24);
            progressSpinner.setVisible(false);

            JFXButton btnImport = new JFXButton("IMPORT");
            btnImport.setFont(Font.font("Roboto", 16.0));
            btnImport.setPrefSize(140.0, 30.0);
            btnImport.getStyleClass().add("dialog-button");

            JFXButton btnCancel = new JFXButton("CANCEL");
            btnCancel.setFont(Font.font("Roboto", 16.0));
            btnCancel.setPrefSize(140.0, 30.0);
            btnCancel.getStyleClass().add("dialog-button");

            dialogLayout.setActions(progressSpinner, btnImport, btnCancel);

            JFXDialog popupDialog = new JFXDialog(MainWindowController.getInstance().getRoot(), dialogLayout, JFXDialog.DialogTransition.CENTER);
            popupDialog.setOverlayClose(false);
            btnCancel.setOnMouseClicked(event -> popupDialog.close());

            btnImport.setOnAction(event -> {
                if (playlistIdField.getText().length() == 0) {
                    Alerts.showErrorAlert("Uh Oh!", "Please enter a valid YouTube Playlist ID!");
                } else {
                    browseOnlineServicePool.submit(new Task<Void>() {
                        @Override
                        protected Void call() {
                            try {
                                Platform.runLater(() -> {
                                    playlistIdField.setDisable(true);
                                    btnImport.setDisable(true);
                                    btnCancel.setDisable(true);

                                    progressSpinner.setVisible(true);
                                });

                                List<OnlineSong> onlineSongList = FXCollections.observableArrayList();
                                for (OnlineSong onlineSong : getSongsInYouTubePublicPlaylist(playlistIdField.getText())) {
                                    if (!isSongPresent(onlineSong.getTitle(), onlineSong.getChannelName())) {
                                        onlineSongList.add(onlineSong);
                                    }
                                }

                                if (playlist != null) {
                                    List<PlaylistSong> playlistSongs = new ArrayList<>();
                                    for (OnlineSong onlineSong : onlineSongList) {
                                        Image img = new Image(onlineSong.getThumbnailURL());
                                        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(img, null);
                                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);

                                        playlistSongs.add(new PlaylistSong(byteArrayOutputStream.toByteArray(), onlineSong.getTitle(),
                                                                        onlineSong.getChannelName(), "Youtube", null,
                                                                        "youtube.com", onlineSong.getLength(), onlineSong.getURL()));
                                    }
                                    playlist.addSongs(playlistSongs);
                                } else {
                                    Platform.runLater(() -> AddToListDialogController.getInstance().load(null, onlineSongList.toArray(OnlineSong[]::new)));
                                }
                            } catch (Exception ex) {
                                Platform.runLater(() -> Alerts.showErrorAlert("Error!", "Unable to import!\n" +
                                        "Reasons:-\n" +
                                        "1) It may be due to no internet connection!\n" +
                                        "2) OR it may be due to invalid Playlist Id!\n" +
                                        "Check Log\n"));
                                LOGGER.log(Level.TRACE, "", ex);
                            } finally {
                                Platform.runLater(() -> {
                                    progressSpinner.setVisible(false);
                                    popupDialog.close();
                                });
                            }
                            return null;
                        }
                    });
                }
            });

            Platform.runLater(popupDialog::show);
        } else {
            browseOnlineServicePool.submit(new Task<Void>() {
                @Override
                protected Void call() {
                    try {
                        List<OnlineSong> onlineSongList = FXCollections.observableArrayList();
                        for (OnlineSong onlineSong : getSongsInYouTubePublicPlaylist(playlistID)) {
                            if (!isSongPresent(onlineSong.getTitle(), onlineSong.getChannelName())) {
                                onlineSongList.add(onlineSong);
                            }
                        }

                        if (playlist != null) {
                            List<PlaylistSong> playlistSongs = new ArrayList<>();
                            for (OnlineSong onlineSong : onlineSongList) {
                                Image img = new Image(onlineSong.getThumbnailURL());
                                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(img, null);
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                ImageIO.write(bufferedImage, "png", byteArrayOutputStream);

                                playlistSongs.add(new PlaylistSong(byteArrayOutputStream.toByteArray(), onlineSong.getTitle(),
                                        onlineSong.getChannelName(), "Youtube", null,
                                        "youtube.com", onlineSong.getLength(), onlineSong.getURL()));
                            }
                            playlist.addSongs(playlistSongs);
                        } else {
                            Platform.runLater(() -> AddToListDialogController.getInstance().load(null, onlineSongList.toArray(OnlineSong[]::new)));
                        }
                    } catch (Exception ex) {
                        Platform.runLater(() -> Alerts.showErrorAlert("Error!", "Unable to import!\n" +
                                "Reasons:-\n" +
                                "1) It may be due to no internet connection!\n" +
                                "2) OR it may be due to invalid Playlist Id!\n" +
                                "Check Log"));
                        LOGGER.log(Level.TRACE, "", ex);
                    }
                    return null;
                }
            });
        }
    }
}