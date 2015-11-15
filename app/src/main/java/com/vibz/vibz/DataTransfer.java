/*

import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;

    public static ArrayList<InetAddress> clients = new ArrayList<InetAddress>();
    private boolean isWifiP2pEnabled = false;
    private static final int SERVER_PORT = 1030;
        private static final int SERVER_PORT = 1030;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;


public void onConnectionInfoAvailable(final WifiP2pInfo info){

        String groupOwnerAddress=info.groupOwnerAddress.getHostAddress().toString();

        // After the group negotiation, we can determine the group owner.
        if(info.groupFormed&&info.isGroupOwner){
        Log.d("The Best","Server Starts");
        startServer();

        }else if(info.groupFormed){
        Socket socket=new Socket();
        try{
        Log.d("The Best","Client comes");
        socket.connect(new InetSocketAddress(groupOwnerAddress,SERVER_PORT));
        }catch(IOException e){}
        }
        }

public void startServer(){
        clients.clear();
        // Collect client ip's
        try
        {
        serverSocket=new ServerSocket(SERVER_PORT);
        while(true){
        Socket clientSocket=serverSocket.accept();
        clients.add(clientSocket.getInetAddress());
        clientSocket.close();
        }
        }catch(IOException e){}
        }
        }

    static public void connect(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {
            }
        });

    }

        */