package com.vibz.vibz;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * ChooseCategoryActivity
 * Created by clement on 06/10/2015.
 * Modified by hugo 31/10/2015
 * Display information from the music library based on the selection of the user on the ListCategoryActivity
 */
public class ChooseCategoryActivity extends AppCompatActivity {

    //Declarations
    SwipeListView swipelistview;
    SongAdapter adapter;
    ArrayList<Song> itemSong;
    int lastPosition = -1;

    private MusicService musicSrv;
    private ListView itemView;
    private ArrayList listSongs = new ArrayList<Song>();
    private ArrayList listItems = new ArrayList<String>();
    private ArrayList listAlbums = new ArrayList<Album>();
    private boolean musicBound = false;

    private TextView itemTitle;

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

        Intent intent = getIntent();
        this.type = intent.getStringExtra("type");
        this.selection = intent.getStringExtra("selection");

        //If the user selected the list of all songs
        if (type.equals("song")) {
            setContentView(R.layout.song_list);
            itemView = (ListView) findViewById(R.id.song_list);

            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);

            swipelistview = (SwipeListView) findViewById(R.id.song_list);
            itemSong = new ArrayList<Song>();
            adapter = new SongAdapter(this, R.layout.song, itemSong, swipelistview);

            swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {
                @Override
                public void onOpened(int position, boolean toRight) {
                }

                @Override
                public void onClosed(int position, boolean fromRight) {
                }

                @Override
                public void onListChanged() {
                }

                @Override
                public void onMove(int position, float x) {
                }

                @Override
                public void onStartOpen(int position, int action, boolean right) {
                    Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
                    ListView listView = (ListView) findViewById(R.id.song_list);
                    View v = listView.getChildAt(position -
                            listView.getFirstVisiblePosition());
                    Button remove_addButton = (Button) v.findViewById(R.id.button_add_remove);
                    remove_addButton.setVisibility(View.VISIBLE);
                    if (lastPosition != -1 && lastPosition != position) {
                        swipelistview.closeAnimate(lastPosition);
                        ListView listView2 = (ListView) findViewById(R.id.song_list);
                        View v2 = listView2.getChildAt(lastPosition -
                                listView2.getFirstVisiblePosition());
                        Button remove_addButton2 = (Button) v2.findViewById(R.id.button_add_remove);
                        remove_addButton2.setVisibility(View.GONE);
                    }
                    lastPosition = position;
                }

                @Override
                public void onStartClose(int position, boolean right) {
                    Log.d("swipe", String.format("onStartClose %d", position));
                    ListView listView = (ListView) findViewById(R.id.song_list);
                    View v = listView.getChildAt(position -
                            listView.getFirstVisiblePosition());
                    Button remove_addButton = (Button) v.findViewById(R.id.button_add_remove);
                    remove_addButton.setVisibility(View.GONE);
                    lastPosition = position;
                }

                @Override
                public void onClickFrontView(int position) {
                    Log.d("swipe", String.format("onClickFrontView %d", position));
                    ListView listView = (ListView) findViewById(R.id.song_list);
                    View v = listView.getChildAt(position -
                            listView.getFirstVisiblePosition());
                    Button remove_addButton = (Button) v.findViewById(R.id.button_add_remove);
                    remove_addButton.setVisibility(View.VISIBLE);
                    if (lastPosition != -1 && lastPosition != position) {
                        swipelistview.closeAnimate(lastPosition);
                        ListView listView2 = (ListView) findViewById(R.id.song_list);
                        View v2 = listView2.getChildAt(lastPosition -
                                listView2.getFirstVisiblePosition());
                        Button remove_addButton2 = (Button) v2.findViewById(R.id.button_add_remove);
                        remove_addButton2.setVisibility(View.GONE);
                    }
                    swipelistview.openAnimate(position); //when you touch front view it will open
                    lastPosition = position;
                }

                @Override
                public void onClickBackView(int position) {
                    Log.d("swipe", String.format("onClickBackView %d", position));
                    ListView listView = (ListView) findViewById(R.id.song_list);
                    View v = listView.getChildAt(position -
                            listView.getFirstVisiblePosition());
                    Button remove_addButton = (Button) v.findViewById(R.id.button_add_remove);
                    remove_addButton.setVisibility(View.GONE);
                    swipelistview.closeAnimate(position);//when you touch back view it will close
                    lastPosition = -1;
                }

                @Override
                public void onDismiss(int[] reverseSortedPositions) {

                }

            });

            //These are the swipe listview settings. you can change these
            //setting as your requrement
            swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT); // there are five swiping modes
            swipelistview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL); //there are four swipe actions
            swipelistview.setOffsetLeft(convertDpToPixel(510f)); // left side offset
            swipelistview.setAnimationTime(200); // animarion time
            swipelistview.setSwipeOpenOnLongPress(true); // enable or disable SwipeOpenOnLongPress

            swipelistview.setAdapter(adapter);

            getAllSongs(this.selection);
            SongAdapter songAdt = new SongAdapter(this, R.layout.song, listSongs, swipelistview);
            itemView.setAdapter(songAdt);
        }

        //If the user selected the list of all albums
        else if (type.equals("album")) {
            setContentView(R.layout.album_list);
            itemView = (ListView) findViewById(R.id.album_list);
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);

            if (getAllAlbums(this.selection)) {
                AlbumAdapter itemAdt = new AlbumAdapter(this, R.layout.album, listAlbums);
                itemView.setAdapter(itemAdt);
            }
        }


        //If the user selected the list of all artists
        else if (type.equals("artist")) {
            setContentView(R.layout.artist_list);
            itemView = (ListView) findViewById(R.id.artist_list);
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            getAllArtists(this.selection);
            ArtistAdapter itemAdt = new ArtistAdapter(this, listItems);
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
                android.util.Log.d("hugo", "id for coverart: " + musicCursor.getLong(idColumn));

                //Get cover

                Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, thisId);
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(this.getApplicationContext(), trackUri);
                Bitmap bitmap;
                byte[] data = mmr.getEmbeddedPicture();

                // convert the byte array to a bitmap
                if (data != null) {
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                } else {
                    Drawable myDrawable = this.getResources().getDrawable(R.drawable.ic_fond);
                    bitmap = ((BitmapDrawable) myDrawable).getBitmap();
                }
