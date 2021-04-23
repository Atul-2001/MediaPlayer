package org.signature.util;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.ui.audioPlayer.BaseController;

import java.util.concurrent.TimeUnit;

public abstract class Utils {

    private static final Logger LOGGER = LogManager.getLogger(BaseController.class);

    private static DropShadow dropShadow = null;

    public static void swapTabStack(StackPane parent, Node child) {
        assert parent != null;
        assert child != null;
        try {
            int stackSize = parent.getChildren().size();
            int tabIndex = parent.getChildren().indexOf(child);
            if (tabIndex != stackSize - 1 && tabIndex > -1) {
                parent.getChildren().get(stackSize - 1).setVisible(false);
                parent.getChildren().get(tabIndex).setVisible(true);
                parent.getChildren().get(tabIndex).toFront();
            }
        } catch (NullPointerException | IndexOutOfBoundsException | ClassCastException ex) {
            LOGGER.log(Level.WARN, "", ex);
        }
    }

    public static void flipStackPane(StackPane stackPane) {
        assert stackPane != null;
        try {
            stackPane.getChildren().get(0).setVisible(true);
            stackPane.getChildren().get(1).setVisible(false);
            stackPane.getChildren().get(1).toBack();
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            LOGGER.log(Level.WARN, "", ex);
        }
    }

    public static Effect createNodeHoverEffect() {
        if (dropShadow == null) {
            dropShadow = new DropShadow();
            dropShadow.setBlurType(BlurType.THREE_PASS_BOX);
            dropShadow.setWidth(50.0);
            dropShadow.setHeight(50.0);
            dropShadow.setOffsetY(6.0);
            dropShadow.setColor(Color.BLACK);
        }
        return dropShadow;
    }

    public static void replaceNode(Parent parent, Node nodeToRemove, Node nodeToAdd) {
        ((Pane) parent).getChildren().removeAll(nodeToRemove);
        ((Pane) parent).getChildren().addAll(nodeToAdd);
    }

    public static String getDuration(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));

        if (hours <= 0) {
            return String.format("%02d:%02d", minutes,seconds);
        } else {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
    }
}
