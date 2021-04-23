package org.signature.dataModel.audioPlayer;

import javax.persistence.*;

@Entity
public class RecentlyPlays {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recently_played_generator")
    @SequenceGenerator(name = "recently_played_generator", sequenceName = "recently_played_seq", allocationSize = 1)
    private int id;
    private String name;
    private String category;

    public RecentlyPlays() {
    }

    public RecentlyPlays(Object object) {
        assert object instanceof Album || object instanceof Artist || object instanceof Playlist;

        if (object instanceof Album) {
            this.name = ((Album) object).getAlbumName();
            this.category = "Album by ".concat(((Album) object).getArtist());
        } else if (object instanceof Artist){
            this.name = ((Artist) object).getName();
            this.category = "Artist";
        } else {
            this.name = ((Playlist) object).getPlaylistName();
            this.category = "Playlist";
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String albumName) {
        this.name = albumName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String albumArtist) {
        this.category = albumArtist;
    }

}
