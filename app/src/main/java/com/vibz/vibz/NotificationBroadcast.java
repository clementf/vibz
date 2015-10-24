package com.vibz.vibz;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class NotificationBroadcast extends BroadcastReceiver {
    public static final String NOTIFY_PAUSE = "com.vibz.vibz.pause";
    public static final String NOTIFY_PLAY = "com.vibz.vibz.play";
    public static final String NOTIFY_NEXT = "com.vibz.vibz.next";
    public MusicService musicSrv;


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(NOTIFY_PLAY)) {
            //musicSrv.playSongAgain();
        } else if (intent.getAction().equals(NOTIFY_PAUSE)) {
            musicSrv.pauseSong();
        } else if (intent.getAction().equals(NOTIFY_NEXT)) {
            //musicSrv.nextSong();
        }
    }
}