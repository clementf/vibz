package com.vibz.vibz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationBroadcast extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(MusicService.NOTIFY_PLAY)) {
            Intent play = new Intent("player.play");
            LocalBroadcastManager.getInstance(context).sendBroadcast(play);
        } else if (intent.getAction().equals(MusicService.NOTIFY_PAUSE)) {
            Intent pause = new Intent("player.pause");
            LocalBroadcastManager.getInstance(context).sendBroadcast(pause);
        } else if (intent.getAction().equals(MusicService.NOTIFY_NEXT)) {
            Intent next = new Intent("player.next");
            LocalBroadcastManager.getInstance(context).sendBroadcast(next);
        }
    }
}