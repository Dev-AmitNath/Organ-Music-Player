module com.example.musicplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires com.jfoenix;
    requires jaudiotagger;
    requires java.sql;

    opens com.example.musicplayer to javafx.fxml;
    opens com.example.musicplayer.Model;
    exports com.example.musicplayer.Model;
    exports com.example.musicplayer;
}