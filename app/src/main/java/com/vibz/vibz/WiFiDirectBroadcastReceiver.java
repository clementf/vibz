/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vibz.vibz;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * A BroadcastReceiver that notifies of important wifi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private Channel channel;
    private Activity activity;
    public static String mydeviceName;
    public static ArrayList<InetAddress> clients = new ArrayList<InetAddress>();

    private BroadcastReceiver setDeviceName = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String name = intent.getStringExtra("Playlist");
                Method method = manager.getClass().getMethod("setDeviceName",
                        WifiP2pManager.Channel.class, String.class, WifiP2pManager.ActionListener.class);

                method.invoke(manager, channel, name, new WifiP2pManager.ActionListener() {
                    public void onSuccess() {}

                    public void onFailure(int reason) {}
                });
            } catch (Exception e)   {}
        }
    };


    private BroadcastReceiver disconnect = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (manager != null && channel != null) {
                manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                    @Override
                    public void onGroupInfoAvailable(WifiP2pGroup group) {
                        if (group != null && manager != null && channel != null
                                && group.isGroupOwner()) {
                            manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

                                @Override
                                public void onSuccess() {
                                    PlaylistActivity.IsConnected = false;
                                    PlaylistActivity.isAdmin = false;
                                    PlaylistActivity.isConsumer = false;
                                    Log.d("clem", "removeGroup onSuccess -");
                                }

                                @Override
                                public void onFailure(int reason) {
                                    Log.d("clem", "removeGroup onFailure -" + reason);
                                }
                            });
                        }
                    }
                });
            }
        }
    };

    private BroadcastReceiver sendFile = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            final  long tempMusique = intent.getLongExtra("musique", 0);


            Log.d("clem", "We are connected");

                // We are connected with the other device, request connection
                // info to find group owner IP

                manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        Log.d("clem", "other available connection niaaah");
                        String groupOwnerAddress = info.groupOwnerAddress.getHostAddress();
                        // After the group negotiation, we can determine the group owner.
                        if (info.groupFormed && info.isGroupOwner) {
                            // The other device acts as the client.
                            new DataTransferAsync(context).execute();
                            Log.d("clem", "We receive the file");
                        } else if (info.groupFormed) {
                            Log.d("clem", "the group is formed");
                            Intent serviceIntent = new Intent(context, DataTransferService.class);
                            serviceIntent.setAction(DataTransferService.ACTION_SEND_FILE);


                            Uri musique = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, tempMusique);
                            serviceIntent.putExtra(DataTransferService.EXTRAS_FILE_PATH, musique.toString());
                            serviceIntent.putExtra(DataTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                                    info.groupOwnerAddress.getHostAddress());
                            serviceIntent.putExtra(DataTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
                            context.startService(serviceIntent);
                            Log.d("clem", "the group is formed but we're not group owner");
                        }
                    }
                });
            }
    };

    /**
     * @param manager  WifiP2pManager system service
     * @param channel  Wifi p2p channel
     * @param activity activity associated with the receiver
     */
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
                                       Activity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
        LocalBroadcastManager.getInstance(this.activity).registerReceiver(setDeviceName,
                new IntentFilter("updateName"));
        LocalBroadcastManager.getInstance(this.activity).registerReceiver(disconnect,
                new IntentFilter("removeGroup"));
        LocalBroadcastManager.getInstance(this.activity).registerReceiver(sendFile,
                new IntentFilter("sendFile"));
    }




    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (manager != null) {
                manager.requestPeers(channel, (PeerListListener) activity.getFragmentManager()
                        .findFragmentById(R.id.frag_list));
            }


        }
        else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (manager == null) {
                return;
            }
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                PlaylistActivity.IsConnected = true;
            }

            manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
               public void onConnectionInfoAvailable(WifiP2pInfo info) {
                    if (info.groupFormed && info.isGroupOwner) {
                        PlaylistActivity.isAdmin = true;
                     }
                     else if (info.groupFormed) {
                        PlaylistActivity.isConsumer = true;
                    }
               }
            });
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                //WTF? C'est pour stocker le nom du device avant qu'on lui change pour la playlist. On se servira de la
               //variable set lors du reset
                if( MusicService.premiereDevice!= true ) {
                    WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                    mydeviceName = device.deviceName;
                    MusicService.premiereDevice = true;
                }
        }
    }
}