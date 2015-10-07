package com.vibz.vibz;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * ChooseCategoryActivity
 * Created by clement on 06/10/2015.
 * Display information from the music library based on the selection of the user on the ListCategoryActivity
 */
public class ChooseCategoryActivity extends AppCompatActivity {

    //Declarations

    private ListView itemView;
    private ArrayList listSongs = new ArrayList<Song>();
    private ArrayList listItems = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list);
        itemView = (ListView) findViewById(R.id.song_list);
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");

        //If the user selected the list of all songs
        if (type.equals("song")) {
            getAllSongs();
            //Add the songs to the adapter
            SongAdapter songAdt = new SongAdapter(this, listSongs);
            itemView.setAdapter(songAdt);
        }
        //If the user selected the list of all albums
        else if (type.equals("album")) {
            getAllAlbums();
            //Add the albums to the adapter
            ItemAdapter itemAdt = new ItemAdapter(this, listItems);
            itemView.setAdapter(itemAdt);
        }
        //If the user selected the list of all artists
        else if (type.equals("artist")) {
            getAllArtists();
            //Add the artists to the adapter
            ItemAdapter itemAdt = new ItemAdapter(this, listItems);
            itemView.setAdapter(itemAdt);
        }

        //TODO : gérer les cas d'erreurs

        //Sort the items
        Collections.sort(listSongs, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });


    }

    /**
     * Get the song list from the device
     */
    public void getAllSongs() {
        Cursor musicCursor = MusicAccess.getAllSongs(this, null, null);
        //Once we have the music, we iterate over it
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            long durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                this.listSongs.add(new Song(thisId, thisTitle, thisArtist, durationColumn));
            }
            while (musicCursor.moveToNext());
        }
    }

    /**
     * Get the album list from the device
     */
    public void getAllAlbums() {

        Cursor musicCursor = MusicAccess.getAllUniqueAlbums(this);
        //Once we have the music, we iterate over it
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);

            do {
                this.listItems.add(musicCursor.getString(titleColumn));
            }
            while (musicCursor.moveToNext());
        }

    }

    /**
     * Get the artist list from the device
     */
    public void getAllArtists() {
        Cursor musicCursor = MusicAccess.getAllUniqueArtists(this);
        //Once we have the music, we iterate over it
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);

            do {
                this.listItems.add(musicCursor.getString(titleColumn));
            }
            while (musicCursor.moveToNext());
        }
    }

}
