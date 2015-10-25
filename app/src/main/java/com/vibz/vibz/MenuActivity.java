package com.vibz.vibz;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Button;
import android.widget.SeekBar;
import android.os.Handler;
import android.widget.TextView;

/**
 * Created by nicolasszewe on 23/10/15.
 */
public class MenuActivity extends AppCompatActivity {

    public static final String TAG = "wifidirectdemo";
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;
    private BroadcastReceiver onCompletionListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("The_best", "La musique est finie");
            songAdt.notifyDataSetChanged();
            addFirstSong();
            seek_bar.setMax((int) musicSrv.CurrentSong.get(0).getDuration());
        }

    };



    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    public static MusicService musicSrv;
    public static SongAdapter songAdt;
    private Intent musicServiceIntent;
    private ListView itemView;
    private boolean musicBound = false;
    public static SeekBar seek_bar;
    private Handler seekHandler = new Handler();
    private TextView song_progress_text;
    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicSrv = binder.getService();
            musicSrv.setList(MusicService.CurrentSong);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.menu);

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
        LocalBroadcastManager.getInstance(this).registerReceiver(onCompletionListener,
                new IntentFilter("musicCompletion"));

        addFirstSong();
        displayVibzMessages();
        refreshPlaylist();
        seekUpdation();

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
                if (MusicService.firstPlay) {
                    int songProgress = seekBar.getProgress();
                    musicSrv.playSongAgain(songProgress);
                    song_progress_text.setText(convertPositionString(songProgress) + "/" + musicSrv.CurrentSong.get(0).getStringDuration());
                    Button p = (Button)findViewById(R.id.pause_play_song);
                    p.setText("| |");
                }
            }
        });

        if(musicSrv.CurrentSong.size()>0) {
            seek_bar.setMax((int) musicSrv.CurrentSong.get(0).getDuration());
        } else {
            seek_bar.setMax(0);
        }
        seekHandler.postDelayed(run, 1000);
    }


    //All the method that are called on start
    public void seekUpdation() {
        if(MusicService.isPlaying==true) {
            seek_bar.setProgress(musicSrv.getPosn());
            song_progress_text.setText(convertPositionString(musicSrv.getPosn()) + "/" + musicSrv.CurrentSong.get(0).getStringDuration());
        }
        seekHandler.postDelayed(run, 1000);
    }
    public void displayVibzMessages(){
        TextView menuTitle = (TextView)findViewById(R.id.menuTitle);
        if(musicSrv.CurrentSong.size()==0){
            menuTitle.setText("TO START, ADD A SONG TO SHOW YOUR VIBZ!");
            menuTitle.setTextSize(20);
        }
        else if(musicSrv.PlaylistSongs.size()==0){
            menuTitle.setText("COME ON, THERE ARE NO SONG LEFT AFTER THAT ONE! SHOW US YOUR VIBZ");
            menuTitle.setTextSize(20);
        }
        else {
            menuTitle.setText("");
            menuTitle.setVisibility(View.GONE);
        }
    }
    public void addFirstSong(){
        if(musicSrv.CurrentSong.size()>0) {
            String artist = musicSrv.CurrentSong.get(0).getArtist();
            TextView firstsongView = (TextView) findViewById(R.id.firstsong_title);
            TextView firstartistView = (TextView) findViewById(R.id.firstsong_artist);
            if(artist.equals("<unknown>")){
                firstartistView.setText("");
            }
            else {
                firstartistView.setText(musicSrv.CurrentSong.get(0).getArtist());
            }
            TextView firstsongDuration = (TextView) findViewById(R.id.firstsong_duration);
            firstsongView.setText(musicSrv.CurrentSong.get(0).getTitle());
            firstsongDuration.setText(musicSrv.CurrentSong.get(0).getStringDuration());
        }
    }
    public void refreshPlaylist(){
        itemView = (ListView) findViewById(R.id.playlist);
        this.songAdt = new SongAdapter(this, MusicService.PlaylistSongs);
        itemView.setAdapter(songAdt);
    }



    //Player function
    public void onPausePlaySong(View view){
        if(musicSrv.isPlaying==true){
            musicSrv.pauseSong();
            musicSrv.isPlaying = false;
            Button p = (Button)findViewById(R.id.pause_play_song);
            p.setText(">");

        }
        else if(musicSrv.CurrentSong.size()>0){
            musicSrv.playSongAgain();
            MusicService.isPlaying = true;
            Button p = (Button)findViewById(R.id.pause_play_song);
            p.setText("| |");
        }
    }
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onCompletionListener);
        super.onPause();
    }
    public void onNextSong(View view){
        musicSrv.nextSong();
        seek_bar.setMax((int) MusicService.CurrentSong.get(0).getDuration());
        songAdt.notifyDataSetChanged();
        addFirstSong();
    }

    //Useful method
    public String convertPositionString(long position){
        String seconds = String.valueOf((position  % 60000) / 1000);
        String minutes = String.valueOf(position / 60000);
        String stringDuration = minutes + ":" + seconds;
        return stringDuration;
    }

    //2 bottoms buttons functions
    public void CheckConnection(View view) {
        Intent intent = new Intent(this, WiFiDirectActivity.class);
        startActivity(intent);
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
