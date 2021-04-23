package org.signature.dataModel.audioPlayer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "ONLINE_SONG")
public class OnlineSong {

    // Y = 89 & T = 84 & S = 83
    // y = 121 & t = 116 & s = 115
    @Transient
    private static int id = 8984830;

    @Id
    private int S_NO;
    private boolean isPlaylist;
    private String title;
    private String channelName;
    private double length;
    private String URL;
    private String thumbnailURL;
    private boolean isLocalSourceAvailable;
    @Transient
    private byte[] thumbnail;
    @Transient
    private final BooleanProperty playing;

    public OnlineSong() {
        this.thumbnail = null;
        this.playing = new SimpleBooleanProperty(false);
        this.isLocalSourceAvailable = false;
    }

    public OnlineSong(boolean isPlaylist, String title, String channelName, double length, String url, String thumbnailURL) {
        id++;
        this.isPlaylist = isPlaylist;
        this.title = title;
        this.channelName = channelName;
        this.length = length;
        this.URL = url;
        this.thumbnailURL = thumbnailURL;
        this.thumbnail = null;
        this.playing = new SimpleBooleanProperty(false);
        this.isLocalSourceAvailable = false;
    }

    public OnlineSong(boolean isPlaylist, String title, String channelName, double length, String url, byte[] thumbnail) {
        id++;
        this.isPlaylist = isPlaylist;
        this.title = title;
        this.channelName = channelName;
        this.length = length;
        this.URL = url;
        this.thumbnailURL = null;
        this.thumbnail = thumbnail;
        this.playing = new SimpleBooleanProperty(false);
        this.isLocalSourceAvailable = false;
    }

    public static int getId() {
        return id;
    }

    public int getSNO() {
        return S_NO;
    }

    public void setSNO(int sno) {
        this.S_NO = sno;
    }

    public boolean isPlaylist() {
        return isPlaylist;
    }

    public void setPlaylist(boolean playlist) {
        isPlaylist = playlist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String songTitle) {
        this.title = songTitle;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double songLength) {
        this.length = songLength;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String url) {
        this.URL = url;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public boolean isLocalSourceAvailable() {
        return isLocalSourceAvailable;
    }

    public void setLocalSourceAvailable(boolean localSourceAvailable) {
        isLocalSourceAvailable = localSourceAvailable;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isPlaying() {
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
        return "OnlineSong{" +
                "S_NO=" + S_NO +
                ", title='" + title + '\'' +
                ", channelName='" + channelName + '\'' +
                ", songLength=" + length +
                ", URL='" + URL + '\'' +
                ", thumbnailURL='" + thumbnailURL + '\'' +
                ", isLocalSourceAvailable=" + isLocalSourceAvailable +
                '}';
    }
}
