package com.example.musicplayer;

import com.example.musicplayer.Model.Album;
import com.example.musicplayer.Model.Artist;
import com.example.musicplayer.Model.DataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class AlbumController {
    @FXML
    private TableView<Artist> albumTable;

    public void initialize(){
        Task<ObservableList<Artist>> task = new GetAllAlbumsTask();
        albumTable.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();
    }
}

class GetAllAlbumsTask extends Task {

    @Override
    public ObservableList<Album> call() {
        return FXCollections.observableArrayList(DataSource.getInstance().queryAlbum(DataSource.ORDER_BY_ASC));
    }
}
