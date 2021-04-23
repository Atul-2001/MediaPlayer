package org.signature;

import com.jfoenix.controls.JFXProgressBar;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeScreenController implements Initializable {

    private static WelcomeScreenController instance = null;

    @FXML
    private JFXProgressBar appLoadProgress;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }
    }

    public static WelcomeScreenController getInstance() {
        return instance;
    }

    public void updateProgress(double progress) {
        appLoadProgress.setProgress(progress);
    }

}
