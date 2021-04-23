package org.signature.dataModel.audioPlayer;

import javafx.beans.property.*;

public class Song {

    private static int sequence = 0;

    private final int id;
    private int musicLibraryId;
    private final StringProperty track;
    private final StringProperty title;
    private final StringProperty album;
    private final StringProperty artist;
    private final StringProperty albumArtist;
    private final StringProperty yearOfRelease;
    private final StringProperty genre;
    private final DoubleProperty length;
    private String location;
    private final LongProperty dateCreated; //creation time of file (in millis);
    private final BooleanProperty isPlaying;

    public Song() {
        this.id = sequence;
        this.track = new SimpleStringProperty("0");
        this.title = new SimpleStringProperty("Unknown Song " + id);
        this.album = new SimpleStringProperty("Unknown Album");
        this.artist = new SimpleStringProperty("Unknown Artist");
        this.albumArtist = new SimpleStringProperty("Unknown Artist");
        this.yearOfRelease = new SimpleStringProperty("");
        this.genre = new SimpleStringProperty("");
        this.length = new SimpleDoubleProperty(0.0);
        this.location = "";
        this.dateCreated = new SimpleLongProperty(0);
        this.musicLibraryId = 0;
        this.isPlaying = new SimpleBooleanProperty(false);
        ++sequence;
    }

    public int getId() {
        return id;
    }

    public String getTrack() {
        return track.get();
    }

    public StringProperty trackProperty() {
        return track;
    }

    public void setTrack(String track) {
        if (track == null) {
            this.track.set("0");
        } else if (track.isEmpty()) {
            this.track.set("0");
        } else {
            this.track.set(track);
        }
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null) {
            this.title.set("Unknown Song");
        } else if (title.isEmpty()) {
            this.title.set("Unknown Song");
        } else {
            this.title.set(title);
        }
    }

    public String getAlbum() {
        return album.get();
    }

    public StringProperty albumProperty() {
        return album;
    }

    public void setAlbum(String album) {
        if (album == null) {
            this.album.set("Unknown Album");
        } else if (album.isEmpty()) {
            this.album.set("Unknown Album");
        } else {
            this.album.set(album);
        }
    }

    public String getArtist() {
        return artist.get();
    }

    public StringProperty artistProperty() {
        return artist;
    }

    public void setArtist(String artist) {
        if (artist == null) {
            this.artist.set("Unknown Artist");
        } else if (artist.isEmpty()) {
            this.artist.set("Unknown Artist");
        } else {
            this.artist.set(artist);
        }
    }

    public String getAlbumArtist() {
        return albumArtist.get();
    }

    public StringProperty albumArtistProperty() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        if (albumArtist == null) {
            this.albumArtist.bind(this.artist);
        } else if (albumArtist.isEmpty()) {
            this.albumArtist.bind(this.artist);
        } else {
            this.albumArtist.unbind();
            this.albumArtist.set(albumArtist);
        }
    }

    public String getYearOfRelease() {
        return yearOfRelease.get();
    }

    public StringProperty yearOfReleaseProperty() {
        return yearOfRelease;
    }

    public void setYearOfRelease(String yearOfRelease) {
        this.yearOfRelease.set(yearOfRelease);
    }

    public String getGenre() {
        return genre.get();
    }

    public StringProperty genreProperty() {
        return genre;
    }

    public void setGenre(String genre) {
        if (genre == null) {
            this.genre.set("Unknown Genre");
        } else if (genre.isEmpty()) {
            this.genre.set("Unknown Genre");
        } else {
            this.genre.set(genre);
        }
    }

    public long getLength() {
        return (long) length.get();
    }

    public DoubleProperty lengthProperty() {
        return length;
    }

    public void setLength(double length) {
        this.length.set(length);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getDateCreated() {
        return dateCreated.get();
    }

    public LongProperty dateCreatedProperty() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated.set(dateCreated);
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
                ", track=" + track.get() +
                ", title=" + title.get() +
                ", album=" + album.get() +
                ", artist=" + artist.get() +
                ", yearOfRelease=" + yearOfRelease.get() +
                ", genre=" + genre.get() +
                ", length=" + length +
                ", location='" + location + '\'' +
                ", creationTime='" + dateCreated.get() + '\'' +
                '}';
    }
}
