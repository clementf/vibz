package com.vibz.vibz;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.vibz.vibz.MusicService.MusicBinder;

import java.util.ArrayList;


/**
 * ListCategory Activity
 * Created by clement on 06/10/2015.
 * We simply list the categories (Musics, artists, albums) and redirect the user to the corresponding activity
 */
public class ListCategoryActivity extends AppCompatActivity {
    private MusicService musicSrv;
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

    public void songPicked(View view) {
        this.musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        this.musicSrv.playSong();
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
}
