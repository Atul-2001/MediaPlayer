package org.signature.dataModel.audioPlayer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javax.persistence.*;

@Entity
public class PlaylistSong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "playlist_song_generator")
    @SequenceGenerator(name = "playlist_song_generator", sequenceName = "playlist_song_seq", allocationSize = 1)
    private int id;
    @Column(length = 10000000)
    private byte[] thumbnail;
    private String title;
    private String artist;
    private String album;
    private String yearOfRelease;
    private String genre;
    private double length;
    private String location;
    @ManyToOne
    private Playlist playlist;

    @Transient
    private final BooleanProperty playing;

    public PlaylistSong() {
        this.thumbnail = new byte[0];
        this.title = "Song " + id;
        this.artist = "Unknown Artist";
        this.album = "Unknown Album";
        this.yearOfRelease = "";
        this.genre = "Unknown Genre";
        this.length = 0.0;
        this.location = "";
        this.playing = new SimpleBooleanProperty(false);
    }

    public PlaylistSong(byte[] thumbnail, String title, String artist, String album, String yearOfRelease, String genre, double length, String location) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.yearOfRelease = yearOfRelease;
        this.genre = genre;
        this.length = length;
        this.location = location;
        this.playing = new SimpleBooleanProperty(false);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getYearOfRelease() {
        return yearOfRelease;
    }

    public void setYearOfRelease(String yearOfRelease) {
        this.yearOfRelease = yearOfRelease;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public double getLength() {
        return length;
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

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public boolean getPlaying() {
        return playing.get();
    }

    public BooleanProperty playingProperty() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing.set(playing);
    }

    @Override
    public String toString() {
        return "PlaylistSong{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", yearOfRelease='" + yearOfRelease + '\'' +
                ", genre='" + genre + '\'' +
                ", length=" + length +
                ", location='" + location + '\'' +
                '}';
    }
}
