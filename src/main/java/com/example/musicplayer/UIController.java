package com.example.musicplayer;

import com.example.musicplayer.Model.DataSource;
import com.example.musicplayer.Model.Song;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class UIController {
    @FXML
    private BorderPane borderPane;
    @FXML
    private JFXButton addSong;
    @FXML
    private AnchorPane aPane;
    @FXML
    private Label songName;
    @FXML
    private Label totalDuration;
    @FXML
    private Label currentDuration;
    @FXML
    private JFXSlider progressBar;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ToggleButton playButton;
    @FXML
    private Button nextSong;
    @FXML
    private Button previousSong;
    @FXML
    private HBox songsButton;
    @FXML
    private HBox artistsButton;
    @FXML
    private Button stopButton;
    @FXML
    private ImageView albumArt;
    boolean open = false;

    private Media media;
    private MediaPlayer mediaPlayer;

    @FXML
    public void handleDashboardClick(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Welcome");
        alert.setHeaderText("Important Note: ");
        alert.setContentText("Support the developer by donating a \nCoffee: Rs20 \nSnacks: Rs120 \nPizza: Rs250");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && (result.get() == ButtonType.OK)){

        }
    }

    @FXML
    public void openArtistsTable() throws IOException {
        Pane loader = FXMLLoader.load(getClass().getResource("artists.fxml"));
        aPane.getChildren().setAll(loader);
    }

    @FXML
    public void openAlbumsTable() throws IOException {
        Pane loader = FXMLLoader.load(getClass().getResource("albums.fxml"));
        aPane.getChildren().setAll(loader);
    }

    @FXML
    public void openSongsTable() throws IOException {
        Pane loader = FXMLLoader.load(getClass().getResource("songs.fxml"));
        aPane.getChildren().setAll(loader);
        SongTableDataModel.getInstance().getSongsTable().getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                //Check whether item is selected and set value of selected item to Label

                Song song = SongTableDataModel.getInstance().getSongsTable().getSelectionModel().getSelectedItem();
                if(song != null)
                {
                    if(mediaPlayer != null){
                        mediaPlayer.stop();
                    }
                    media = new Media(new File(song.getLocation()).toURI().toString());
                    mediaPlayer = new MediaPlayer(media);
                    try {
                        initializePlay(mediaPlayer, song);
                    } catch (Exception e) {
                        System.out.println("Can't initialise playing this song.");
                    }
                    playButton.setSelected(true);
                    mediaPlayer.play();
                }
            }
        });
        open = true;
    }

    private void initializePlay(MediaPlayer mediaPlayer, Song song) throws CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException, IOException {
        progressBar.setDisable(false);
        volumeSlider.setDisable(false);

        AudioFile f = AudioFileIO.read(new File(song.getLocation()));
        Tag tag = f.getTag();
        byte[] bytes = tag.getFirstArtwork().getBinaryData();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        albumArt.setImage(new Image(in, 200, 200, true, true));
        final Circle clip = new Circle(100, 100, 100);
        albumArt.setClip(clip);
        songName.setText(song.getName() + "-" + f.getTag().getFirst(FieldKey.ARTIST));
        volumeSlider.setValue(mediaPlayer.getVolume() * 100);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaPlayer.setVolume(volumeSlider.getValue() / 100);
            }
        });

        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
              @Override
              public void changed(ObservableValue<? extends Duration> observable, javafx.util.Duration oldValue, javafx.util.Duration newValue) {
                  progressBar.setValue(newValue.toSeconds());
                  int minutes = (int) newValue.toMinutes();
                  int seconds = (int) newValue.toSeconds() - minutes * 60;
                  currentDuration.setText(minutes + ":" + seconds);
                  if(newValue.toSeconds() == mediaPlayer.getMedia().getDuration().toSeconds()){
                      SongTableDataModel.getInstance().getSongsTable().getSelectionModel().selectNext();
                      SongTableDataModel.getInstance().getSongsTable().getSelectionModel().selectNext();
                  }
              }
          }
        );

        progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
            }
        });

        progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
            }
        });

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                javafx.util.Duration total = media.getDuration();
                progressBar.setMax(total.toSeconds());
                int minutes = (int)total.toMinutes();
                int seconds = (int)total.toSeconds() - minutes*60;
                totalDuration.setText(minutes + ":" + seconds);
            }
        });
    }

    @FXML
    public void initialize(){
        progressBar.setDisable(true);
        volumeSlider.setDisable(true);
        albumArt.setImage(new Image("music-thumbnail.jpg", 200, 200, true, true));
        final Circle clip = new Circle(100, 100, 100);
        albumArt.setClip(clip);
        progressBar.setValue(0);

        addSong.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Song song = new Song();
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Add Song to Organ Music Player");
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Song","*.mp3"));
                File file = chooser.showOpenDialog(borderPane.getScene().getWindow());
                if(file != null){
                    System.out.println(file.getPath());
                    System.out.println(file.getName());
                    String songName = (file.getName().split("\\."))[0];

                    try {
                        AudioFile f = AudioFileIO.read(file);
                        Tag tag = f.getTag();
                        byte[] bytes = tag.getFirstArtwork().getBinaryData();
                        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                        System.out.println(tag.getFirst(FieldKey.TITLE));
                        System.out.println(tag.getFirst(FieldKey.TRACK));
                        System.out.println(tag.getFirst(FieldKey.ALBUM));
                        System.out.println(tag.getFirst(FieldKey.ARTIST));

                        DataSource.getInstance().insertSong(tag.getFirst(FieldKey.TITLE), tag.getFirst(FieldKey.ARTIST),tag.getFirst(FieldKey.ALBUM),Integer.parseInt(tag.getFirst(FieldKey.TRACK)),file.getPath());
                        try {
                            if (open) {
                                SongTableDataModel.getInstance().getSongsTable().refresh();
                                //openSongsTable();
                            }else{
                                openSongsTable();
                            }
                        }catch (IOException e){
                            System.out.println("Can't open songs table.");
                        }
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    System.out.println("Add Song window closed.");
                }
            }
        });

        nextSong.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                SongTableDataModel.getInstance().getSongsTable().getSelectionModel().selectBelowCell();
            }
        });

        previousSong.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                SongTableDataModel.getInstance().getSongsTable().getSelectionModel().selectAboveCell();
            }
        });

        playButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(mediaPlayer != null){
                    if(playButton.isSelected()){
                        ImageView img = new ImageView(new Image("play.png", 25,25,true, true));
                        img.setFitHeight(16);
                        img.setFitWidth(16);
                        playButton.setGraphic(img);
                        mediaPlayer.play();
                    }
                    if (!playButton.isSelected()) {
                        ImageView img = new ImageView(new Image("pause.png", 25,25,true, true));
                        img.setFitHeight(16);
                        img.setFitWidth(16);
                        playButton.setGraphic(img);
                        mediaPlayer.pause();
                    }
                }
            }
        });

        stopButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(mediaPlayer!= null){
                    if(playButton.isSelected()){
                        ImageView img = new ImageView(new Image("play.png", 25,25,true, true));
                        img.setFitHeight(16);
                        img.setFitWidth(16);
                        playButton.setSelected(false);
                        playButton.setGraphic(img);
                    }
                    mediaPlayer.stop();

                }
            }
        });

    }

}

