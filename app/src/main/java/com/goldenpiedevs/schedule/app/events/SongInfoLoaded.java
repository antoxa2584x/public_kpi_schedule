package com.goldenpiedevs.schedule.app.events;


public class SongInfoLoaded {
    private String song;

    public SongInfoLoaded(String song) {
        this.song = song;
    }

    public String getSong() {
        return song;
    }

    public String getArtist() {
        return song.split(" - ")[0];
    }

    public String getName() {
        return song.substring(song.indexOf(" - ") + 3, song.length());
    }

}
