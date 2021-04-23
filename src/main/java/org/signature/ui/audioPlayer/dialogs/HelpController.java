package org.signature.ui.audioPlayer.dialogs;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.signature.ui.MainWindowController;

import java.net.URL;
import java.util.ResourceBundle;

public class HelpController implements Initializable {

    private static HelpController instance = null;

    @FXML
    private JFXDialogLayout root;

    private JFXDialog dialog = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        dialog = new JFXDialog(MainWindowController.getInstance().getRoot(), root, JFXDialog.DialogTransition.CENTER);
        dialog.setOverlayClose(false);
    }

    public static HelpController getInstance() {
        return instance;
    }

    public JFXDialogLayout getRoot() {
        return root;
    }

    @FXML
    private void handleCloseDialog(ActionEvent actionEvent) {
        this.dialog.close();
    }

    public void showDialog() {
        this.dialog.show();
    }
}
