package com.vibz.vibz;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
public class ChooseCategoryActivity extends AppCompatActivity{

    //Declarations
    private MusicService musicSrv;
    private ListView itemView;
    private ArrayList listSongs = new ArrayList<Song>();
    private ArrayList listItems = new ArrayList<String>();
    private boolean musicBound = false;

    private Toolbar mToolbar;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;

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
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_home:
                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_search:
                handleMenuSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void handleMenuSearch(){
        ActionBar action = getSupportActionBar(); //get the actionbar

        if(isSearchOpened){ //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(false); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_open_search));

            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSeach = (EditText)action.getCustomView().findViewById(R.id.edtSearch); //the text editor

            //this is a listener to do a search when the user clicks on search button
            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        doSearch(edtSeach.getText().toString());
                        return true;
                    }
                    return false;
                }
            });


            edtSeach.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);


            //add the close icon
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_close_search));

            isSearchOpened = true;
        }




    }
    private void doSearch(String str) {
        Log.d("clem", "search : " + str);
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("search", str);
        startActivity(intent);


    }

    @Override
    public void onBackPressed() {
        if(isSearchOpened) {
            handleMenuSearch();
            return;
        }
        super.onBackPressed();
    }


}
