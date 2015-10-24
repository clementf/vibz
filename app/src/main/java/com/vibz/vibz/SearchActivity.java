package com.vibz.vibz;

import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private String search;
    private ArrayList listSongs = new ArrayList<Song>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list);
        Intent intent = getIntent();
        this.search = intent.getStringExtra("search");

        ListView itemView = (ListView) findViewById(R.id.song_list);

        Log.d("clem", "search term is : " + this.search);
        String selection = "";
        selection = "(" + MediaStore.Audio.Artists.ARTIST + " LIKE '%" + search + "%' OR ";
        selection += MediaStore.Audio.Media.TITLE+ " LIKE '%" + search + "%' OR ";
        selection += MediaStore.Audio.Albums.ALBUM+ " LIKE '%" + search + "%')";
        this.getAllSongs(selection);

        SongAdapter songAdt = new SongAdapter(this, listSongs);
        itemView.setAdapter(songAdt);
    }


    /**
     * Get the song list from the device
     */
    public void getAllSongs(String selection) {
        Cursor musicCursor = MusicAccess.getAllSongs(this, selection, null, null);
        //Once we have the music, we iterate over it
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                Long thisDuration = musicCursor.getLong(durationColumn);
                android.util.Log.d("clem", "search results allSongs " + musicCursor.getString(titleColumn));
                android.util.Log.d("clem", "song id : " + musicCursor.getString(artistColumn));
                this.listSongs.add(new Song(thisId, thisTitle, thisArtist, thisDuration));
            }
            while (musicCursor.moveToNext());
        }
    }


}
