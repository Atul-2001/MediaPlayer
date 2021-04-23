package org.signature.dataModel.audioPlayer;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class MusicLibrary {

    @Id
    private int id;
    private String folderName;
    private String folderLocation;
    @Transient
    private boolean isLibraryLoaded = false;

    public MusicLibrary() {}

    public MusicLibrary(String folderName, String folderLocation) {
        this.folderName = folderName;
        this.folderLocation = folderLocation;
        this.id = hashCode();
    }

    public int getId() {
        return id;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderLocation() {
        return folderLocation;
    }

    public void setFolderLocation(String folderLocation) {
        this.folderLocation = folderLocation;
        this.id = hashCode();
    }

    public boolean isLibraryLoaded() {
        return isLibraryLoaded;
    }

    public void setLibraryLoaded(boolean libraryLoaded) {
        isLibraryLoaded = libraryLoaded;
    }

    @Override
    public int hashCode() {
        int result = folderName.hashCode();
        result = 31 * result + folderLocation.hashCode();
        return Math.abs(result)/10;
    }
}
