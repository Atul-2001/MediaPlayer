package org.signature.dataModel.audioPlayer;

import org.signature.ui.audioPlayer.Inventory;

import javax.persistence.*;

@Entity
public class RecentlyPlayed {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recently_played_generator")
    @SequenceGenerator(name = "recently_played_generator", sequenceName = "recently_played_seq", allocationSize = 1)
    private int id;
    private String albumName;
    private String albumArtist;
    private String artist;

    public RecentlyPlayed() {
    }

    public RecentlyPlayed(Album album) {
        assert album != null;
        Artist artist = Inventory.getArtist(album.getArtist());

        this.albumName = album.getAlbumName();
        this.albumArtist = album.getAlbumArtist();
        this.artist = artist.getName();
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
