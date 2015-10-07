package com.vibz.vibz;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by clement on 06/10/2015.
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private final IBinder musicBind = new MusicBinder();
    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosition;

    public void onCreate() {
        //create the service
        super.onCreate();
        //initialize position
        this.songPosition = 0;
        //create player
        this.player = new MediaPlayer();
        initMusicPlayer();

    }


    public void initMusicPlayer() {
        //set player properties
        this.player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        this.player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        this.player.setOnPreparedListener(this);
        this.player.setOnCompletionListener(this);
        this.player.setOnErrorListener(this);

    }

    public void setList(ArrayList<Song> theSongs) {
        this.songs = theSongs;
    }

    public void playSong() {
        player.reset();
        //get song
        Song playedSong = this.songs.get(this.songPosition);
        //get id
        long currentSong = playedSong.getID();
        //set uri
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    public void setSong(int songIndex) {
        this.songPosition = songIndex;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
}