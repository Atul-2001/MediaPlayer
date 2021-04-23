package org.signature.ui.audioPlayer;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.signature.App;
import org.signature.util.Utils;

import java.net.URL;
import java.util.ResourceBundle;

public class MiniPlayerViewController implements Initializable {

    private static MiniPlayerViewController instance = null;

    private final String APP_ICON = "Media-player-icon-circle.png";
    private final String PAUSE_ICON = "M12 38h8V10h-8v28zm16-28v28h8V10h-8z";
    private final String PLAY_ICON = "M16 10v28l22-14z";

    @FXML
    private StackPane root;
    @FXML
    private HBox topBar;
    @FXML
    private Rectangle albumArtView;
    @FXML
    private StackPane contentStack;
    @FXML
    private JFXButton skipPrevious, playPause, skipNext;
    @FXML
    private Label songName, artistName;
    @FXML
    private JFXProgressBar songSeek;

    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location != null && instance == null) {
            instance = this;
        }

        albumArtView.heightProperty().bind(root.heightProperty());
        albumArtView.widthProperty().bind(root.widthProperty());

        ConsoleController.getInstance().albumArtProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.toString().contains("javafx.scene.image.Image")) {
                albumArtView.setFill(Color.BLACK);
                albumArtView.setEffect(null);
            } else {
                albumArtView.setFill(new ImagePattern(newValue));
                albumArtView.setEffect(new GaussianBlur(30.0));
            }
        });

        skipPrevious.onActionProperty().bind(ConsoleController.getInstance().getSkipPrevious().onActionProperty());

        playPause.onActionProperty().bind(ConsoleController.getInstance().getPlayPause().onActionProperty());
        ConsoleController.getInstance().playingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                ((SVGPath) playPause.getGraphic()).setContent(PAUSE_ICON);
                playPause.getTooltip().setText("Pause");
            } else {
                ((SVGPath) playPause.getGraphic()).setContent(PLAY_ICON);
                playPause.getTooltip().setText("Play");
            }
        });

        skipNext.onActionProperty().bind(ConsoleController.getInstance().getSkipNext().onActionProperty());

        songName.textProperty().bind(ConsoleController.getInstance().songTitleProperty());
        artistName.textProperty().bind(ConsoleController.getInstance().songArtistProperty());

        ConsoleController.getInstance().currentTimeProperty().addListener((observable, oldValue, newValue) -> songSeek.setProgress(newValue.doubleValue()/ConsoleController.getInstance().getSongLength()));

        ConsoleController.getInstance().activeProperty().addListener((observable, oldValue, newValue) -> Utils.flipStackPane(contentStack));

        stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(new Scene(root));
        stage.getIcons().add(new Image(App.class.getResourceAsStream(APP_ICON)));
        stage.setMinWidth(168.0);
        stage.setMinHeight(160.0);
        stage.setWidth(300.0);
        stage.setHeight(300.0);
        stage.setMaxWidth(500.0);
        stage.setMaxHeight(386.0);
        stage.setAlwaysOnTop(true);
        stage.setX(App.getScreenWidth() - 320.0);
        stage.setY(20.0);

        topBar.setOnMousePressed(pressEvent -> topBar.setOnMouseDragged(dragEvent -> {
            stage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
            stage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
        }));
    }

    public static MiniPlayerViewController getInstance() {
        return instance;
    }

    public StackPane getRoot() {
        return root;
    }

    public void showStage() {
        this.stage.show();
        App.getStage().close();
    }

    @FXML
    private void handleShuffleAll(ActionEvent actionEvent) {
        ConsoleController.getInstance().handleShuffleAll();
    }

    @FXML
    private void handleLeaveMiniView(ActionEvent actionEvent) {
        this.stage.close();
        App.getStage().show();
    }

    @FXML
    private void handleCloseOperation(ActionEvent actionEvent) {
        this.stage.close();
        App.getStage().close();
    }
}