//                Log.i("debug","freeMemoryAllowed:"+Runtime.getRuntime().freeMemory());
//                Log.i("debug","maxMemoryAllowed:"+Runtime.getRuntime().maxMemory());
//                Log.i("debug","totalMemoryAllowed:"+Runtime.getRuntime().totalMemory());
                //Bitmap bit = getResizedBitmap(bitmap,100);
                this.listSongs.add(new Song(thisId, thisTitle, thisArtist, thisDuration, bitmap));
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
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            do {
                long thisId = musicCursor.getLong(idColumn);
                Album album = new Album(thisId, musicCursor.getString(titleColumn), null);
                Bitmap bitmap = getBitmapFromSong(album);
                //bit = getResizedBitmap(bitmap,100);
                android.util.Log.d("clem", "search results allAlbums " + musicCursor.getString(titleColumn));
                this.listAlbums.add(new Album(thisId, musicCursor.getString(titleColumn), bitmap));
            }
            while (musicCursor.moveToNext());
            return true;
        }
        //No album found, so we get all the tracks for ths artist
        else if (musicCursor != null && musicCursor.getCount() == 0) {

            this.setContentView(R.layout.song_list);
            itemView = (ListView) findViewById(R.id.song_list);
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);

            swipelistview = (SwipeListView) findViewById(R.id.song_list);
            itemSong = new ArrayList<Song>();
            adapter = new SongAdapter(this, R.layout.song, itemSong, swipelistview);

            swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {
                @Override
                public void onOpened(int position, boolean toRight) {
                }

                @Override
                public void onClosed(int position, boolean fromRight) {
                }

                @Override
                public void onListChanged() {
                }

                @Override
                public void onMove(int position, float x) {
                }

                @Override
                public void onStartOpen(int position, int action, boolean right) {
                    Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
                }

                @Override
                public void onStartClose(int position, boolean right) {
                    Log.d("swipe", String.format("onStartClose %d", position));
                }

                @Override
                public void onClickFrontView(int position) {
                    Log.d("swipe", String.format("onClickFrontView %d", position));
                    if (lastPosition != -1) {
                        swipelistview.closeAnimate(lastPosition);
                    }
                    swipelistview.openAnimate(position); //when you touch front view it will open
                    lastPosition = position;
                }

                @Override
                public void onClickBackView(int position) {
                    Log.d("swipe", String.format("onClickBackView %d", position));

                    swipelistview.closeAnimate(position);//when you touch back view it will close
                    lastPosition = -1;
                }

                @Override
                public void onDismiss(int[] reverseSortedPositions) {

                }

            });

            //These are the swipe listview settings. you can change these
            //setting as your requrement
            swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT); // there are five swiping modes
            swipelistview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL); //there are four swipe actions
            swipelistview.setOffsetLeft(convertDpToPixel(510f)); // left side offset
            swipelistview.setAnimationTime(200); // animarion time
            swipelistview.setSwipeOpenOnLongPress(false); // enable or disable SwipeOpenOnLongPress

            swipelistview.setAdapter(adapter);

            this.getAllSongs(selection);
            SongAdapter songAdt = new SongAdapter(this, R.layout.song, listSongs, swipelistview);
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
                Intent intent = new Intent(this, PlaylistActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.action_search:
                handleMenuSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void handleMenuSearch() {
        ActionBar action = getSupportActionBar(); //get the actionbar

        if (isSearchOpened) { //test if the search is open

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

            edtSeach = (EditText) action.getCustomView().findViewById(R.id.edtSearch); //the text editor

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
        if (isSearchOpened) {
            handleMenuSearch();
            return;
        }
        super.onBackPressed();
    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    /**
     * Get the song list from the device
     */
    public Bitmap getBitmapFromSong(Album album) {
        String selection = MediaStore.Audio.Media.ALBUM_ID + " = '" + album.getID() + "'";
        Cursor musicCursor = MusicAccess.getAllSongs(this, selection, null, null);
        //Once we have the music, we iterate over it
        Bitmap bitmap = null;
        Bitmap bit = null;
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);


            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);

                //Get cover

                Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, thisId);
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(this.getApplicationContext(), trackUri);
                byte[] data = mmr.getEmbeddedPicture();

                // convert the byte array to a bitmap
                if (data != null) {
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                } else {
                    Drawable myDrawable = this.getResources().getDrawable(R.drawable.ic_fond);
                    bitmap = ((BitmapDrawable) myDrawable).getBitmap();
                }
                //bit = getResizedBitmap(bitmap,100);
            }
            while (musicCursor.moveToNext());
        }
        return bitmap;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
