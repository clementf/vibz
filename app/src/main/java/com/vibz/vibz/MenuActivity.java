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

/**
 * Created by nicolasszewe on 23/10/15.
 */
public class MenuActivity  extends AppCompatActivity {
    public static MusicService musicSrv;
    public static SongAdapter songAdt;
    private Intent musicServiceIntent;
    private ListView itemView;
    private boolean musicBound = false;
    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicSrv = binder.getService();
            musicSrv.setList(musicSrv.PlaylistSongs);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        android.util.Log.d("The_best", "Let's debug this shit nigga");
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.menu);
        refreshPlaylist();

        if (musicServiceIntent == null) {
            musicServiceIntent = new Intent(this, MusicService.class);
            bindService(musicServiceIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(musicServiceIntent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshPlaylist();
    }

    public void refreshPlaylist(){
        itemView = (ListView) findViewById(R.id.playlist);
        this.songAdt = new SongAdapter(this, musicSrv.PlaylistSongs);
        itemView.setAdapter(songAdt);
    }
    public void CheckConnection(View view) {
        //Intent intent = new Intent(this, Connection.class);
        //startActivity(intent);
    }

    public void AddSongButton(View view) {
        Intent intent = new Intent(this, ListCategoryActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
