package org.signature.dataModel.audioPlayer;

import org.signature.util.Utils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "ONLINE_SONG")
public class OnlineSong {

    @Id
    private int S_NO;

    // Y = 89 & T = 84 & S = 83
    // y = 121 & t = 116 & s = 115
    @Transient
    private static int id = 8984830;

    private boolean isPlaylist;
    private String songURL;
    private String thumbnailURL;
    private String songTitle;
    private String channelName;
    private double songLength;
    @Transient
    private boolean isPlaying;
    private boolean isLocalSourceAvailable;

    public OnlineSong() {
    }

    public OnlineSong(boolean isPlaylist, String songURL, String thumbnailURL, String songTitle, String channelName, double songLength) {
        id++;
        this.isPlaylist = isPlaylist;
        this.songURL = songURL;
        this.thumbnailURL = thumbnailURL;
        this.songTitle = songTitle;
        this.channelName = channelName;
        this.songLength = songLength;
        this.isLocalSourceAvailable = false;
    }

    public int getSNO() {
        return S_NO;
    }

    public void setSNO(int sno) {
        this.S_NO = sno;
    }

    public int getId() {
        return id;
    }

    public boolean isPlaylist() {
        return isPlaylist;
    }

    public void setPlaylist(boolean playlist) {
        isPlaylist = playlist;
    }

    public String getURL() {
        return this.songURL;
    }

    public void setURL(String url) {
        this.songURL = url;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getDuration() {
        return Utils.getDuration((long) songLength);
    }

    public double getSongLength() {
        return this.songLength;
    }

    public void setSongLength(double songLength) {
        this.songLength = songLength;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isLocalSourceAvailable() {
        return this.isLocalSourceAvailable;
    }

    public void setLocalSourceAvailable(boolean value) {
        this.isLocalSourceAvailable = value;
    }

    @Override
    public String toString() {
        return "Song{" +
                "songURL='" + songURL + '\'' +
                ", thumbnailURL='" + thumbnailURL + '\'' +
                ", songTitle='" + songTitle + '\'' +
                ", channelName='" + channelName + '\'' +
                '}';
    }
}
