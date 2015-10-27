package com.vibz.vibz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by clement on 06/10/2015.
 * Adapter made to display a song list in a Listview
 */
public class SongAdapter extends BaseAdapter {

    private ArrayList<Song> songs = new ArrayList<>();
    private LayoutInflater songInf;
    private Context context;
    private int showedButton = -1;

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


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final String whereWeAre = context.getClass().getSimpleName();
        RelativeLayout songLay = (RelativeLayout) songInf.inflate
                    (R.layout.song, parent, false);
        final Song currSong = songs.get(position);

        TextView songView = (TextView) songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView) songLay.findViewById(R.id.song_artist);
        TextView songDuration = (TextView) songLay.findViewById(R.id.song_duration);
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());
        songDuration.setText(currSong.getStringDuration());

        final Button buttonAddRemove = (Button) songLay.findViewById(R.id.button_add_remove);
        if(whereWeAre.equals("ChooseCategoryActivity") || whereWeAre.equals("SearchActivity")) buttonAddRemove.setText("+");
        else if (whereWeAre.equals("MenuActivity")) buttonAddRemove.setText("-");

        if(showedButton != position)
            buttonAddRemove.setVisibility(View.INVISIBLE);

        buttonAddRemove.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Get the name of the current activity
                if (whereWeAre.equals("ChooseCategoryActivity") || whereWeAre.equals("SearchActivity")) {
                    if (MusicService.firstPlay == false) {
                        MusicService.CurrentSong.add(currSong);
                        MenuActivity.musicSrv.onFirstPlay();
                        MusicService.firstPlay = true;
                        MusicService.isPlaying = true;
                    }
                    else {
                        MusicService.PlaylistSongs.add(currSong);
                    }
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, currSong.getTitle() + " added to the playlist", duration);
                    toast.show();
                } else if (whereWeAre.equals("MenuActivity")) {
                    if (MusicService.PlaylistSongs.size() > 1) {
                        MusicService.PlaylistSongs.remove(currSong);
                        MenuActivity.songAdt.notifyDataSetChanged();
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, currSong.getTitle() + " removed to the playlist", duration);
                        toast.show();
                    }
                }
                buttonAddRemove.setVisibility(View.INVISIBLE);

            }
        });

        songLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position > 0 || !whereWeAre.equals("MenuActivity")){
                    buttonAddRemove.setVisibility(View.VISIBLE);
                    showedButton = position;
                    notifyDataSetChanged();
                }

            }
        });
        songLay.setTag(position);
        return songLay;
    }

}
