package org.signature.ui.audioPlayer.tabs;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.App;
import org.signature.ui.audioPlayer.dialogs.AddMusicDialogController;
import org.signature.ui.audioPlayer.dialogs.DownloadsController;

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
    private Stage stage = null;
    private Parent root = null;

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

        Platform.runLater(() -> {
            try {
                if (root == null || stage == null) {
                    this.root = DownloadsController.getInstance().getRoot();

                    this.stage = new Stage();
                    this.stage.setScene(new Scene(this.root));
                    this.stage.setTitle("Downloads");

                    Screen screen = Screen.getPrimary();
                    Rectangle2D bounds = screen.getVisualBounds();
                    this.stage.setX(bounds.getMinX());
                    this.stage.setY(bounds.getMinY());
                    this.stage.setMinWidth(bounds.getWidth() * 0.75);
                    this.stage.setMinHeight(bounds.getHeight() * 0.75);
                    this.stage.setWidth(bounds.getWidth() * 0.75);
                    this.stage.setHeight(bounds.getHeight() * 0.75);

                    App.getStage().setOnCloseRequest(event -> {
                        if (this.stage.isShowing()) {
                            this.stage.close();
                        }
                    });
                }
            } catch (NullPointerException e) {
                LOGGER.log(Level.ERROR, "Failed to load Download window!, " + e.getLocalizedMessage());
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

    @FXML
    private void handleShowDownloads(ActionEvent actionEvent) {
        this.stage.show();
        this.stage.toFront();
    }

    public boolean isStageShowing() {
        return this.stage.isShowing();
    }

    public void showDownloads() {
        if (this.stage.isShowing()) {
            this.stage.toFront();
        } else {
            handleShowDownloads(null);
        }
    }
}
