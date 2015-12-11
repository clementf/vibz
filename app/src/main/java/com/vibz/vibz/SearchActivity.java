package com.vibz.vibz;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
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

public class SearchActivity extends AppCompatActivity {

    //TODO ADD AutoCompletion Search
    private String search;
    private ArrayList listSongs = new ArrayList<Song>();

    SwipeListView swipelistview;
    SongAdapter adapter;
    ArrayList<Song> itemSong;
    int lastPosition;

    private Toolbar mToolbar;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list);
        Intent intent = getIntent();
        this.search = intent.getStringExtra("search");

        ListView itemView = (ListView) findViewById(R.id.song_list);

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

        Log.d("clem", "search term is : " + this.search);
        String selection = "";
        selection = "(" + MediaStore.Audio.Artists.ARTIST + " LIKE '%" + search + "%' OR ";
        selection += MediaStore.Audio.Media.TITLE + " LIKE '%" + search + "%' OR ";
        selection += MediaStore.Audio.Albums.ALBUM + " LIKE '%" + search + "%')";
        this.getAllSongs(selection);
        SongAdapter songAdt = new SongAdapter(this, R.layout.song, listSongs, swipelistview);
        itemView.setAdapter(songAdt);
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
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

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

    /**
     * Get the song list from the device
     */
    public void getAllSongs(String selection) {
        Cursor musicCursor = MusicAccess.getAllSongs(this, selection, null, null);
        //Once we have the music, we iterate over it
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                Long thisDuration = musicCursor.getLong(durationColumn);
                Long albumId = musicCursor.getLong(albumColumn);
                android.util.Log.d("clem", "search results allSongs " + musicCursor.getString(titleColumn));
                android.util.Log.d("clem", "song id : " + musicCursor.getString(artistColumn));

                //Get cover
                Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, thisId);
                this.listSongs.add(new Song(thisId, thisTitle, thisArtist, albumId, thisDuration, trackUri,0));
            }
            while (musicCursor.moveToNext());
        }
    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
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
