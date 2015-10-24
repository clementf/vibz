package com.vibz.vibz;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by clement on 06/10/2015.
 * Adapter made to display a song list in a Listview
 */
public class SongAdapter extends BaseAdapter {

    //Declarations

    private ArrayList<Song> songs = new ArrayList<>();
    private LayoutInflater songInf;


    public SongAdapter(Context c, ArrayList<Song> theSongs) {
        this.songs = theSongs;
        this.songInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    /**
     * Add the content for each row of the list (Song and artist's names)
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout songLay = (LinearLayout) songInf.inflate
                (R.layout.song, parent, false);
        TextView songView = (TextView) songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView) songLay.findViewById(R.id.song_artist);
        TextView songDuration = (TextView) songLay.findViewById(R.id.song_duration);

        final Song currSong = songs.get(position);

        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());
        String seconds = String.valueOf((currSong.getDuration()  % 60000) / 1000);
        String minutes = String.valueOf(currSong.getDuration() / 60000);
        songDuration.setText(minutes+ ":" + seconds);

        songLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicService.PlaylistSongs.add(currSong);
                if (MusicService.firstPlay == false) {
                    MenuActivity.musicSrv.onFirstPlay();
                    MusicService.firstPlay = true;
                    MusicService.isPlaying = true;
                }
            }
        });
        songLay.setTag(position);
        return songLay;
    }

}
