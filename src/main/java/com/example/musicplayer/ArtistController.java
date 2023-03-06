package com.example.musicplayer;

import com.example.musicplayer.Model.Artist;
import com.example.musicplayer.Model.DataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class ArtistController {
    @FXML
    private TableView<Artist> artistTable;

    public void initialize(){
        Task<ObservableList<Artist>> task = new GetAllArtistsTask();
        artistTable.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();
    }
}

class GetAllArtistsTask extends Task {

    @Override
    public ObservableList<Artist> call() {
        return FXCollections.observableArrayList(DataSource.getInstance().queryArtist(DataSource.ORDER_BY_ASC));
    }
}
