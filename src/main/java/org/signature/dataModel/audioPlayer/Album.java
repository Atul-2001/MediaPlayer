package org.signature.dataModel.audioPlayer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javax.persistence.*;

@Entity
@Table(name = "Favourite_Album")
public class Album {

    @Transient
    private static int sequence = 0;


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fav_album_generator")
    @SequenceGenerator(name = "fav_album_generator", sequenceName = "fav_album_seq", allocationSize = 1)
    private long S_NO;
    @Transient
    private final int id;
    private String albumName;
    private String albumArtist;
    @Transient
    private byte[] albumImage;
    @Transient
    private String albumImageMimeType;
    @Transient
    private int genre;
    @Transient
    private int artist;
    private String releaseYear;
    @Transient
    private String creationTime; // creation time of song file (in millis)
    @Transient
    private final BooleanProperty favourite;

    public Album() {
        this.id = sequence;
        this.albumName = "Unknown Album " + id;
        this.albumArtist = "Unknown Artist";
        this.albumImage = new byte[0];
        this.albumImageMimeType = "";
        this.genre = Integer.MIN_VALUE;
        this.artist = Integer.MIN_VALUE;
        this.releaseYear = "";
        this.creationTime = "";
        this.favourite = new SimpleBooleanProperty(false);
        ++sequence;
    }

    public Album(String albumName, int artist, String releaseYear, String creationTime) {
        this.id = sequence;
        ++sequence;
        this.albumName = albumName;
        this.artist = artist;
        this.favourite = new SimpleBooleanProperty(false);
        this.releaseYear = releaseYear;
        this.creationTime = creationTime;
    }

    public long getS_NO() {
        return S_NO;
    }

    public int getId() {
        return id;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public byte[] getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(byte[] albumImage) {
        this.albumImage = albumImage;
    }

    public String getAlbumImageMimeType() {
        return albumImageMimeType;
    }

    public void setAlbumImageMimeType(String albumImageMimeType) {
        this.albumImageMimeType = albumImageMimeType;
    }

    public int getGenre() {
        return genre;
    }

    public void setGenre(int genre) {
        this.genre = genre;
    }

    public int getArtist() {
        return artist;
    }

    public void setArtist(int artist) {
        this.artist = artist;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public BooleanProperty getFavourite() {
        return favourite;
    }

    public void setFavourite(boolean isFavourite) {
        favourite.set(isFavourite);
    }

    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", albumName='" + albumName + '\'' +
                ", albumArtist='" + albumArtist + '\'' +
                ", albumImageMimeType='" + albumImageMimeType + '\'' +
                ", genre=" + genre +
                ", artist=" + artist +
                ", releaseYear=" + releaseYear +
                ", creationTime=" + creationTime +
                '}';
    }
}
