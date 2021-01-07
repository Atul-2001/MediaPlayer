package org.signature.dataModel.audioPlayer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javax.persistence.*;

@Entity
@Table(name = "Favourite_Song")
public class Song {

    @Transient
    private static int sequence = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "fav_song_generator")
    @SequenceGenerator(name = "fav_song_generator", sequenceName = "fav_song_seq", allocationSize = 1)
    private long S_NO;
    @Transient
    private final int id;
    private String track;
    private String title;
    private String yearOfRelease;
    @Transient
    private int genre;
    private double length;
    @Transient
    private int album;
    private String location;
    @Transient
    private String creationTime; //creation time of file (in millis);
    @Transient
    private final BooleanProperty favourite, isPlaying;

    public Song() {
        this.id = sequence;
        this.track = "0";
        this.title = "Unknown Song " + id;
        this.yearOfRelease = "";
        this.genre = Integer.MIN_VALUE;
        this.length = 0.0;
        this.album = Integer.MIN_VALUE;
        this.location = "";
        favourite = new SimpleBooleanProperty(false);
        isPlaying = new SimpleBooleanProperty(false);
        ++sequence;
    }

    public long getS_NO() {
        return S_NO;
    }

    public int getId() {
        return id;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYearOfRelease() {
        return yearOfRelease;
    }

    public void setYearOfRelease(String yearOfRelease) {
        this.yearOfRelease = yearOfRelease;
    }

    public int getGenre() {
        return genre;
    }

    public void setGenre(int genre) {
        this.genre = genre;
    }

    public long getLength() {
        return (long) length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public int getAlbum() {
        return album;
    }

    public void setAlbum(int album) {
        this.album = album;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public void setFavourite(boolean isFavourite) {
        favourite.set(isFavourite);
    }

    public BooleanProperty getFavourite() {
        return favourite;
    }

    public BooleanProperty isPlayingProperty() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying.set(isPlaying);
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", track='" + track + '\'' +
                ", title='" + title + '\'' +
                ", yearOfRelease='" + yearOfRelease + '\'' +
                ", genre=" + genre +
                ", length=" + length +
                ", album=" + album +
                ", location='" + location + '\'' +
                ", creationTime='" + creationTime + '\'' +
                '}';
    }
}
