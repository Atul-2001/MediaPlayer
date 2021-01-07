package org.signature.animations;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public abstract class Animation {
    public static Timeline createTimelineAnimation(Pane node, double initialValue, double finalValue) {
        KeyValue startKeyValue = new KeyValue(node.prefWidthProperty(), initialValue, Interpolator.EASE_OUT);
        KeyFrame startKeyFrame = new KeyFrame(Duration.ZERO, startKeyValue);

        KeyValue endKeyVale = new KeyValue(node.prefWidthProperty(), finalValue, Interpolator.EASE_IN);
        KeyFrame endKeyFrame = new KeyFrame(Duration.seconds(0.4), endKeyVale);

        return new Timeline(startKeyFrame, endKeyFrame);
    }

//    public static ParallelTransition createParallelTransition(Node node, Transition... transitions) {
//        return new ParallelTransition(node, transitions);
//    }
//
//    public static TranslateTransition createTranslateTransition(Duration duration) {
//        return new TranslateTransition(duration);
//    }
//
//    public static TranslateTransition createTranslateTransition(Node node, Duration duration) {
//        return new TranslateTransition(duration, node);
//    }
//
//    public static FadeTransition createFadeTransition(Duration duration) {
//        return new FadeTransition(duration);
//    }
//
//    public static FadeTransition createFadeTransition(Node node, Duration duration) {
//        return new FadeTransition(duration, node);
//    }
}
