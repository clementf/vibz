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

import java.util.ArrayList;

/**
 * Created by clement on 06/10/2015.
 * Service implementing the music player itself
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private final IBinder musicBind = new MusicBinder();
    private MediaPlayer player;
    public static ArrayList<Song> PlaylistSongs = new ArrayList<Song>();
    public static boolean isPlaying = false;
    public static boolean firstPlay = false;
    private int songPosition;


    public void onCreate() {
        super.onCreate();
        this.songPosition = 0;
        this.player = new MediaPlayer();
        initMusicPlayer();
    }


    public void initMusicPlayer() {
        this.player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        this.player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        this.player.setOnPreparedListener(this);
        this.player.setOnCompletionListener(this);
        this.player.setOnErrorListener(this);
    }


    public void setList(ArrayList<Song> theSongs) {
        this.PlaylistSongs = theSongs;
    }

    /**
     * Play the current song based on the position (index) of the playlist
     */
    public void playSong() {
        player.reset();
        Song playedSong = this.PlaylistSongs.get(this.songPosition);
        long currentSong = playedSong.getID();
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            android.util.Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    /**
     * Set the index of the playlist (Usefull for "nextSong" functionnality)
     *
     * @param songIndex
     */
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

    public void onFirstPlay(){
        this.setSong(0);
        this.playSong();
    }

    public void pauseSong(){
        player.pause();
    }

    public int getPosn(){
        return player.getCurrentPosition();
    }

    public void playSongAgain(){
        player.seekTo(getPosn());
        player.start();
    }

    public void playSongAgain(int position){
        player.seekTo(position);
        player.start();
    }

    public void nextSong(){
        if(PlaylistSongs.size() > 1){
            PlaylistSongs.remove(0);
            MenuActivity.songAdt.notifyDataSetChanged();
            this.setSong(0);
            this.playSong();
        }
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        //On supprime la dernière musique que si il y en a encore
        if(PlaylistSongs.size() > 1){
            PlaylistSongs.remove(0);
            MenuActivity.songAdt.notifyDataSetChanged();
        }
        this.setSong(0);
        this.playSong();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
}