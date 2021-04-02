package org.signature.dataModel.audioPlayer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Song {

    private static int sequence = 0;

    private final int id;
    private int musicLibraryId;
    private String track;
    private String title;
    private String album;
    private String artist;
    private String yearOfRelease;
    private int genre;
    private double length;
    private String location;
    private String creationTime; //creation time of file (in millis);
    private final BooleanProperty isPlaying;

    public Song() {
        this.id = sequence;
        this.track = "0";
        this.title = "Unknown Song " + id;
        this.album = "Unknown Album";
        this.artist = "Unknown Artist";
        this.yearOfRelease = "";
        this.genre = Integer.MIN_VALUE;
        this.length = 0.0;
        this.location = "";
        this.musicLibraryId = 0;
        this.isPlaying = new SimpleBooleanProperty(false);
        ++sequence;
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

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
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

    public int getMusicLibraryId() {
        return musicLibraryId;
    }

    public void setMusicLibraryId(int musicLibraryId) {
        this.musicLibraryId = musicLibraryId;
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
