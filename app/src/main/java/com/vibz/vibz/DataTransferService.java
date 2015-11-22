package com.vibz.vibz;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.net.ServerSocket;

import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;

public class DataTransferService extends IntentService {
    public static ArrayList<InetAddress> clients = new ArrayList<InetAddress>();
    private boolean isWifiP2pEnabled = false;
    private static final int SERVER_PORT = 1030;

    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_FILE = "com.example.android.wifidirect.SEND_FILE";
    public static final String EXTRAS_FILE_PATH = "file_url";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";

    public DataTransferService(String name) {
        super(name);
    }

    protected BroadcastReceiver connect = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WifiP2pConfig config = new WifiP2pConfig();
            WifiP2pDevice device = intent.getParcelableExtra("Device");
            config.deviceAddress = device.deviceAddress;
            config.wps.setup = WpsInfo.PBC;

            MainActivity.manager.connect(MainActivity.channel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d("the_best", "Je suis connecté");
                }

                @Override
                public void onFailure(int reason) {
                }
            });
        }
    };

    class infoReceiver extends BroadcastReceiver implements WifiP2pManager.ConnectionInfoListener {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            Log.d("the_best","" + info);
            String groupOwnerAddress = info.groupOwnerAddress.getHostAddress();

            // After the group negotiation, we can determine the group owner.
            if (info.groupFormed && info.isGroupOwner) {
                Log.d("the_best", "Server Starts");
                startServer();

            } else if (info.groupFormed) {
                Socket socket = new Socket();
                try {
                    Log.d("the_best", "Client comes");
                    socket.connect(new InetSocketAddress(groupOwnerAddress, SERVER_PORT));
                } catch (IOException e) {
                }
            }
        }

        @Override
        public void onReceive(Context contexte, Intent intent){}
    }




    public DataTransferService(Context contexte) {
        super("FileTransferService");
        LocalBroadcastManager.getInstance(contexte).registerReceiver(connect,
                new IntentFilter("onConnect"));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        Log.d("the_best", "" + context);
        if (intent.getAction().equals(ACTION_SEND_FILE)) {
            String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            Socket socket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
            try {
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
                OutputStream stream = socket.getOutputStream();
                ContentResolver cr = context.getContentResolver();
                InputStream is = null;
                try {
                    is = cr.openInputStream(Uri.parse(fileUri));
                } catch (FileNotFoundException e) {
                }
                //DeviceDetailFragment.copyFile(is, stream);
            } catch (IOException e) {
            } finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    public void startServer() {
        clients.clear();
        // Collect client ip's
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                clients.add(clientSocket.getInetAddress());
                clientSocket.close();
            }
        } catch (IOException e) {
        }
    }

}

