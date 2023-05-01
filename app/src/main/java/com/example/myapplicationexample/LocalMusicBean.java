package com.example.myapplicationexample;

public class LocalMusicBean {

    private String id; //Song id
    private String song; //Song Name
    private String singer; //Singer Name
    private String album; //Album Name
    private String duration; //Song Length
    private String path; //Song Path
    //private String albumArt;  //Album Address

    public LocalMusicBean() {
    }

    public LocalMusicBean(String id, String song, String singer, String album, String duration, String path) {
        this.id = id;
        this.song = song;
        this.singer = singer;
        this.album = album;
        this.duration = duration;
        this.path = path;
        //this.albumArt = albumArt;
    }

//    public String getAlbumArt() {
//        return albumArt;
//    }
//
//    public void setAlbumArt(String albumArt) {
//        this.albumArt = albumArt;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
