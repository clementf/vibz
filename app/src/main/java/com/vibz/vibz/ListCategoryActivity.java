package com.vibz.vibz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;


/**
 * ListCategory Activity
 * Created by clement on 06/10/2015.
 * We simply list the categories (Musics, artists, albums) and redirect the user to the corresponding activity
 */
public class ListCategoryActivity extends AppCompatActivity {
    private ArrayList<Song> playList;
    private boolean musicBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.list_category);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    public void chooseBySong(View view) {
        Intent intent = new Intent(this, ChooseCategoryActivity.class);
        intent.putExtra("type", "song");
        startActivity(intent);

    }

    public void chooseByArtist(View view) {
        Intent intent = new Intent(this, ChooseCategoryActivity.class);
        intent.putExtra("type", "artist");
        startActivity(intent);
    }

    public void chooseByAlbum(View view) {
        Intent intent = new Intent(this, ChooseCategoryActivity.class);
        intent.putExtra("type", "album");
        startActivity(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    /**
     * Selection of an item in the menu
     */
    @Override
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
    }
}
