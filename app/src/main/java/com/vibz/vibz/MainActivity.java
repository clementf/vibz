package com.vibz.vibz;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


/**
 * Created by nicolasszewe on 3/11/15.
 */

public class MainActivity extends Activity {
    private static WifiP2pManager manager;
    private static WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    public WifiP2pInfo deviceInfo = new WifiP2pInfo();
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

            ImageButton layout = (ImageButton)findViewById(R.id.create_playlist_button);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)layout.getLayoutParams();
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
                    MusicService.PlaylistName = editText.getText().toString();
                    deviceInfo.isGroupOwner = true;
                    Intent intent = new Intent(MainActivity.this, PlaylistActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    static public void connect(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
            }
        });
    }

    public void onConnectionInfoAvailable(final WifiP2pInfo info) {

        String groupOwnerAddress = info.groupOwnerAddress.getHostAddress().toString();

        // After the group negotiation, we can determine the group owner.
        if (info.groupFormed && info.isGroupOwner) {
            Log.d("The Best","Server Starts");
            startServer();

        } else if (info.groupFormed) {
            Socket socket = new Socket();
            try {
                Log.d("The Best","Client comes");
                socket.connect(new InetSocketAddress(groupOwnerAddress, SERVER_PORT));
            }catch(IOException e){}
        }
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
        findViewById(R.id.create_playlist_button).setVisibility(view.GONE);
        findViewById(R.id.playlist_name).setVisibility(view.VISIBLE);
    }

    static public Context getContext(){
        return context;
    }

    public void startServer() {
        clients.clear();
        // Collect client ip's
            try
            {
                ServerSocket serverSocket = new ServerSocket(SERVER_PORT) ;
                while(true) {
                    Socket clientSocket = serverSocket.accept();
                    clients.add(clientSocket.getInetAddress());
                    clientSocket.close();
                }
            } catch (IOException e){}
    }
}
