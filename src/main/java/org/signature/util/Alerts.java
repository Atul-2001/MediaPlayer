package org.signature.util;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import org.signature.ui.audioPlayer.BaseController;
import org.signature.ui.audioPlayer.dialogs.PlayingListDialogController;

public abstract class Alerts {

    public static void loadTimeError(Exception exception) {
        exception.printStackTrace();
        Platform.exit();
    }

    public static void showErrorAlert(String heading, String content)
    {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.getStylesheets().add(PlayingListDialogController.class.getResource("dialog-style.css").toString());
        dialogLayout.getStyleClass().add("dialog_style");
        Label headingLabel = new Label(heading);
        headingLabel.setFont(Font.font("Roboto", 28));
        dialogLayout.setHeading(headingLabel);
        Label contentLabel = new Label(content);
        contentLabel.setFont(Font.font("Roboto", 18));
        contentLabel.setWrapText(true);
        dialogLayout.setBody(contentLabel);
        JFXButton okButton = new JFXButton("OK");
        okButton.setFont(Font.font("Roboto", 16));
        dialogLayout.setActions(okButton);

        StackPane baseStackPane = (StackPane) BaseController.getInstance().getRoot().getCenter();
        JFXDialog alertDialog = new JFXDialog(baseStackPane,dialogLayout, JFXDialog.DialogTransition.CENTER);
        alertDialog.setOverlayClose(false);
        alertDialog.getStylesheets().add(PlayingListDialogController.class.getResource("dialog-style.css").toString());
        alertDialog.getStyleClass().add("dialog_box");
        okButton.setOnMouseClicked(event -> alertDialog.close());

        Platform.runLater(alertDialog::show);
    }
}
