package com.vibz.vibz;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by clement on 22/11/2015.
 */
public class DataTransferAsync extends AsyncTask<Void, Void, String> {

    private Context context;
    private TextView statusText;

    public DataTransferAsync(Context context) {
        this.context = context;
    }

    @Override
    public String doInBackground(Void... params) {
        try {

            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */

            ServerSocket myServerSocket = new ServerSocket(9999);
            Socket skt = myServerSocket.accept();
            ArrayList<String> my = new ArrayList<String>();
            my.add("Bernard");
            my.add("Grey");
            try {
                ObjectOutputStream objectOutput = new ObjectOutputStream(skt.getOutputStream());
                objectOutput.writeObject(my);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            return null;
        }
        return "worked";
    }


    /**
     * Start activity that can handle the JPEG image
     */
    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            Log.d("clem", "sent data");
        }
    }
}