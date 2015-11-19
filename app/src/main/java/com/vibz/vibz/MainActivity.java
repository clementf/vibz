package com.vibz.vibz;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.net.InetAddress;
import java.util.ArrayList;


/**
 * Created by nicolasszewe on 3/11/15.
 */

public class MainActivity extends Activity {
    private static WifiP2pManager manager;
    private static WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    private boolean isWifiP2pEnabled = false;
    private static final int SERVER_PORT = 1030;
    private ArrayList<InetAddress> clients = new ArrayList<InetAddress>();
    public static Context context;

    private BroadcastReceiver setVisibleDevice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("hugo", "Un device trouvé !");
            View view = getCurrentFocus();
            View v = findViewById(R.id.linearLayout1);
            View v2 = findViewById(R.id.create_playlist);
            View v3 = findViewById(R.id.loadingPanel);

            v.setVisibility(view.VISIBLE);
            v3.setVisibility(view.GONE);
            v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 0, 0.55f));
            v2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 0, 0.30f));

            ImageButton layout = (ImageButton) findViewById(R.id.create_playlist_button);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
            params.setMargins(80, 80, 80, 80);
            layout.setLayoutParams(params);
        }
    };

    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    private final IntentFilter intentFilter = new IntentFilter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        setContentView(R.layout.connectivity);


        LocalBroadcastManager.getInstance(this).registerReceiver(setVisibleDevice,
                new IntentFilter("deviceFound"));

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


                    findViewById(R.id.create_playlist_button).setVisibility(View.VISIBLE);
                    findViewById(R.id.vibz).setVisibility(View.VISIBLE);
                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                    findViewById(R.id.linearLayout1).setVisibility(View.VISIBLE);
                    findViewById(R.id.playlist_name).setVisibility(View.GONE);
                    findViewById(R.id.Image_create).setVisibility(View.GONE);

                    findViewById(R.id.loadingPanel).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 0, 0.05f));
                    findViewById(R.id.create_playlist).setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, 0, 0.8f));

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

    public void NamePlaylist(View view) {
        findViewById(R.id.create_playlist_button).setVisibility(view.GONE);
        findViewById(R.id.vibz).setVisibility(view.GONE);
        findViewById(R.id.loadingPanel).setVisibility(view.INVISIBLE);
        findViewById(R.id.linearLayout1).setVisibility(view.GONE);
        findViewById(R.id.playlist_name).setVisibility(view.VISIBLE);
        findViewById(R.id.Image_create).setVisibility(view.VISIBLE);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        layoutParams.setMargins(50, 50, 50, 50);
        findViewById(R.id.Image_create_img).setLayoutParams(layoutParams);
        findViewById(R.id.Image_create).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 0, 0.4f));
        findViewById(R.id.loadingPanel).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 0, 0.5f));
        findViewById(R.id.create_playlist).setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, 0, 0.1f));

        //Focus on edit text (show soft-keyboard)
        EditText yourEditText = (EditText) findViewById(R.id.playlist_name);
        yourEditText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
        yourEditText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));

    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.create_playlist_button).getVisibility() == View.GONE) {
            findViewById(R.id.create_playlist_button).setVisibility(View.VISIBLE);
            findViewById(R.id.vibz).setVisibility(View.VISIBLE);
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            findViewById(R.id.linearLayout1).setVisibility(View.VISIBLE);
            findViewById(R.id.playlist_name).setVisibility(View.GONE);
            findViewById(R.id.Image_create).setVisibility(View.GONE);

            findViewById(R.id.loadingPanel).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 0, 0.05f));
            findViewById(R.id.create_playlist).setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, 0, 0.8f));
        } else {
            super.onBackPressed();
        }
    }

    static public Context getContext() {
        return context;
    }

}
