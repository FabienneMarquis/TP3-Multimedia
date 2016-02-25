package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by 1494778 on 2016-02-05.
 */
public class PlayList extends Observable{

    private List<Song> playlist;


    public PlayList() {
        playlist = new ArrayList<>();
        // "Super Neko World.mp3" "07-Psapp-Everybody-Wants-To-Be-A-Cat.mp3" "Deftones - Change (In The House Of Flies).mp3"

        playlist.add(new Song(getClass().getResource("/Deftones - Change (In The House Of Flies).mp3").toString()));

        playlist.add(new Song(getClass().getResource("/Super Neko World.mp3").toString()));

        playlist.add(new Song(getClass().getResource("/07-Psapp-Everybody-Wants-To-Be-A-Cat.mp3").toString()));
    }

    public List<Song> getPlaylist() {
        return playlist;
    }
    public void addSong(Song song){
        playlist.add(song);
    }
    public ObservableList<Song> createPlaylist(){
        ObservableList<Song>  observableList = FXCollections
                .synchronizedObservableList(FXCollections.observableArrayList());
        observableList.addAll(getPlaylist());
       return observableList;
    }
}
