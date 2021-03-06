package com.vibz.vibz;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
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
import java.io.OutputStream;
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
        Log.d("clem", "background send data");
        try {
            ServerSocket serverSocket = new ServerSocket(8988);
            Log.d("clem", "Server: Socket opened");
            Socket client = serverSocket.accept();
            Log.d("clem", "Server: connection done");
            final File f = new File(Environment.getExternalStorageDirectory() + "/"
                    + context.getPackageName() + "/vibz-shared-" + System.currentTimeMillis()
                    + ".mp3");

            File dirs = new File(f.getParent());
            if (!dirs.exists())
                dirs.mkdirs();
            f.createNewFile();

            InputStream inputstream = client.getInputStream();
            copyFile(inputstream, new FileOutputStream(f));
            Log.d("clem", "server: copying files " + f.toString());
            serverSocket.close();
            Log.d("clem", "server is closing, path is : " + f.getAbsolutePath());
            return f.getAbsolutePath();
        } catch (IOException e) {
            Log.e("clem", e.getMessage());
            return null;
        }

    }


    /**
     * Start activity that can handle the music transferred
     */
    @Override
    protected void onPostExecute(String result) {
        Uri songUri = Uri.parse("file://" + result);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(result);
        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        long duration = Long.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)).longValue();
        if (title == null)
            title = "Unknown";
        if (artist == null)
            artist = "<Unknown>";
        Log.d("hugo", artist + " - " + title);
        Song ajout = new Song(0, title, artist, 0, duration, Uri.parse("file://" + result), 0, WiFiDirectBroadcastReceiver.mydeviceName);

        MusicService.PlaylistSongs.add(ajout);
    }

    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        Log.d("clem", "on reçoit des bits");
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d("clem", e.toString());
            return false;
        }
        return true;

    }
}

