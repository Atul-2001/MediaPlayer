package org.signature.ui.audioPlayer.tabs;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXToggleButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.ui.audioPlayer.dialogs.AddMusicDialogController;

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

    private JFXDialog dialog = null;

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

        LOGGER.log(Level.INFO, "Settings Tab Loaded !!");
    }

    public static SettingsTabController getInstance() {
        return instance;
    }

    public VBox getRoot() {
        return tab_settings;
    }

    @FXML
    private void handleAddLocalMusic(MouseEvent mouseEvent) {
        if (dialog == null) {
            BorderPane node = AddMusicDialogController.getInstance().getRoot();
            dialog = new JFXDialog((StackPane) tab_settings.getParent(), node, JFXDialog.DialogTransition.CENTER);
            dialog.setMinSize(460, 260);
            dialog.prefHeightProperty().bind(node.heightProperty());
            ((JFXButton) ((HBox) node.getBottom()).getChildren().get(0)).setOnAction(event -> dialog.close());
        }
        dialog.show();
    }
}
