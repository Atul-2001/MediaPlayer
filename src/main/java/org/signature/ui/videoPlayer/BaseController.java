package org.signature.ui.videoPlayer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import org.signature.App;

import java.net.URL;
import java.util.ResourceBundle;

public class BaseController implements Initializable {

    private static BaseController instance = null;

    @FXML
    private BorderPane playerBase;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        App.updateProgress(20.0);
    }

    public static BaseController getInstance() {
        return instance;
    }

    public BorderPane getRoot() {
        return playerBase;
    }
}
