package com.vibz.vibz;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by clement on 06/10/2015.
 * Adapter made to display a song list in a Listview
 */
public class SongAdapter extends BaseAdapter {

    //Declarations

    private ArrayList<Song> songs = new ArrayList<>();
    private LayoutInflater songInf;
    private Context context;


    public SongAdapter(Context c, ArrayList<Song> theSongs) {
        this.context = c;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        //map to song layout
        RelativeLayout songLay = (RelativeLayout) songInf.inflate
                (R.layout.song, parent, false);
        TextView songView = (TextView) songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView) songLay.findViewById(R.id.song_artist);
        TextView songDuration = (TextView) songLay.findViewById(R.id.song_duration);

        final Song currSong = songs.get(position);

        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());

        songDuration.setText(currSong.getStringDuration());

        final String whereWeAre = context.getClass().getSimpleName();
        final Button buttonAdd_remove = (Button) songLay.findViewById(R.id.button_add_remove);

        if(whereWeAre.equals("ChooseCategoryActivity")) {
            buttonAdd_remove.setText("+");
        }
        else if (whereWeAre.equals("MenuActivity")){
            buttonAdd_remove.setText("-");
        }

        buttonAdd_remove.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Get the name of the current activity
                if(whereWeAre.equals("ChooseCategoryActivity")) {
                    MusicService.PlaylistSongs.add(currSong);
                    if (MusicService.isPlaying == false) {
                        MenuActivity.musicSrv.onFirstPlay();
                        MusicService.isPlaying = true;
                    }
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, currSong.getTitle() + " added to the playlist", duration);
                    toast.show();
                }
                else if(whereWeAre.equals("MenuActivity")){
                    if(MusicService.PlaylistSongs.size()>1) {
                        MusicService.PlaylistSongs.remove(currSong);
                        MenuActivity.songAdt.notifyDataSetChanged();
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, currSong.getTitle() + " removed to the playlist", duration);
                        toast.show();
                    }
                }
                buttonAdd_remove.setVisibility(View.INVISIBLE);

            }
        });

        songLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(whereWeAre.equals("MenuActivity") &&  position == 0 ){}
                else buttonAdd_remove.setVisibility(View.VISIBLE);

            }
        });
        songLay.setTag(position);
        return songLay;
    }

}
