package com.vibz.vibz;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


/**
 * Created by nicolasszewe on 3/11/15.
 */

public class ConnectionActivity extends Activity {
    private static WifiP2pManager manager;
    private static WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    private boolean isWifiP2pEnabled = false;
    private static final int SERVER_PORT = 1030;
    private ArrayList<InetAddress> clients = new ArrayList<InetAddress>();

    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }
    private final IntentFilter intentFilter = new IntentFilter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connectivity);

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
                    LocalBroadcastManager.getInstance(ConnectionActivity.this).sendBroadcast(intent);

                    Intent intent2 = new Intent(ConnectionActivity.this,MenuActivity.class);
                    startActivity(intent2);
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
        findViewById(R.id.playlist_name).setVisibility(view.VISIBLE);
        findViewById(R.id.create_playlist_button).setVisibility(view.INVISIBLE);
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
