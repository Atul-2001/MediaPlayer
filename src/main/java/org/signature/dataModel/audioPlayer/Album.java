package org.signature.dataModel.audioPlayer;


public class Album {

    private static int sequence = 0;

    private final int id;
    private String albumName;
    private String albumArtist;
    private String artist;
    private byte[] albumImage;
    private String albumImageMimeType;
    private int genre;
    private String releaseYear;
    private String creationTime; // creation time of song file (in millis)

    public Album() {
        this.id = sequence;
        this.albumName = "Unknown Album " + id;
        this.albumArtist = "Unknown Artist";
        this.artist = "Unknown Artist";
        this.albumImage = new byte[0];
        this.albumImageMimeType = "";
        this.genre = Integer.MIN_VALUE;
        this.releaseYear = "";
        this.creationTime = "";
        ++sequence;
    }

    public Album(String albumName, String artist, String releaseYear, String creationTime) {
        this.id = sequence;
        ++sequence;
        this.albumName = albumName;
        this.artist = artist;
        this.releaseYear = releaseYear;
        this.creationTime = creationTime;
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

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
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
