package org.signature.util;

import javafx.application.Platform;

public abstract class Alerts {

    public static void loadTimeError(Exception exception) {
        exception.printStackTrace();
        Platform.exit();
    }
}
