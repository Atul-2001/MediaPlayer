package org.signature.dataModel.audioPlayer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Playlists")
public class Playlist {

    @Id
    private int id;
    private String playlistName;
    private int totalSongs;
    private String totalSongDuration;
    @OneToMany(mappedBy = "Playlist", fetch = FetchType.EAGER)
    private final List<Song> songList;

    public Playlist() {
        playlistName = "";
        totalSongs = 0;
        totalSongDuration = "0 mins";
        songList = new ArrayList<>();
    }

    public Playlist(String playlistName) {
        this.playlistName = playlistName;
        totalSongs = 0;
        totalSongDuration = "0 mins";
        songList = new ArrayList<>();
    }

    public Playlist(String playlistName, int totalSongs) {
        this.playlistName = playlistName;
        this.totalSongs = totalSongs;
        totalSongDuration = "0 mins";
        songList = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public int getTotalSongs() {
        return totalSongs;
    }

    public void setTotalSongs(int totalSongs) {
        this.totalSongs = totalSongs;
    }

    public String getTotalSongDuration() {
        return totalSongDuration;
    }

    public void setTotalSongDuration(String totalSongDuration) {
        this.totalSongDuration = totalSongDuration;
    }

    public List<Song> getSongList() {
        return songList;
    }
}
