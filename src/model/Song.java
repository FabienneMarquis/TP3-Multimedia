package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.Observable;
import java.util.Set;

/**
 * Created by Fabienne on 2016-01-28.
 */
public class Song extends Observable {

    private String urlSong;

    private String infoSong;

    private String songFileName;

    private String album;
    private String artist;
    private String title;
    private String year;
    private String albumArtist;
    private String composer;
    private String genre;

    public Song(String urlSong) {
        this.urlSong = urlSong;
        Media media = new Media(urlSong);
        setMetadate(media);
    }

    public String getSongFileName() {
        return songFileName;
    }

    public String getInfoSong() {
        return infoSong;
    }

    public String getUrlSong() {
        return urlSong;
    }

    public void setInfoSong(String info) {
        this.infoSong = info;
    }

    public void setInfoSong() {
        //  voir si je mets les info là pour le pas de métadonné  if(album!=null&&albumArtist!=null)
        this.infoSong = "Album : " + album + "\n Album artist:" + albumArtist +
                "\n Artist : " + artist + "\n Composer : " + composer +
                "\n Genre : " + genre + "\n Title : " + title +
                "\n Year : " + year;
    }

    public String getArtist() {
        return artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setMetadate(Media media) {
        ObservableMap<String, Object> metadata = media.getMetadata();
        Set<String> keys = metadata.keySet();

        for (String key : keys) {
            if (!key.equals("raw metadata")) {
                if (key.equals("album")) {
                    setAlbum(metadata.get(key).toString());
                } else if (key.equals("artist")) {
                    setArtist(metadata.get(key).toString());
                } else if (key.equals("title")) {
                    setTitle(metadata.get(key).toString());
                } else if (key.equals("year")) {
                    setYear(metadata.get(key).toString());
                } else if (key.equals("albumArtist")) {
                    setAlbumArtist(metadata.get(key).toString());
                } else if (key.equals("composer")) {
                    setComposer(metadata.get(key).toString());
                } else if (key.equals("genre")) {
                    setGenre(metadata.get(key).toString());
                } else if (metadata.isEmpty()) {
                    setInfoSong("Aucune Métadonnées disponibles");
                }
            }
        }
        setInfoSong();

    }


}
