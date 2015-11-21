package com.vibz.vibz;

import android.graphics.Bitmap;

/**
 * Created by hugo on 31/10/2015.
 * Represent a Album
 */
public class Album {
    private long id;
    private String title;


    public Album(long AlbumID, String AlbumTitle) {
        this.id = AlbumID;
        this.title = AlbumTitle;

    }

    public long getID() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }



}
