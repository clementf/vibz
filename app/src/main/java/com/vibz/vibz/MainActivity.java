package com.vibz.vibz;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;



/**
 * Created by nicolasszewe on 3/11/15.
 */

public class MainActivity extends Activity {
    public static WifiP2pManager manager;
    public static WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;

    private final IntentFilter intentFilter = new IntentFilter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connectivity);

        DataTransferService dataService = new DataTransferService(this);
        // add necessary intent values to be matched.

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        //The Manager which is looking for other devices
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {
            }
        });


        final EditText editText = (EditText) findViewById(R.id.playlist_name);
        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    MusicService.PlaylistName = editText.getText().toString();
                    Intent intent = new Intent("updateName");
                    intent.putExtra("Playlist", "PlayListName$*:" + MusicService.PlaylistName);
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);

                    Intent intent2 = new Intent(MainActivity.this, PlaylistActivity.class);
                    startActivity(intent2);
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void NamePlaylist(View view){
        findViewById(R.id.playlist_name).setVisibility(view.VISIBLE);
        findViewById(R.id.create_playlist_button).setVisibility(view.INVISIBLE);
    }

}
