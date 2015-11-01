package com.vibz.vibz;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by clement on 06/10/2015.
 * Modified by hugo on 30/10/2015
 * Adapter made to display a song list in a Listview
 */
public class SongAdapter extends ArrayAdapter {

    private ArrayList<Song> songs = new ArrayList<>();
    private LayoutInflater songInf;
    private Context context;
    private int showedButton = -1;
    private SwipeListView swipelistview;
    int layoutResID;

    public SongAdapter(Context c, int layoutResourceId, ArrayList<Song> theSongs, SwipeListView slv) {
        super(c, layoutResourceId, theSongs);

        this.context = c;
        this.layoutResID = layoutResourceId;
        this.songs = theSongs;
        this.songInf = LayoutInflater.from(c);
        this.swipelistview = slv;
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

        NewsHolder holder = null;
        View row = convertView;
        holder = null;
        final String whereWeAre = context.getClass().getSimpleName();
        final Song currSong = songs.get(position);

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResID, parent, false);

        holder = new NewsHolder();

        holder.songTitle = (TextView) row.findViewById(R.id.song_title);
        holder.songArtist = (TextView) row.findViewById(R.id.song_artist);
        holder.songDuration = (TextView) row.findViewById(R.id.song_duration);
        holder.remove_addButton = (Button) row.findViewById(R.id.button_add_remove);
        holder.coverart = (ImageView) row.findViewById(R.id.coverartlist);
        holder.noteButton = (Button) row.findViewById(R.id.button_vote);

        holder.songTitle.setText(currSong.getTitle());
        holder.songArtist.setText(currSong.getArtist());
        holder.songDuration.setText(currSong.getStringDuration());
        holder.coverart.setImageBitmap(currSong.getCoverart());
        holder.noteButton.setBackgroundColor(Color.rgb(1, 145, 216));

        if (whereWeAre.equals("ChooseCategoryActivity") || whereWeAre.equals("SearchActivity")) {
            holder.remove_addButton.setText("Add");
            holder.remove_addButton.setBackgroundColor(Color.rgb(108, 142, 72));
        } else if (whereWeAre.equals("MenuActivity")) {
            holder.remove_addButton.setText("Remove");
            holder.remove_addButton.setBackgroundColor(Color.rgb(170, 0, 0));
        }
        holder.remove_addButton.setVisibility(View.VISIBLE);
        holder.remove_addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Get the name of the current activity
                if (whereWeAre.equals("ChooseCategoryActivity") || whereWeAre.equals("SearchActivity")) {
                    if (MusicService.firstPlay == false) {
                        MusicService.CurrentSong.add(currSong);
                        MenuActivity.musicSrv.onFirstPlay();
                        MusicService.firstPlay = true;
                        MusicService.isPlaying = true;
                    } else {
                        MusicService.PlaylistSongs.add(currSong);
                    }
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, currSong.getTitle() + " added to the playlist", duration);
                    toast.show();
                } else if (whereWeAre.equals("MenuActivity")) {
                    if (MusicService.PlaylistSongs.size() > 0) {
                        MusicService.PlaylistSongs.remove(currSong);
                        MenuActivity.songAdt.notifyDataSetChanged();
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, currSong.getTitle() + " removed from the playlist", duration);
                        toast.show();
                    }
                }
                swipelistview.closeAnimate(position);
            }
        });

        row.setTag(position);
        return row;
    }

    static class NewsHolder {

        TextView songTitle;
        TextView songArtist;
        TextView songDuration;
        ImageView coverart;
        Button remove_addButton;
        Button noteButton;
    }
}


