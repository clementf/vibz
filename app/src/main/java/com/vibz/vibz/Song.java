package com.vibz.vibz;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by clement on 06/10/2015.
 * Modified by hugo on 31/10/2015
 * Represent a song
 */
public class Song {
    private long id;
    private long nbVote;
    private String title;
    private String artist;
    private long albumId;
    private long duration;
    private Bitmap coverart;
    private Uri bitmapUri;

    public Song(long songID, String songTitle, String songArtist, long albumId, long duration, Uri bitmap, long NbVote) {
        this.id = songID;
        this.title = songTitle;
        this.artist = songArtist;
        this.duration = duration;
        this.bitmapUri = bitmap;
        this.albumId = albumId;
        this.nbVote = NbVote;
    }

    public long getID() {
        return this.id;
    }

    public long getNbVote() {
        return this.nbVote;
    }

    public void setNbVote(long nbVote) {
        this.nbVote = nbVote;
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

    public Uri getBitmapUri(){
        return this.bitmapUri;
    }

    public long getAlbumId(){
        return this.albumId;
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
