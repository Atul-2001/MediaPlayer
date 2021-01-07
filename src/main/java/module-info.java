module org.signature {
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.swing;
    requires com.jfoenix;
    requires org.apache.logging.log4j;
    requires java.sql;
    requires com.h2database;
    requires java.persistence;
    requires java.naming;
    requires org.hibernate.orm.core;
    requires org.hibernate.commons.annotations;
    requires mp3agic;

    opens org.signature;
    opens org.signature.util;
    opens org.signature.ui;
    opens org.signature.ui.audioPlayer;
    opens org.signature.ui.audioPlayer.dialogs;
    opens org.signature.ui.audioPlayer.model;
    opens org.signature.ui.audioPlayer.tabs;
    opens org.signature.dataModel.audioPlayer;

}