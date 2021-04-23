package org.signature.dataModel.audioPlayer;


import javafx.beans.property.*;

import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;

public class Album {

    private static int sequence = 0;

    private final int id;
    private final StringProperty albumName;
    private final StringProperty albumArtist;
    private final StringProperty artist;
    private byte[] albumImage;
    private StringProperty albumImageMimeType;
    private final StringProperty genre;
    private final StringProperty releaseYear;
    private LongProperty dateCreated; // creation time of song file (in millis)

    public Album() {
        this.id = sequence;
        this.albumName = new SimpleStringProperty( "Unknown Album " + id);
        this.albumArtist = new SimpleStringProperty("Unknown Artist");
        this.artist = new SimpleStringProperty("Unknown Artist");
        this.albumImage = new byte[0];
        this.albumImageMimeType = new SimpleStringProperty();
        this.genre = new SimpleStringProperty();
        this.releaseYear = new SimpleStringProperty();
        this.dateCreated = new SimpleLongProperty(0);
        ++sequence;
    }

    public Album(String albumName, String artist, String releaseYear, long dateCreated) {
        this.id = sequence;
        ++sequence;

        if (albumName == null) {
            this.albumName = new SimpleStringProperty("Unknown Album ");
        } else if (albumName.isEmpty()) {
            this.albumName = new SimpleStringProperty("Unknown Album ");
        } else {
            this.albumName = new SimpleStringProperty(albumName);
        }

        this.albumArtist = new SimpleStringProperty("Unknown Artist");

        if (artist == null) {
            this.artist = new SimpleStringProperty("Unknown Artist");
        } else if (artist.isEmpty()) {
            this.artist = new SimpleStringProperty("Unknown Artist");
        } else {
            this.artist = new SimpleStringProperty(artist);
        }

        this.albumImageMimeType = new SimpleStringProperty();
        this.genre = new SimpleStringProperty();

        if (releaseYear == null) {
            this.releaseYear = new SimpleStringProperty(String.valueOf(LocalDateTime.now().getYear()));
        } else if (releaseYear.isEmpty()) {
            this.releaseYear = new SimpleStringProperty(String.valueOf(LocalDateTime.now().getYear()));
        } else {
            this.releaseYear = new SimpleStringProperty(releaseYear);
        }

        this.dateCreated = new SimpleLongProperty(dateCreated);
    }

    public int getId() {
        return id;
    }

    public String getAlbumName() {
        return albumName.get();
    }

    public StringProperty albumNameProperty() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        if (albumName == null) {
            this.albumName.set("Unknown Album");
        } else if (albumName.isEmpty()) {
            this.albumName.set("Unknown Album");
        } else {
            this.albumName.set(albumName);
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

    public byte[] getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(byte[] albumImage) {
        this.albumImage = albumImage;
    }

    public String getAlbumImageMimeType() {
        return albumImageMimeType.get();
    }

    public StringProperty albumImageMimeTypeProperty() {
        return albumImageMimeType;
    }

    public void setAlbumImageMimeType(String albumImageMimeType) {
        this.albumImageMimeType.set(albumImageMimeType);
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

    public String getReleaseYear() {
        return releaseYear.get();
    }

    public StringProperty releaseYearProperty() {
        return releaseYear;
    }

    public void setReleaseYear(String releaseYear) {
        if (releaseYear == null) {
            this.releaseYear.set(String.valueOf(LocalDateTime.now().getYear()));
        } else if (releaseYear.isEmpty()) {
            this.releaseYear.set(String.valueOf(LocalDateTime.now().getYear()));
        } else {
            this.releaseYear.set(releaseYear);
        }
    }

    public long getDateCreated() {
        return dateCreated.get();
    }

    public LongProperty dateCreatedProperty() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        if (FileTime.fromMillis(dateCreated).compareTo(FileTime.fromMillis(this.dateCreated.get())) < 0) {
            this.dateCreated.set(dateCreated);
        } else if (this.dateCreated.get() == 0) {
            this.dateCreated.set(dateCreated);
        }
    }

    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", albumName='" + albumName.get() + '\'' +
                ", albumArtist='" + albumArtist.get() + '\'' +
                ", albumImageMimeType='" + albumImageMimeType + '\'' +
                ", genre=" + genre.get() +
                ", artist=" + artist.get() +
                ", releaseYear=" + releaseYear.get() +
                ", creationTime=" + dateCreated.get() +
                '}';
    }
}
