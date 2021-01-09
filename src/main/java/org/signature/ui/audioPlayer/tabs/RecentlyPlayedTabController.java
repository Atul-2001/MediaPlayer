package org.signature.ui.audioPlayer.tabs;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.dataModel.audioPlayer.Album;
import org.signature.dataModel.audioPlayer.Artist;
import org.signature.ui.audioPlayer.BaseController;
import org.signature.ui.audioPlayer.ConsoleController;
import org.signature.ui.audioPlayer.Inventory;
import org.signature.ui.audioPlayer.model.RecentPane;
import org.signature.util.Utils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RecentlyPlayedTabController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(RecentlyPlayedTabController.class);

    private static RecentlyPlayedTabController instance = null;

    @FXML
    private VBox tab_recentlyPlayed;
    @FXML
    private StackPane contentStack;
    @FXML
    private FlowPane list_played_albums;

    private final ObservableList<Object> recentlyPlayed = FXCollections.observableArrayList();
    private boolean isCachedListLoaded = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        recentlyPlayed.addListener((ListChangeListener<Object>) c -> {
            c.next();
            if (c.wasAdded()) {
                for (Object object : c.getAddedSubList()) {
                    list_played_albums.getChildren().add(new RecentPane(object));
                    if (isCachedListLoaded) {
                        Inventory.addRecentlyPlayed(object);
                    }
                }
            }

            if (list_played_albums.getChildren().size() == 0 && !contentStack.getChildren().get(1).toString().contains("VBox")) {
                Utils.flipStackPane(contentStack);
            } else if (list_played_albums.getChildren().size() > 0 && contentStack.getChildren().get(1).toString().contains("VBox")) {
                Utils.flipStackPane(contentStack);
            }
        });

        if (!isCachedListLoaded) {
            setRecentlyPlayed(Inventory.getCachedRecentlyPlayed());
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
            isCachedListLoaded = true;
        }

        LOGGER.log(Level.INFO, "'Recently Played' Tab Loaded !!");
    }

    public static RecentlyPlayedTabController getInstance() {
        return instance;
    }

    public VBox getRoot() {
        return tab_recentlyPlayed;
    }

    @FXML
    private void handleShuffleMusic(ActionEvent actionEvent) {
        ConsoleController.getInstance().handleShuffleRecentlyPlays();
    }

    @FXML
    private void handleShowMyMusic(MouseEvent mouseEvent) {
        BaseController.getInstance().getBtnSongs().fire();
    }

    public void setRecentlyPlayed(List<Object> recentPlays) {
        list_played_albums.getChildren().clear();
        recentlyPlayed.setAll(recentPlays);
    }

    public void addRecentlyPlayed(Object recentPlayed) {
        assert recentPlayed instanceof Album || recentPlayed instanceof Artist;
        if (!recentlyPlayed.contains(recentPlayed)) {
            recentlyPlayed.add(recentPlayed);
        }
    }
}
