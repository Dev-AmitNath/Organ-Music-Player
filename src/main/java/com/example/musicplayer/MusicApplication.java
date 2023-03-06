package com.example.musicplayer;

import com.example.musicplayer.Model.DataSource;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;


public class MusicApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("media.fxml"));
        Parent root = loader.load();
        Image image = new Image("logo-2.png");
        stage.setResizable(false);
        stage.getIcons().add(image);
        stage.setTitle("Organ Music Player");
        stage.setScene(new Scene(root,1024,700));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public MusicApplication() {
        super();
    }

    @Override
    public void init() throws Exception {
        super.init();
        if(!DataSource.getInstance().open()){
            System.out.println("Fatal error in the Database.");
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        DataSource.getInstance().close();
    }
}