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
import android.widget.Button;
import android.widget.SeekBar;
import android.os.Handler;
import android.widget.TextView;

/**
 * Created by nicolasszewe on 23/10/15.
 */
public class MenuActivity extends AppCompatActivity {
    public static MusicService musicSrv;
    public static SongAdapter songAdt;
    private Intent musicServiceIntent;
    private ListView itemView;
    private boolean musicBound = false;
    private SeekBar seek_bar;
    private Handler seekHandler = new Handler();
    private TextView song_progress_text;
    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicSrv = binder.getService();
            musicSrv.setList(MusicService.PlaylistSongs);
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
        seekUpdation();

        if (musicServiceIntent == null) {
            musicServiceIntent = new Intent(this, MusicService.class);
            bindService(musicServiceIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(musicServiceIntent);
        }
    }

    Runnable run = new Runnable() {
        @Override public void run() {
            seekUpdation();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        refreshPlaylist();
        seek_bar = (SeekBar) findViewById(R.id.musicProgress);
        song_progress_text = (TextView) findViewById(R.id.song_progress_text);
        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar arg0, int songProgress, boolean arg2){
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (MusicService.isPlaying) {
                    int songProgress=seekBar.getProgress();
                    musicSrv.playSongAgain(songProgress);
                    song_progress_text.setText(convertPositionString(songProgress) + "/" + MusicService.PlaylistSongs.get(0).getStringDuration());
                }
            }

        });

        if(MusicService.PlaylistSongs.size()>0) {
            seek_bar.setMax((int) MusicService.PlaylistSongs.get(0).getDuration());
        }
        else {
            seek_bar.setMax(0);
        }
        seekHandler.postDelayed(run, 1000);
    }

    public void seekUpdation() {
        if(MusicService.isPlaying==true) {
            seek_bar.setProgress(musicSrv.getPosn());
            song_progress_text.setText(convertPositionString(musicSrv.getPosn()) + "/" + MusicService.PlaylistSongs.get(0).getStringDuration());
        }
        seekHandler.postDelayed(run, 1000);
    }




    public String convertPositionString(long position){
        String seconds = String.valueOf((position  % 60000) / 1000);
        String minutes = String.valueOf(position / 60000);
        String stringDuration = minutes + ":" + seconds;
        return stringDuration;
    }

    public void refreshPlaylist(){
        itemView = (ListView) findViewById(R.id.playlist);
        this.songAdt = new SongAdapter(this, MusicService.PlaylistSongs);
        itemView.setAdapter(songAdt);
    }

    public void onPausePlaySong(View view){
        if(musicSrv.isPlaying==true){
            musicSrv.pauseSong();
            musicSrv.isPlaying = false;
            Button p = (Button)findViewById(R.id.pause_play_song);
            p.setText(">");

        }
        else if(MusicService.PlaylistSongs.size()>0){
            musicSrv.playSongAgain();
            MusicService.isPlaying = true;
            Button p = (Button)findViewById(R.id.pause_play_song);
            p.setText("| |");
        }
    }

    public void onNextSong(View view){
       musicSrv.nextSong();
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
