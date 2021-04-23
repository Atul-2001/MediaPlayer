package org.signature.ui.audioPlayer.dialogs;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.controlsfx.control.Rating;
import org.signature.ui.MainWindowController;
import org.signature.util.Alerts;
import org.signature.util.MailUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class FeedbackController implements Initializable {

    private static FeedbackController instance = null;

    @FXML
    private JFXDialogLayout root;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private Rating rating;
    @FXML
    private TextArea reviewField;
    @FXML
    private JFXButton submit;

    private JFXDialog dialog = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        submit.disableProperty().bind(nameField.textProperty().isEmpty().or(emailField.textProperty().isEmpty()));

        dialog = new JFXDialog(MainWindowController.getInstance().getRoot(), root, JFXDialog.DialogTransition.CENTER);
        dialog.setOverlayClose(false);
    }

    public static FeedbackController getInstance() {
        return instance;
    }

    public JFXDialogLayout getRoot() {
        return root;
    }

    @FXML
    void handleSubmit(ActionEvent event) {
        String user = nameField.getText();
        StringBuilder messageContent = new StringBuilder();
        messageContent.append("<html><body>");
        messageContent.append("<h1>User Detail</h1>");
        messageContent.append("<p>Name : ").append(user).append("<br>");
        messageContent.append("Email : ").append(emailField.getText()).append("</p><br>");
        messageContent.append("<center><h1><font size = '48'>").append(user).append(" has given ").append(rating.getRating()).append(" star to MP34 (Audio Player)</font></h1></center>");
        messageContent.append("<h2>Review : </h2>");
        messageContent.append("<h3>").append(reviewField.getText()).append("</h3>");
        messageContent.append("</body></html>");

        if (MailUtil.sendMail(user, messageContent.toString())) {
            Alerts.showInformationAlert("Successful", "Thank you for you review!");
            handleCloseDialog(null);
        } else {
            Alerts.showErrorAlert("Failed", "Failed to submit review!\nPlease try again!");
        }
    }

    @FXML
    public void handleCloseDialog(ActionEvent actionEvent) {
        this.dialog.close();
    }

    public void showDialog() {
        nameField.clear();
        emailField.clear();
        rating.setRating(0.0);
        reviewField.clear();
        this.dialog.show();
    }
}
