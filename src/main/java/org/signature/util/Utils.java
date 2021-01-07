package org.signature.util;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.signature.ui.audioPlayer.BaseController;

import java.util.concurrent.TimeUnit;

public abstract class Utils {

    private static final Logger LOGGER = LogManager.getLogger(BaseController.class);

    private static DropShadow dropShadow = null;
    private static final ParallelTransition swapTabTransition = new ParallelTransition();

    public static void swapTabStack(StackPane parent, Pane child) {
        assert parent != null;
        assert child != null;
        try {
            while (swapTabTransition.getStatus().equals(Animation.Status.RUNNING)) {
                //do nothing just wait!
            }

            int stackSize = parent.getChildren().size();
            int tabIndex = parent.getChildren().indexOf(child);
            if (tabIndex != stackSize - 1 && tabIndex > -1) {

                FadeTransition hideNodeTransition = new FadeTransition(Duration.seconds(0.2), parent.getChildren().get(stackSize - 1));
                hideNodeTransition.setInterpolator(Interpolator.EASE_IN);
                hideNodeTransition.setToValue(0.0);

                FadeTransition showNodeTransition = new FadeTransition(Duration.seconds(0.4), parent.getChildren().get(tabIndex));
                showNodeTransition.setInterpolator(Interpolator.EASE_OUT);
                showNodeTransition.setToValue(1.0);
                showNodeTransition.setOnFinished(event -> parent.getChildren().get(tabIndex).toFront());

                swapTabTransition.getChildren().setAll(hideNodeTransition, showNodeTransition);
                swapTabTransition.playFromStart();

                /*parent.getChildren().get(stackSize - 1).setVisible(false);
                parent.getChildren().get(tabIndex).setVisible(true);
                parent.getChildren().get(tabIndex).toFront();*/
            }
        } catch (NullPointerException | IndexOutOfBoundsException | ClassCastException e) {
            LOGGER.log(Level.WARN, e);
        }
    }

    public static void flipStackPane(StackPane stackPane) {
        assert stackPane != null;
        try {
            stackPane.getChildren().get(0).setVisible(true);
            stackPane.getChildren().get(1).setVisible(false);
            stackPane.getChildren().get(1).toBack();
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            LOGGER.log(Level.WARN, e);
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
