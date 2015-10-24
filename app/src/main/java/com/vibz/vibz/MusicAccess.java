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
                                     String selection,
                                     String[] projection,
                                     String sortOrder) {

        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        //selection is the restriction by album, artists or both
        if (selection == null || selection.equals("")) {
            selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        } else {
            selection += " AND " + MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        }


        return contentResolver.query(uri, null, selection, null, sortOrder);

    }

    /**
     * Queries Music library and returns a cursor with all unique artists
     */
    public static Cursor getAllUniqueArtists(Context context, String selection) {
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
    public static Cursor getAllUniqueAlbums(Context context, String selection) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = {MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS};
        return contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                projection,
                selection,
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

    public static String getAlbumById(Context context, Integer id) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = {MediaStore.Audio.Albums.ALBUM};
        String selection = MediaStore.Audio.Albums._ID + " = " + id;
        Cursor musicCursor = contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                MediaStore.Audio.Albums.ALBUM + " ASC");
        if (musicCursor != null && musicCursor.moveToFirst()) {
            return musicCursor.getString(0);
        }
        return "";
    }

    public static String getArtistById(Context context, Integer id) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = {MediaStore.Audio.Artists.ARTIST};
        String selection = MediaStore.Audio.Artists._ID + " = " + id;
        Cursor musicCursor = contentResolver.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                MediaStore.Audio.Artists.ARTIST + " ASC");
        if (musicCursor != null && musicCursor.moveToFirst()) {
            return musicCursor.getString(0);
        }
        return "";
    }
}
