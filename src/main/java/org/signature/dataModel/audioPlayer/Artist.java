package org.signature.dataModel.audioPlayer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javax.persistence.*;

@Entity
@Table(name = "Favourite_Artist")
public class Artist {

    @Transient
    private static int sequence = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "fav_artist_generator")
    @SequenceGenerator(name = "fav_artist_generator", sequenceName = "fav_artist_seq", allocationSize = 1)
    private long S_NO;
    @Transient
    private final int id;
    private String name;
    @Transient
    private final BooleanProperty favourite;

    public Artist() {
        this.id = sequence;
        this.name = "Unknown Artist " + id;
        this.favourite = new SimpleBooleanProperty(false);
        ++sequence;
    }

    public Artist(String name) {
        this.id = sequence;
        ++sequence;
        this.name = name;
        this.favourite = new SimpleBooleanProperty(false);
    }

    public long getS_NO() {
        return S_NO;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BooleanProperty getFavourite() {
        return favourite;
    }

    public void setFavourite(boolean isFavourite) {
        favourite.set(isFavourite);
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}