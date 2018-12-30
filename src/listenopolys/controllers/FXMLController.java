/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listenopolys.controllers;

import java.net.URL;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.util.Duration;

import java.util.*;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import listenopolys.models.*;
import listenopolys.models.PlaylistServices;

/**
 *
 * @author enmora
 */
public class FXMLController implements Initializable, TrackReaderListener {

    @FXML
    private Slider sliderMedia;

    @FXML
    private Slider sliderVolume;

    @FXML
    private ListView<Playlist> viewPlaylists;

    @FXML
    private ListView<Track> viewTracks;

    @FXML
    private Button buttonPlayPause;

    @FXML
    private ToggleButton buttonRepeat;

    @FXML
    private ToggleButton buttonRandom;

    @FXML
    private Label labelTotalTime;

    @FXML
    private Label labelCurrentTime;

    private PlaylistServices playlists;
    private TrackReader reader;
    private boolean repeat;
    private boolean random;
    private Timer timer;
    private List<Integer> randomList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        viewPlaylists.setCellFactory(playLv -> new ListCell<Playlist>(){
            @Override
            public void updateItem(Playlist item, boolean empty){
                super.updateItem(item, empty);
                if(empty){
                    setText(null);
                }
                else {
                    String text = item.getTitle();
                    setText(text);
                }
            }
        }
        );
        viewTracks.setCellFactory(trackLv -> new ListCell<Track>(){
            @Override
            public void updateItem(Track item, boolean empty){
                super.updateItem(item, empty);
                if(empty){
                    setText(null);
                }
                else {
                    String text = item.getTitle();
                    setText(text);
                }
            }
        }
        );
        sliderVolume.valueProperty().addListener(new InvalidationListener(){
            @Override
            public void invalidated(Observable observable){
                if(reader != null){
                    reader.getPlayer().setVolume(sliderVolume.getValue() / 100.0);
                }
            }
        });
        repeat=false;
        random=false;
        playlists = new PlaylistServices();
        viewPlaylists.setItems(playlists.getPlaylistList());
        playlists.addPlaylist(new Playlist("Hello World!"));
        playlists.getPlaylist("Hello World!").addTrack(new Track("test", "C:\\Users\\enzo_\\Downloads\\16061.mp3", "rock", 1995));
        playlists.getPlaylist("Hello World!").addTrack(new Track("test2", "C:\\Users\\enzo_\\Music\\Marshmello - Alone (Official Music Video).mp3", "rock", 1995));
        playlists.getPlaylist("Hello World!").addTrack(new Track("test3", "hugoladobe.wav", "rock", 1995));
        playlists.getPlaylist("Hello World!").addTrack(new Track("test4", "hugoladobe.wav", "rock", 1995));
        playlists.getPlaylist("Hello World!").addTrack(new Track("test5", "hugoladobe.wav", "rock", 1995));
        playlists.getPlaylist("Hello World!").addTrack(new Track("test6", "hugoladobe.wav", "rock", 1995));
        timer = new Timer();
    }


    public void viewPlaylistsClicked(){
        if(viewPlaylists.getSelectionModel().getSelectedItem() != null)
            viewTracks.setItems(viewPlaylists.getSelectionModel().getSelectedItem().getTracks());
    }

    public void viewTracksClicked(){
        if(viewTracks.getSelectionModel().getSelectedItem() != null && viewPlaylists.getSelectionModel().getSelectedItem() != null) {
            if(reader!=null) {
                reader.stop();
                buttonPlayPause.setText("Play");
            }
            reader = new TrackReader(viewTracks.getSelectionModel().getSelectedItem(), repeat);
            reader.getPlayer().setVolume(sliderVolume.getValue() / 100.0);
            reader.addListener(this);
            timer.cancel();
            timer.purge();
            Duration dur = viewTracks.getSelectionModel().getSelectedItem().getDuration();
            labelTotalTime.setText((int)(dur.toMinutes())+":"+(int)(dur.toSeconds())%60);
            labelCurrentTime.setText("0:0");
            sliderMedia.setValue(0);
        }
    }

    public void sliderMediaClickOut(){
        if(reader != null) {
            reader.getPlayer().seek(viewTracks.getSelectionModel().getSelectedItem().getDuration().multiply(sliderMedia.getValue() / 100.0));
            timer = new Timer();
            timer.schedule(new Updater(sliderMedia, labelCurrentTime, reader.getPlayer()), 0, 10);
        }
    }

    public void sliderMediaClickIn(){
        timer.cancel();
        timer.purge();
    }

    public void buttonPlayPauseClicked(){
        if(reader == null) return;
        if(reader.getStatus().equals("PAUSED")||reader.getStatus().equals("READY")||reader.getStatus().equals("STOPPED")){
            reader.play();
            buttonPlayPause.setText("Pause");
            timer.cancel();
            timer.purge();
            timer = new Timer();
            timer.schedule(new Updater(sliderMedia, labelCurrentTime, reader.getPlayer()), 0, 10);
        }
        else if(reader.getStatus().equals("PLAYING")){
            reader.pause();
            buttonPlayPause.setText("Play");
            timer.cancel();
            timer.purge();
        }
    }

    public void buttonStopClicked(){
        if(reader != null) {
            reader.stop();
            sliderMedia.setValue(0);
            labelCurrentTime.setText("0:0");
            buttonPlayPause.setText("Play");
            timer.cancel();
            timer.purge();
        }
    }

    public void endOfMedia() {
        if (!repeat) {
            timer.cancel();
            timer.purge();
            buttonPlayPause.setText("Play");
            reader.stop();
            int nextIndex;
            if(random){
                if(randomList.isEmpty()){
                    randomizeRandomList();
                }
                nextIndex = randomList.remove(0);
            }
            else
                nextIndex = (viewTracks.getSelectionModel().getSelectedIndex() + 1 >= viewTracks.getItems().size()) ? 0 : viewTracks.getSelectionModel().getSelectedIndex() + 1;
            viewTracks.scrollTo(nextIndex);
            viewTracks.getSelectionModel().select(nextIndex);
            viewTracks.getFocusModel().focus(nextIndex);
            viewTracksClicked();
            reader.getPlayer().setOnReady(() -> {
                        buttonPlayPauseClicked();
                    }
            );
        }
    }

    public void buttonRepeatClicked(){
        repeat = !repeat;
        if(reader!=null) {
            reader.setRepeatTo(repeat);
        }
    }

    private void randomizeRandomList() {
        randomList = new ArrayList<>();
        for (int i =0 ; i<viewTracks.getItems().size() ; i++) randomList.add(i);
        randomList.remove(viewTracks.getSelectionModel().getSelectedIndex());
        Collections.shuffle(randomList);
    }

    public void buttonRandomClicked(){
        random = !random;
        if(random){
            randomizeRandomList();
        }
    }

    public void close(){
        timer.cancel();
        timer.purge();
    }

}
