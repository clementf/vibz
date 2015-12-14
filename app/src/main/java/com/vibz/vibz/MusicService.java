package com.vibz.vibz;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.util.ArrayList;

/**
 * Created by clement on 06/10/2015.
 * Service implementing the music player itself
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private final IBinder musicBind = new MusicBinder();
    public static MediaPlayer player;
    public static String PlaylistName = "";
    public static boolean premiereDevice;
    public static ArrayList<Song> CurrentSong = new ArrayList<>();
    public static ArrayList<Song> PlaylistSongs = new ArrayList<>();
    public static boolean isPlaying = false;
    public static boolean firstPlay = false;
    public int songPosition;

    public static final String NOTIFY_PAUSE = "com.vibz.vibz.pause";
    public static final String NOTIFY_PLAY = "com.vibz.vibz.play";
    public static final String NOTIFY_NEXT = "com.vibz.vibz.next";


    public void onCreate() {
        super.onCreate();

        NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();

        this.songPosition = 0;
        player = new MediaPlayer();
        initMusicPlayer();

        LocalBroadcastManager.getInstance(this).registerReceiver(onPause,
                new IntentFilter("player.pause"));
        LocalBroadcastManager.getInstance(this).registerReceiver(onPlay,
                new IntentFilter("player.play"));
        LocalBroadcastManager.getInstance(this).registerReceiver(onNext,
                new IntentFilter("player.next"));
    }

    public void initMusicPlayer() {
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs) {
        CurrentSong = theSongs;
    }

    public void playSong() {
        player.reset();
        long currentSongID = CurrentSong.get(0).getID();
        Uri trackUri = CurrentSong.get(0).getBitmapUri();
        if (currentSongID==0) {
            trackUri = CurrentSong.get(0).getBitmapUri();
        }
        else{
            trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSongID);
        }
        Log.d("NICO","" + trackUri);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            android.util.Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
        isPlaying = true;
        customSimpleNotification(this.getApplicationContext());
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
        return false;
    }

    public void onFirstPlay() {
        MusicService.firstPlay = true;
        this.setSong(0);
        this.playSong();

    }

    public void pauseSong() {
        player.pause();
        isPlaying = false;
        customSimpleNotification(this.getApplicationContext());
    }

    public int getPosn() {
        return player.getCurrentPosition();
    }

    public void playSongAgain() {
        player.seekTo(getPosn());
        player.start();
        isPlaying = true;
        customSimpleNotification(this.getApplicationContext());
    }

    public void playSongAgain(int position) {
        player.seekTo(position);
        player.start();
    }

    public void nextSong() {
        if (PlaylistSongs.size() > 0) {
            CurrentSong.remove(0);
            CurrentSong.add(PlaylistSongs.get(0));
            PlaylistSongs.remove(0);
            this.setSong(0);
            this.playSong();

        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //On supprime la dernière musique que si il y en a encore
        if (PlaylistSongs.size() > 0) {
            CurrentSong.add(PlaylistSongs.get(0));
            CurrentSong.remove(0);
            PlaylistSongs.remove(0);
            Intent intent = new Intent("musicCompletion");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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

    private BroadcastReceiver onPause = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pauseSong();
        }
    };

    private BroadcastReceiver onPlay = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            playSongAgain();
        }
    };

    private BroadcastReceiver onNext = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            nextSong();
        }
    };


    public static void customSimpleNotification(Context context) {
        RemoteViews simpleView = new RemoteViews(context.getPackageName(), R.layout.custom_notification);
        if (isPlaying) {
            simpleView.setViewVisibility(R.id.btnPause, View.VISIBLE);
            simpleView.setViewVisibility(R.id.btnPlay, View.GONE);
        } else {
            simpleView.setViewVisibility(R.id.btnPause, View.GONE);
            simpleView.setViewVisibility(R.id.btnPlay, View.VISIBLE);
        }

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.vibz)
                .setContentTitle("Custom Big View").build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.contentView = simpleView;
        notification.contentView.setTextViewText(R.id.textSongName, CurrentSong.get(0).getTitle());
        notification.contentView.setTextViewText(R.id.textAlbumName, CurrentSong.get(0).getArtist());

        setListeners(simpleView, context);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(7, notification);
    }


    private static void setListeners(RemoteViews view, Context context) {
        Intent pause = new Intent(NOTIFY_PAUSE);
        Intent next = new Intent(NOTIFY_NEXT);
        Intent play = new Intent(NOTIFY_PLAY);

        PendingIntent pPause = PendingIntent.getBroadcast(context, 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPause, pPause);

        PendingIntent pNext = PendingIntent.getBroadcast(context, 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnNext, pNext);

        PendingIntent pPlay = PendingIntent.getBroadcast(context, 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPlay, pPlay);
    }
}