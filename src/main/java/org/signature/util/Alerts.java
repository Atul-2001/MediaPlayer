package org.signature.util;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import org.signature.ui.MainWindowController;
import org.signature.ui.audioPlayer.dialogs.PlayingListDialogController;

public abstract class Alerts {

    public static void loadTimeError(Exception exception) {
        exception.printStackTrace();
        Platform.exit();
    }

    private static JFXDialog createAlertDialog(String heading, String content, Alert.AlertType alertType) {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.getStylesheets().add(PlayingListDialogController.class.getResource("dialog-style.css").toString());
        if (alertType.equals(Alert.AlertType.ERROR)) {
            dialogLayout.getStyleClass().add("error_dialog_box");
        } else if (alertType.equals(Alert.AlertType.INFORMATION)) {
            dialogLayout.getStyleClass().add("dialog_box");
        }
        Label headingLabel = new Label(heading);
        headingLabel.setFont(Font.font("Roboto", 28));
        dialogLayout.setHeading(headingLabel);
        Label contentLabel = new Label(content);
        contentLabel.setFont(Font.font("Roboto", 18));
        contentLabel.setWrapText(true);
        dialogLayout.setBody(contentLabel);
        JFXButton okButton = new JFXButton("OK");
        okButton.getStyleClass().add("dialog-button");
        okButton.setFont(Font.font("Roboto", 16));
        dialogLayout.setActions(okButton);

        JFXDialog alertDialog = new JFXDialog(MainWindowController.getInstance().getRoot(), dialogLayout, JFXDialog.DialogTransition.CENTER);
        alertDialog.setOverlayClose(false);
        okButton.setOnMouseClicked(event -> alertDialog.close());

        return alertDialog;
    }

    public static void showErrorAlert(String heading, String content) {
        JFXDialog errorAlert = createAlertDialog(heading, content, Alert.AlertType.ERROR);
        Platform.runLater(errorAlert::show);
    }

    public static void showInformationAlert(String heading, String content) {
        JFXDialog informationAlert = createAlertDialog(heading, content, Alert.AlertType.INFORMATION);
        Platform.runLater(informationAlert::show);
    }
}
