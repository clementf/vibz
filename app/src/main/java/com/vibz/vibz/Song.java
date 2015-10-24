package com.vibz.vibz;

/**
 * Created by clement on 06/10/2015.
 * Represent a song
 */
public class Song {
    private long id;
    private String title;
    private String artist;
    private long duration;

    public Song(long songID, String songTitle, String songArtist, long duration) {
        this.id = songID;
        this.title = songTitle;
        this.artist = songArtist;
        this.duration = duration;
    }

    public long getID() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getArtist() {
        return this.artist;
    }

    public long getDuration() {
        return this.duration;
    }

}
