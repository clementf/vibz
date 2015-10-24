package com.vibz.vibz;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * ChooseCategoryActivity
 * Created by clement on 06/10/2015.
 * Display information from the music library based on the selection of the user on the ListCategoryActivity
 */
public class ChooseCategoryActivity extends Activity {

    //Declarations
    private MusicService musicSrv;
    private ListView itemView;
    private ArrayList listSongs = new ArrayList<Song>();
    private ArrayList listItems = new ArrayList<String>();
    private boolean musicBound = false;

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicSrv = binder.getService();
            musicSrv.setList(listSongs);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };
    //Type of list to display : song, artist or album
    private String type = "";
    //Selection to make when querying the music library
    private String selection = "";


    //Getters and setters
    public String getType() {
        return this.type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list);
        itemView = (ListView) findViewById(R.id.song_list);
        Intent intent = getIntent();
        this.type = intent.getStringExtra("type");
        this.selection = intent.getStringExtra("selection");

        //If the user selected the list of all songs
        if (type.equals("song")) {
            getAllSongs(this.selection);
            SongAdapter songAdt = new SongAdapter(this, listSongs);
            itemView.setAdapter(songAdt);
        }
        //If the user selected the list of all albums
        else if (type.equals("album")) {
            if (getAllAlbums(this.selection)) {
                ItemAdapter itemAdt = new ItemAdapter(this, listItems, this.type);
                itemView.setAdapter(itemAdt);
            }
        }
        //If the user selected the list of all artists
        else if (type.equals("artist")) {
            getAllArtists(this.selection);
            ItemAdapter itemAdt = new ItemAdapter(this, listItems, this.type);
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

    /**
     * Get the album list from the device
     * Return true if some albums are found, otherwise false
     */
    public boolean getAllAlbums(String selection) {

        Cursor musicCursor = MusicAccess.getAllUniqueAlbums(this, selection);
        //Once we have the music, we iterate over it
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);

            do {
                android.util.Log.d("clem", "search results allAlbums " + musicCursor.getString(titleColumn));
                this.listItems.add(musicCursor.getString(0));
            }
            while (musicCursor.moveToNext());
            return true;
        }
        //No album found, so we get all the tracks for ths artist
        else if (musicCursor != null && musicCursor.getCount() == 0) {
            this.getAllSongs(selection);
            SongAdapter songAdt = new SongAdapter(this, listSongs);
            itemView.setAdapter(songAdt);
            return false;
        }

        return false;

    }

    /**
     * Get the artist list from the device
     */
    public void getAllArtists(String selection) {
        Cursor musicCursor = MusicAccess.getAllUniqueArtists(this, selection);
        //Once we have the music, we iterate over it
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);

            int titleColumnID = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST_ID);

            do {
                android.util.Log.d("clem", "artist id : " + musicCursor.getString(0));
                android.util.Log.d("clem", "search results allArtists " + musicCursor.getString(titleColumn));
                this.listItems.add(musicCursor.getString(0));
            }
            while (musicCursor.moveToNext());
        }
    }

    /**
     * Add a menu in the navigation bar to go back to the main activity
     */
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type something...");
        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchView.findViewById(searchPlateId);
        if (searchPlate!=null) {
            searchPlate.setBackgroundColor(Color.DKGRAY);
            int searchTextId = searchPlate.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView searchText = (TextView) searchPlate.findViewById(searchTextId);
            if (searchText!=null) {
                searchText.setTextColor(Color.WHITE);
                searchText.setHintTextColor(Color.WHITE);
            }
        }
        return true;
    }
*/

    /**
     * Selection of an item in the menu
     */
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }*/


}
