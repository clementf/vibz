package com.vibz.vibz;

import android.graphics.Bitmap;

/**
 * Created by hugo on 31/10/2015.
 * Represent a Album
 */
public class Album {
    private long id;
    private String title;
    private Bitmap coverart;

    public Album(long AlbumID, String AlbumTitle, Bitmap bitmap) {
        this.id = AlbumID;
        this.title = AlbumTitle;
        this.coverart = bitmap;
    }

    public long getID() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public Bitmap getCoverart() {
        return this.coverart;
    }

}
