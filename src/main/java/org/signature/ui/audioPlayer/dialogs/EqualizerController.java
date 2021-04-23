package org.signature.ui.audioPlayer.dialogs;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.media.EqualizerBand;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.ui.MainWindowController;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class EqualizerController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(EqualizerController.class);

    private static EqualizerController instance = null;

    @FXML
    private JFXDialogLayout root;
    @FXML
    private JFXComboBox<String> EQMode;
    @FXML
    private Slider low;
    @FXML
    private Slider midLow;
    @FXML
    private Slider mid;
    @FXML
    private Slider midHigh;
    @FXML
    private Slider high;

    private final List<EqualizerBand> bands = new ArrayList<>();
    private JFXDialog dialog = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        low.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(Boolean.TRUE)) {
                if (!EQMode.getSelectionModel().getSelectedItem().equals("Custom")) {
                    EQMode.getSelectionModel().select("Custom");
                }
            }
        });

        midLow.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(Boolean.TRUE)) {
                if (!EQMode.getSelectionModel().getSelectedItem().equals("Custom")) {
                    EQMode.getSelectionModel().select("Custom");
                }
            }
        });

        mid.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(Boolean.TRUE)) {
                if (!EQMode.getSelectionModel().getSelectedItem().equals("Custom")) {
                    EQMode.getSelectionModel().select("Custom");
                }
            }
        });

        midHigh.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(Boolean.TRUE)) {
                if (!EQMode.getSelectionModel().getSelectedItem().equals("Custom")) {
                    EQMode.getSelectionModel().select("Custom");
                }
            }
        });

        high.valueChangingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(Boolean.TRUE)) {
                if (!EQMode.getSelectionModel().getSelectedItem().equals("Custom")) {
                    EQMode.getSelectionModel().select("Custom");
                }
            }
        });

        dialog = new JFXDialog(MainWindowController.getInstance().getRoot(), root, JFXDialog.DialogTransition.CENTER);
        dialog.setOverlayClose(false);
    }

    public static EqualizerController getInstance() {
        return instance;
    }

    public JFXDialogLayout getRoot() {
        return root;
    }

    @FXML
    private void handleCloseDialog(ActionEvent actionEvent) {
        this.dialog.close();
    }

    @FXML
    private void handleSetEQMode(ActionEvent actionEvent) {
        bands.clear();
        switch (EQMode.getSelectionModel().getSelectedItem()) {
            case "Flat":
                low.setValue(0.0);
                midLow.setValue(0.0);
                mid.setValue(0.0);
                midHigh.setValue(0.0);
                high.setValue(0.0);
                bands.addAll(getBands());
                EQMode.getTooltip().setText("Flat");
                break;
            case "Treble boost":
                low.setValue(0.0);
                midLow.setValue(0.0);
                mid.setValue(0.0);
                midHigh.setValue(3.0);
                high.setValue(6.0);
                bands.addAll(getBands());
                EQMode.getTooltip().setText("Treble boost");
                break;
            case "Bass boost":
                low.setValue(6.0);
                midLow.setValue(3.0);
                mid.setValue(0.0);
                midHigh.setValue(0.0);
                high.setValue(0.0);
                bands.addAll(getBands());
                EQMode.getTooltip().setText("Bass boost");
                break;
            case "Headphones":
                low.setValue(6.0);
                midLow.setValue(2.5);
                mid.setValue(0.0);
                midHigh.setValue(2.0);
                high.setValue(2.2);
                bands.addAll(getBands());
                EQMode.getTooltip().setText("Headphones");
                break;
            case "Laptop":
                low.setValue(6.0);
                midLow.setValue(5.6);
                mid.setValue(2.0);
                midHigh.setValue(4.0);
                high.setValue(5.0);
                bands.addAll(getBands());
                EQMode.getTooltip().setText("Laptop");
                break;
            case "Portable speakers":
                low.setValue(7.0);
                midLow.setValue(6.0);
                mid.setValue(5.0);
                midHigh.setValue(4.5);
                high.setValue(4.0);
                bands.addAll(getBands());
                EQMode.getTooltip().setText("Portable speakers");
                break;
            case "Home stereo":
                low.setValue(6.0);
                midLow.setValue(5.0);
                mid.setValue(2.0);
                midHigh.setValue(3.0);
                high.setValue(4.0);
                bands.addAll(getBands());
                EQMode.getTooltip().setText("Home stereo");
                break;
            case "TV":
                low.setValue(3.0);
                midLow.setValue(10.0);
                mid.setValue(0.0);
                midHigh.setValue(5.0);
                high.setValue(6.0);
                bands.addAll(getBands());
                EQMode.getTooltip().setText("TV");
                break;
            case "Car":
                low.setValue(7.5);
                midLow.setValue(3.5);
                mid.setValue(1.0);
                midHigh.setValue(3.0);
                high.setValue(5.0);
                bands.addAll(getBands());
                EQMode.getTooltip().setText("Car");
                break;
            case "Custom":
                bands.addAll(getBands());
                EQMode.getTooltip().setText("Custom");
                break;
        }
    }

    private List<EqualizerBand> getBands() {
        return Arrays.asList(new EqualizerBand(32, 19, low.getValue()),
                new EqualizerBand(32, 19, midLow.getValue()),
                new EqualizerBand(32, 19, mid.getValue()),
                new EqualizerBand(32, 19, midHigh.getValue()),
                new EqualizerBand(32, 19, high.getValue()),

                new EqualizerBand(64, 39, low.getValue()),
                new EqualizerBand(64, 39, midLow.getValue()),
                new EqualizerBand(64, 39, mid.getValue()),
                new EqualizerBand(64, 39, midHigh.getValue()),
                new EqualizerBand(64, 39, high.getValue()),

                new EqualizerBand(125, 78, low.getValue()),
                new EqualizerBand(125, 78, midLow.getValue()),
                new EqualizerBand(125, 78, mid.getValue()),
                new EqualizerBand(125, 78, midHigh.getValue()),
                new EqualizerBand(125, 78, high.getValue()),

                new EqualizerBand(250, 156, low.getValue()),
                new EqualizerBand(250, 156, midLow.getValue()),
                new EqualizerBand(250, 156, mid.getValue()),
                new EqualizerBand(250, 156, midHigh.getValue()),
                new EqualizerBand(250, 156, high.getValue()),

                new EqualizerBand(500, 312, low.getValue()),
                new EqualizerBand(500, 312, midLow.getValue()),
                new EqualizerBand(500, 312, mid.getValue()),
                new EqualizerBand(500, 312, midHigh.getValue()),
                new EqualizerBand(500, 312, high.getValue()),

                new EqualizerBand(1000, 625, low.getValue()),
                new EqualizerBand(1000, 625, midLow.getValue()),
                new EqualizerBand(1000, 625, mid.getValue()),
                new EqualizerBand(1000, 625, midHigh.getValue()),
                new EqualizerBand(1000, 625, high.getValue()),

                new EqualizerBand(2000, 1250, low.getValue()),
                new EqualizerBand(2000, 1250, midLow.getValue()),
                new EqualizerBand(2000, 1250, mid.getValue()),
                new EqualizerBand(2000, 1250, midHigh.getValue()),
                new EqualizerBand(2000, 1250, high.getValue()),

                new EqualizerBand(4000, 2500, low.getValue()),
                new EqualizerBand(4000, 2500, midLow.getValue()),
                new EqualizerBand(4000, 2500, mid.getValue()),
                new EqualizerBand(4000, 2500, midHigh.getValue()),
                new EqualizerBand(4000, 2500, high.getValue()),

                new EqualizerBand(8000, 5000, low.getValue()),
                new EqualizerBand(8000, 5000, midLow.getValue()),
                new EqualizerBand(8000, 5000, mid.getValue()),
                new EqualizerBand(8000, 5000, midHigh.getValue()),
                new EqualizerBand(8000, 5000, high.getValue()),

                new EqualizerBand(16000, 10000, low.getValue()),
                new EqualizerBand(16000, 10000, midLow.getValue()),
                new EqualizerBand(16000, 10000, mid.getValue()),
                new EqualizerBand(16000, 10000, midHigh.getValue()),
                new EqualizerBand(16000, 10000, high.getValue()));
    }

    public void showDialog() {
        this.dialog.show();
    }

    public MediaPlayer setAudioEqualizer(MediaPlayer mediaPlayer) {
        try {
            mediaPlayer.getAudioEqualizer().setEnabled(true);
//            mediaPlayer.getAudioEqualizer().getBands().setAll(bands);
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.WARN, "Failed to set equalizer! ", ex);
            mediaPlayer.getAudioEqualizer().setEnabled(false);
        }

        return mediaPlayer;
    }
}
