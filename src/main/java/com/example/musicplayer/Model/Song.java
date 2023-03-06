package com.example.musicplayer.Model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Song {
    private SimpleIntegerProperty index;
    private SimpleIntegerProperty id;
    private SimpleIntegerProperty track;
    private SimpleStringProperty name;
    private SimpleIntegerProperty albumId;
    private SimpleStringProperty location;

    public Song(){
        this.id = new SimpleIntegerProperty();
        this.track = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.albumId = new SimpleIntegerProperty();
        this.index = new SimpleIntegerProperty();
        this.location = new SimpleStringProperty();
    }

    public String getLocation(){
        return location.get();
    }

    public void setLocation(String location){
        this.location.set(location);
    }

    public int getIndex() {
        return index.get();
    }

    public void setIndex(int index) {
        this.index.set(index);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public int getTrack() {
        return track.get();
    }

    public void setTrack(int track) {
        this.track.set(track);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getAlbumId() {
        return albumId.get();
    }

    public void setAlbumId(int albumId) {
        this.albumId.set(albumId);
    }
}
