package org.signature.ui.audioPlayer.dialogs;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.App;
import org.signature.dataModel.audioPlayer.MusicLibrary;
import org.signature.ui.MainWindowController;
import org.signature.ui.audioPlayer.AudioPlayer;
import org.signature.ui.audioPlayer.Inventory;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddMusicDialogController implements Initializable {

    private final Logger LOGGER = LogManager.getLogger(AddMusicDialogController.class);

    private static AddMusicDialogController instance = null;

    @FXML
    private BorderPane root;
    @FXML
    private VBox libraryList;
    @FXML
    private JFXButton btn_AddLibrary;

    private JFXDialog dialog = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        root.prefHeightProperty().bind(libraryList.heightProperty().add(2).add(80).add(60));

        root.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() > App.getScreenHeight() * 0.70) {
                root.prefHeightProperty().unbind();
            }
        });

        ((VBox) root.getTop()).getChildren().get(1).setVisible(libraryList.getChildren().size() != 1);

        libraryList.heightProperty().addListener((observable, oldValue, newValue) -> ((VBox) root.getTop()).getChildren().get(1).setVisible(libraryList.getChildren().size() != 1));

        dialog = new JFXDialog(MainWindowController.getInstance().getRoot(), root, JFXDialog.DialogTransition.CENTER);
        dialog.setOverlayClose(false);
        dialog.setMinSize(460, 260);
        dialog.prefHeightProperty().bind(root.heightProperty());

//        LOGGER.log(Level.INFO, "Add Music Dialog Loaded !!");
    }

    public static AddMusicDialogController getInstance() {
        return instance;
    }

    public BorderPane getRoot() {
        return root;
    }

    private void loadExistingLibraries() {
        libraryList.getChildren().clear();
        libraryList.getChildren().add(btn_AddLibrary);
        for (MusicLibrary library : Inventory.getLibraries()) {
            libraryList.getChildren().add(createNode(library.getFolderName(), library.getFolderLocation()));
        }
    }

    @FXML
    private void handleAddLocalMusicLibrary(ActionEvent actionEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Open folder");
        chooser.setInitialDirectory(new File(System.getProperty("user.home"), "Music"));
        File result = chooser.showDialog(root.getScene().getWindow());
        if (result != null) {
            if (Inventory.addLibrary(new MusicLibrary(result.getName(), result.getAbsolutePath()))) {
                libraryList.getChildren().add(createNode(result.getName(), result.getAbsolutePath()));
                AudioPlayer.getInstance().musicLoader(Inventory.getCachedAlbums().get(0), Inventory.getCachedArtists().get(0));
            }
        }
    }

    @FXML
    private void handleCloseDialog(ActionEvent actionEvent) {
        this.dialog.close();
    }

    private AnchorPane createNode(String name, String location) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setMinSize(286.0, 68.0);
        anchorPane.setPrefSize(286.0, 68.0);
        anchorPane.setMaxSize(286.0, 68.0);

        Label folderName = new Label(name);
        folderName.setMinHeight(24);
        folderName.setPrefHeight(24);
        folderName.setMaxHeight(24);
        folderName.setFont(Font.font("Roboto Medium", FontWeight.NORMAL, FontPosture.REGULAR, 18));
        AnchorPane.setTopAnchor(folderName, 12.0);
        AnchorPane.setLeftAnchor(folderName, 10.0);

        Label folderLocation = new Label(location);
        folderLocation.setMinHeight(20);
        folderLocation.setPrefHeight(20);
        folderLocation.setMaxHeight(20);
        folderLocation.setFont(Font.font("Roboto", FontWeight.NORMAL, FontPosture.REGULAR, 16));
        AnchorPane.setTopAnchor(folderLocation, 34.0);
        AnchorPane.setLeftAnchor(folderLocation, 10.0);

        Pane btn_close = new Pane();
        btn_close.setStyle("-fx-background-color: TRANSPARENT");
        SVGPath btn_close_img = new SVGPath();
        btn_close_img.setContent("M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z");
        btn_close.getChildren().add(btn_close_img);
        btn_close.setOnMouseClicked(event -> {

            Alert deleteAlert = new Alert(Alert.AlertType.NONE);
            deleteAlert.initOwner(anchorPane.getScene().getWindow());
            deleteAlert.setTitle("Remove this folder?");
            deleteAlert.setHeaderText("Remove this folder?");
            deleteAlert.setContentText("If you remove the \"" + name + "\" folder from Music,\n" +
                    "it won't appear in Music anymore,\n" +
                    "but won't be deleted.");
            ButtonType btn_remove = new ButtonType("Remove Folder");
            deleteAlert.getButtonTypes().addAll(btn_remove, ButtonType.CANCEL);
            Optional<ButtonType> result = deleteAlert.showAndWait();

            if (result.isPresent() && result.get().equals(btn_remove)) {
                int index = libraryList.getChildren().indexOf(btn_close.getParent());
                libraryList.getChildren().remove(index);
                Inventory.removeLibrary(index - 1);
            }
        });
        AnchorPane.setTopAnchor(btn_close, 10.0);
        AnchorPane.setRightAnchor(btn_close, 10.0);

        anchorPane.getChildren().addAll(folderName, folderLocation, btn_close);
        anchorPane.getStyleClass().add("dialog-button");

        return anchorPane;
    }

    public void showDialog() {
        loadExistingLibraries();
        this.dialog.show();
    }

}
