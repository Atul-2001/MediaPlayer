package org.signature.ui.audioPlayer.model;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import org.signature.dataModel.audioPlayer.Album;
import org.signature.dataModel.audioPlayer.Artist;
import org.signature.dataModel.audioPlayer.Song;
import org.signature.ui.audioPlayer.ConsoleController;

public class AddToContextMenu extends ContextMenu {

    private static AddToContextMenu instance = null;

    private final String ADD_TO_PLAYLIST_ICON = "M10 20h4V4h-4v16zm-6 0h4v-8H4v8zM16 9v11h4V9h-4z";
    private final String NEW_PLAYLIST_ICON = "M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z";
    private final String PLAYLIST_ICON = "M15 6H3v2h12V6zm0 4H3v2h12v-2zM3 16h8v-2H3v2zM17 6v8.18c-.31-.11-.65-.18-1-.18-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3V8h3V6h-5z";

    private final JFXButton addToNowPlaying;
    private final JFXButton newPlaylist;
    private JFXDialog dialog;

    private static Song song = null;
    private static Album album = null;
    private static Artist artist = null;

    private AddToContextMenu() {

        setStyle("-fx-background-color: linear-gradient(to top, #FFDEE9 0%, #B5FFFC 100%);");

        addToNowPlaying = getMenuButton("Now playing", ADD_TO_PLAYLIST_ICON);
        newPlaylist = getMenuButton("New playlist", NEW_PLAYLIST_ICON);

        getItems().setAll(new CustomMenuItem(addToNowPlaying),
                new SeparatorMenuItem(),
                new CustomMenuItem(newPlaylist));

        init();
    }

    private void init() {
        addToNowPlaying.setOnAction(event -> {
            if (song != null) {
                ConsoleController.getInstance().addToNowPlaying(song);
            } else if (album != null) {
                ConsoleController.getInstance().addToNowPlaying(album);
            } else if (artist != null) {
                ConsoleController.getInstance().addToNowPlaying(artist);
            }
        });

        newPlaylist.setOnAction(event -> {

        });
    }

    public static AddToContextMenu getInstance(Object item) {
        assert item instanceof Song || item instanceof Album || item instanceof Artist;

        if (item instanceof Song) {
            song = (Song) item;
            album = null;
            artist = null;
        } else if (item instanceof Album) {
            song = null;
            album = (Album) item;
            artist = null;
        } else if (item instanceof Artist) {
            song = null;
            album = null;
            artist = (Artist) item;
        } else {
            // nothing
        }

        if (instance == null) {
            instance = new AddToContextMenu();
        }
        return instance;
    }

    private JFXButton getMenuButton(String text, String graphics) {
        JFXButton button = new JFXButton(text);
        button.setFont(Font.font("Roboto", 14.0));

        SVGPath button_graphics = new SVGPath();
        button_graphics.setContent(graphics);
        button.setGraphic(button_graphics);

        button.setGraphicTextGap(12.0);
        button.setAlignment(Pos.CENTER);

        return button;
    }
}
