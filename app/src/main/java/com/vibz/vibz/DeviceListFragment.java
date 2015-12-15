package com.vibz.vibz;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;


/**
 * A ListFragment that displays available peers on discovery and requests the
 * parent activity to handle user interaction events
 */

public class DeviceListFragment extends ListFragment implements PeerListListener {

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setListAdapter(new WiFiPeerListAdapter(getActivity(), R.layout.row_devices, peers));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.device_list, null);
        mContentView.setBackgroundColor(Color.TRANSPARENT);
        return mContentView;
    }


    public List<WifiP2pDevice> getDevices() {
        return peers;
    }

    public WifiP2pDevice getDevice() {
        return device;
    }

    /**
     * Array adapter for ListFragment that maintains WifiP2pDevice list.
     */
    private class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {
        private List<WifiP2pDevice> items;

        /**
         * @param context
         * @param textViewResourceId
         * @param objects
         */
        public WiFiPeerListAdapter(Context context, int textViewResourceId, List<WifiP2pDevice> objects) {
            super(context, textViewResourceId, objects);
            items = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_devices, null);
            }

            final WifiP2pDevice device = items.get(position);
            if (device != null) {
                Intent intent = new Intent("deviceFound");
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

                final TextView top = (TextView) v.findViewById(R.id.device_name);
                if (top != null) {
                    top.setText(device.deviceName);
                    try {
                        final String[] tempName = device.deviceName.split("$*:");
                        top.setText(tempName[1]);
                        Log.d("hugo", "Playlist found : " + top.getText());
                        top.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                //Start new activity page : show playlist
                                Intent intentMenu = new Intent(getActivity(), PlaylistActivity.class);
                                startActivity(intentMenu);
                                PlaylistActivity.isConsumer = true;
                                PlaylistActivity.isAdmin = false;
                                MusicService.PlaylistName = (String) top.getText();
                                top.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent("onConnect");
                                        intent.putExtra("Device", device);
                                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                                    }
                                }, 1000); //adding one sec delay
                            }
                        });
                    } catch (Exception e) {
                    }
                }
            }
            return v;
        }
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        peers.clear();
        List<WifiP2pDevice> listDevice = new ArrayList<>();
        listDevice.addAll(peerList.getDeviceList());
        for (int i = 0; i < listDevice.size(); i++) {
            if (listDevice.get(i).deviceName.contains("PlayListName$*:")) {
                peers.add(listDevice.get(i));
            }
        }
        ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
        if (peers.size() == 0) {
            return;
        }
    }

}




