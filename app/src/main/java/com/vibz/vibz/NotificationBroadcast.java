package com.vibz.vibz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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