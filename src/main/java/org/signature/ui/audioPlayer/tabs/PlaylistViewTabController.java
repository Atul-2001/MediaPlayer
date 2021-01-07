package org.signature.ui.audioPlayer.tabs;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.ui.audioPlayer.BaseController;
import org.signature.util.Utils;

import java.net.URL;
import java.util.ResourceBundle;

public class PlaylistViewTabController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(PlaylistViewTabController.class);

    @FXML private VBox playlist_view;
    @FXML private HBox playlistViewHeader;
    @FXML private AnchorPane playlistArtPane;
    @FXML private Pane playlistArtColor2;
    @FXML private Pane playlistArtColor1;
    @FXML private ImageView playlistArt;
    @FXML private VBox info_and_controls_pane;
    @FXML private TextField playlistName;
    @FXML private HBox playlistSizeInfoPane;
    @FXML private Label noOfSongs;
    @FXML private Label totalHours;
    @FXML private Label totalMinutes;
    @FXML private StackPane contentStack;
    @FXML private ScrollPane playlistViewContentPane;
    @FXML private VBox playlistSongsList;

    StringProperty playlistNameProperty = new SimpleStringProperty("");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        playlistViewHeader.prefHeightProperty().bind(playlistViewContentPane.vvalueProperty().multiply(playlistViewHeader.heightProperty()));

        playlistArtPane.prefHeightProperty().bind(playlistViewHeader.heightProperty().subtract(60));
        playlistArtPane.prefWidthProperty().bind(playlistArtPane.heightProperty());

        playlistArt.fitHeightProperty().bind(playlistArtPane.heightProperty().subtract(14));
        playlistArt.fitWidthProperty().bind(playlistArt.fitHeightProperty());

        playlistArtColor1.prefHeightProperty().bind(playlistArtPane.heightProperty().subtract(34));
        playlistArtColor1.prefWidthProperty().bind(playlistArtColor1.heightProperty());

        playlistArtColor2.prefHeightProperty().bind(playlistArtPane.heightProperty().subtract(54));
        playlistArtColor2.prefWidthProperty().bind(playlistArtColor2.heightProperty());

        playlistViewHeader.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() <= 98.0 && playlistSizeInfoPane.getParent() != null) {
                info_and_controls_pane.getChildren().removeAll(playlistSizeInfoPane);
            } else if (newValue.doubleValue() > 98.0 && playlistSizeInfoPane.getParent() == null) {
                info_and_controls_pane.getChildren().add(1, playlistSizeInfoPane);
            }
        });

        playlistSongsList.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (playlistSongsList.getChildren().size() != 0 && !playlistSongsList.isVisible()) {
                Utils.flipStackPane(contentStack);
            } else if (playlistSongsList.getChildren().size() == 0 && playlistSongsList.isVisible()) {
                Utils.flipStackPane(contentStack);
            }
        });
    }

    public void createPlaylist(String name) {
        playlistName.setText(name);
        noOfSongs.setText("0");
        totalHours.setText("0");
        totalMinutes.setText("0");
        playlistNameProperty.set(name);
    }

    @FXML
    private void handleSaveNewName(ActionEvent actionEvent) {
        playlistName.setEditable(false);
        playlistNameProperty.set(playlistName.getText());
    }

    @FXML
    private void handlePlayAllSongs(ActionEvent actionEvent) {
    }

    @FXML
    private void handleAddTo(ActionEvent actionEvent) {
    }

    @FXML
    private void handleRenamePlaylist(ActionEvent actionEvent) {
        playlistName.setEditable(true);
        playlistName.requestFocus();
    }

    @FXML
    private void handleDeletePlaylist(ActionEvent actionEvent) {
        ((StackPane) BaseController.getInstance().getRoot().getCenter()).getChildren().removeAll(playlist_view);
    }

    @FXML
    private void handleShowMyMusic(MouseEvent mouseEvent) {
        BaseController.getInstance().getBtnAlbums().fire();
    }

    public StringProperty playlistNamePropertyProperty() {
        return playlistNameProperty;
    }
}
