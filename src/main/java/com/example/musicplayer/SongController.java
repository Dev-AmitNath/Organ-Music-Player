package com.example.musicplayer;

import com.example.musicplayer.Model.DataSource;
import com.example.musicplayer.Model.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class SongController{
    @FXML
    protected TableView<Song> songTable;

    @FXML
    public void initialize(){
        Task<ObservableList<Song>> task = new GetAllSongsTask();
        songTable.itemsProperty().bind(task.valueProperty());
        songTable.getSelectionModel().setCellSelectionEnabled(true);
        new Thread(task).start();
        SongTableDataModel.getInstance().setSongsTable(songTable);
    }

}

class GetAllSongsTask extends Task {

    @Override
    public ObservableList<Song> call() {
        return FXCollections.observableArrayList(DataSource.getInstance().querySong(DataSource.ORDER_BY_ASC));
    }

}


