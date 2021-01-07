package org.signature.ui;

import com.jfoenix.controls.JFXTabPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.ui.audioPlayer.BaseController;
import org.signature.util.Alerts;

import java.io.IOException;

public class MainWindowController {

    private final Logger LOGGER = LogManager.getLogger(MainWindowController.class);

    @FXML
    private VBox root;
    @FXML
    private JFXTabPane tabStack;

    public void initialize() {
        try {
            tabStack.tabMinWidthProperty().bind(root.widthProperty().divide(2));
            tabStack.getTabs().get(0).setContent(FXMLLoader.load(BaseController.class.getResource("AudioPlayerBase.fxml")));
        } catch (NullPointerException | IOException e) {
            LOGGER.log(Level.ERROR, "Failed to load Media Player " + e.getLocalizedMessage(), e);
            Alerts.loadTimeError(e);
        }
    }
}
