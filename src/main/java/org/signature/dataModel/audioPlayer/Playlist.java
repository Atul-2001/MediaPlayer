package org.signature.dataModel.audioPlayer;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.signature.ui.audioPlayer.Inventory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "Playlists")
public class Playlist implements Comparable<Playlist> {

    private int id;
    private final StringProperty playlistName = new SimpleStringProperty();
    private final StringProperty totalSongs = new SimpleStringProperty();
    private final StringProperty totalSongDuration = new SimpleStringProperty();
    private List<PlaylistSong> songList = new ArrayList<>();

    public Playlist() {
        this("");
    }

    public Playlist(String playlistName) {
        this(playlistName, Collections.emptyList());
    }

    public Playlist(String playlistName, PlaylistSong playlistSong) {
        this(playlistName, Collections.singletonList(playlistSong));
    }

    public Playlist(String playlistName, List<PlaylistSong> playlistSongs) {
        this.id = -1;
        this.playlistName.set(playlistName);
        this.songList.addAll(playlistSongs);
        this.totalSongs.set("" + this.songList.size());
        this.totalSongDuration.set("0 mins");
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "playlist_generator")
    @SequenceGenerator(name = "playlist_generator", sequenceName = "playlist_seq", allocationSize = 1)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "Playlist_Name")
    public String getPlaylistName() {
        return playlistName.get();
    }

    @Transient
    public StringProperty playlistNameProperty() {
        return playlistName;
    }

    public void setPlaylistName(String playlistNameProperty) {
        this.playlistName.set(playlistNameProperty);
    }

    @Column(name = "Total_no_of_song")
    public String getTotalSongs() {
        return totalSongs.get();
    }

    public StringProperty totalSongsProperty() {
        return totalSongs;
    }

    public void setTotalSongs(String totalSongs) {
        this.totalSongs.set(totalSongs);
    }

    @Column(name = "Total_Duration_of_Songs")
    public String getTotalSongDuration() {
        return totalSongDuration.get();
    }

    public StringProperty totalSongDurationProperty() {
        return totalSongDuration;
    }

    public void setTotalSongDuration(String totalSongDuration) {
        this.totalSongDuration.set(totalSongDuration);
    }

    @OneToMany(mappedBy = "playlist", fetch = FetchType.EAGER)
    public List<PlaylistSong> getSongList() {
        return songList;
    }

    public void setSongList(List<PlaylistSong> songs) {
        this.songList = songs;
        this.totalSongs.set("" + this.songList.size());
    }

    public void addSong(PlaylistSong song) {
        if (song != null) {
            Inventory.updatePlaylist(this, song, true);
        }
    }

    public void addSongs(List<PlaylistSong> songs) {
        if (songs != null) {
            if (songs.size() > 0) {
                Inventory.updatePlaylist(this, songs);
            }
        }
    }

    public void setSongs(List<PlaylistSong> songs) {
        if (songs != null) {
            if (songs.size() > 0) {
                Inventory.updatePlaylist(this, songs);
            }
        }
    }

    public void removeSong(PlaylistSong song) {
        if (song != null) {
            Inventory.updatePlaylist(this, song, false);
        }
    }

    public void removeSong(String songTitle) {
        if (songTitle != null) {
            for (PlaylistSong playlistSong : songList) {
                if (playlistSong.getTitle().equals(songTitle)) {
                    Inventory.updatePlaylist(this, playlistSong, false);
                    break;
                }
            }
        }
    }

    public void removeAllSongs() {
        Inventory.updateRemoveAllSongFromPlaylist(this);
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "id=" + id +
                ", playlistName=" + playlistName.get() +
                ", totalSongs=" + totalSongs.get() +
                ", totalSongDuration=" + totalSongDuration.get() +
                ", songList=" + songList.size() +
                '}';
    }

    @Override
    public int compareTo(Playlist playlist) {
        return playlist.getId();
    }
}