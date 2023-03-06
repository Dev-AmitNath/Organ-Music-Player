package com.example.musicplayer;

import com.example.musicplayer.Model.Song;
import javafx.scene.control.TableView;

public class SongTableDataModel {

    private TableView<Song> songsTable;

    private static SongTableDataModel instance = new SongTableDataModel();

    public static SongTableDataModel getInstance(){
        return instance;
    }

    public TableView<Song> getSongsTable() {
        return songsTable;
    }

    public void setSongsTable(TableView<Song> songsTable) {
        this.songsTable = songsTable;
    }
}
