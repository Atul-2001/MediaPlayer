package org.signature.ui.audioPlayer.tabs;

import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXToggleButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.ui.audioPlayer.dialogs.*;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsTabController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(SettingsTabController.class);

    private static SettingsTabController instance = null;

    @FXML
    private VBox tab_settings;
    @FXML
    private JFXToggleButton updateMediaInfo;
    @FXML
    private JFXToggleButton lockScreenArt;
    @FXML
    private JFXToggleButton wallpaperArt;
    @FXML
    private JFXRadioButton lightMode;
    @FXML
    private JFXRadioButton darkMode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        updateMediaInfo.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                updateMediaInfo.setText("On");
            } else {
                updateMediaInfo.setText("Off");
            }
        });

        lockScreenArt.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                lockScreenArt.setText("On");
            } else {
                lockScreenArt.setText("Off");
            }
        });

        wallpaperArt.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                wallpaperArt.setText("On");
            } else {
                wallpaperArt.setText("Off");
            }
        });

//        LOGGER.log(Level.INFO, "Settings Tab Loaded !!");
    }

    public static SettingsTabController getInstance() {
        return instance;
    }

    public VBox getRoot() {
        return tab_settings;
    }

    @FXML
    private void handleAddLocalMusic(MouseEvent mouseEvent) {
        AddMusicDialogController.getInstance().showDialog();
    }

    @FXML
    private void handleShowDownloads(ActionEvent actionEvent) {
        DownloadsController.getInstance().showDownloads();
    }

    @FXML
    private void handleOpenEqualizer(MouseEvent mouseEvent) {
        EqualizerController.getInstance().showDialog();
    }

    @FXML
    private void handleShowHelp(MouseEvent mouseEvent) {
        HelpController.getInstance().showDialog();
    }

    @FXML
    private void handleShowFeedbackForm(MouseEvent mouseEvent) {
        FeedbackController.getInstance().showDialog();
    }

    @FXML
    private void handleShowAbout(MouseEvent mouseEvent) {
        AboutController.getInstance().showDialog();
    }

}
