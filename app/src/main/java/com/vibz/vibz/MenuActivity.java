package com.vibz.vibz;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by nicolasszewe on 23/10/15.
 */
public class MenuActivity  extends AppCompatActivity {
    private Intent playIntent;
    private ListView itemView;
    private boolean musicBound = false;
    public static boolean isPlaying;
    public static ArrayList PlaylistSongs = new ArrayList<Song>();
    public static MusicService musicSrv;

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicSrv = binder.getService();
            musicSrv.setList(MenuActivity.PlaylistSongs);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        android.util.Log.d("The_best","Let's debug this shit nigga");
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.menu);
        itemView = (ListView) findViewById(R.id.playlist);
        SongAdapter songAdt = new SongAdapter(this, PlaylistSongs);
        itemView.setAdapter(songAdt);
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
        }

    @Override
    protected void onStart() {
        super.onStart();
        itemView = (ListView) findViewById(R.id.playlist);
        SongAdapter songAdt = new SongAdapter(this,PlaylistSongs);
        itemView.setAdapter(songAdt);
    }

    public static void setPlaylistSongs(Song song){
        PlaylistSongs.add(song);
    }

    public static ArrayList getPlaylistSongs(){
        return PlaylistSongs;
    }

    public void CheckConnection(View view) {
        //Intent intent = new Intent(this, Connection.class);
        //startActivity(intent);
    }

    public void AddSong(View view) {
        Intent intent = new Intent(this, ListCategoryActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
