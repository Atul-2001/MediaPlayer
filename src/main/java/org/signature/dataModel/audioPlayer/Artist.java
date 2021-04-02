package org.signature.dataModel.audioPlayer;

public class Artist {

    private static int sequence = 0;

    private final int id;
    private String name;

    public Artist() {
        this.id = sequence;
        this.name = "Unknown Artist " + id;
        ++sequence;
    }

    public Artist(String name) {
        this.id = sequence;
        ++sequence;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}