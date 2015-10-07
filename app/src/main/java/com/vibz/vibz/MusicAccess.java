package com.vibz.vibz;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by clement on 06/10/2015.
 * Helps to find music and abstracts the querying part in other classes
 */
public class MusicAccess {


    /**
     * Queries Music library and returns a cursor with all songs.
     */
    public static Cursor getAllSongs(Context context,
                                     String[] projection,
                                     String sortOrder) {

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";

        return contentResolver.query(uri, null, selection, null, sortOrder);

    }

    /**
     * Queries Music library and returns a cursor with all unique artists
     */
    public static Cursor getAllUniqueArtists(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = {MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS};

        return contentResolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Audio.Artists.ARTIST + " ASC");

    }

    /**
     * Queries Music library and returns a cursor with all unique albums,
     */
    public static Cursor getAllUniqueAlbums(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = {MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS};

        return contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Audio.Albums.ALBUM + " ASC");

    }

    /**
     * Queries Music library and returns a cursor with all unique genres
     */
    public static Cursor getAllUniqueGenres(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = {MediaStore.Audio.Genres._ID,
                MediaStore.Audio.Genres.NAME};

        return contentResolver.query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Audio.Genres.NAME + " ASC");

    }

    /**
     * Queries Music library and returns a cursor with all unique playlists
     */
    public static Cursor getAllUniquePlaylists(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = {MediaStore.Audio.Playlists._ID,
                MediaStore.Audio.Playlists.NAME};

        return contentResolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Audio.Playlists.NAME + " ASC");

    }
}
