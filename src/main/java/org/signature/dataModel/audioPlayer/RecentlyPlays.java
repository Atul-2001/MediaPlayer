package org.signature.dataModel.audioPlayer;

import javax.persistence.*;

@Entity
public class RecentlyPlays {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recently_played_generator")
    @SequenceGenerator(name = "recently_played_generator", sequenceName = "recently_played_seq", allocationSize = 1)
    private int id;
    private String albumName;
    private String albumArtist;
    private String artist;

    public RecentlyPlays() {
    }

    public RecentlyPlays(Object object) {
        assert object instanceof Album || object instanceof Artist;

        if (object instanceof Album) {
            this.albumName = ((Album) object).getAlbumName();
            this.albumArtist = ((Album) object).getAlbumArtist();
            this.artist = ((Album) object).getArtist();
        } else {
            this.albumName = null;
            this.albumArtist = null;
            this.artist = ((Artist) object).getName();
        }
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
}
