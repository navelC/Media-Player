package com.nqc.nqccuoiki;

import java.io.Serializable;

public class    Song implements Serializable {
    private String image;
    private String song;
    private String singer;
    private String source;
    private String key;
    private boolean fav;
    private int duration;
    public Song() {
    }

    public Song(String image, String song, String singer, String source) {
        this.image = image;
        this.song = song;
        this.singer = singer;
        this.source = source;
        this.duration = 60000;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getSource(){ return source;}

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public boolean getFav() {
        return fav;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setfav(boolean isfav) {
        this.fav = isfav;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Song)) return false;
        return getKey().equals(((Song) o).getKey());
    }
}
