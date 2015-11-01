package com.vibz.vibz;

import android.graphics.Bitmap;

/**
 * Created by clement on 06/10/2015.
 * Modified by hugo on 31/10/2015
 * Represent a song
 */
public class Song {
    private long id;
    private String title;
    private String artist;
    private long duration;
    private Bitmap coverart;

    public Song(long songID, String songTitle, String songArtist, long duration, Bitmap bitmap) {
        this.id = songID;
        this.title = songTitle;
        this.artist = songArtist;
        this.duration = duration;
        this.coverart = bitmap;
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

    public String getStringDuration() {
        String seconds = String.valueOf((this.getDuration() % 60000) / 1000);
        String minutes = String.valueOf(this.getDuration() / 60000);
        String stringDuration = minutes + ":" + seconds;
        return stringDuration;
    }

    public Bitmap getCoverart() {
        return this.coverart;
    }

}
