package com.vibz.vibz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

        holder.nbVote = (TextView) row.findViewById(R.id.voteNumber);
        holder.songTitle = (TextView) row.findViewById(R.id.song_title);
        holder.songArtist = (TextView) row.findViewById(R.id.song_artist);
        holder.songDuration = (TextView) row.findViewById(R.id.song_duration);
        holder.remove_addButton = (Button) row.findViewById(R.id.button_add_remove);
        holder.coverart = (ImageView) row.findViewById(R.id.coverartlist);
        holder.noteButton = (Button) row.findViewById(R.id.button_vote);
        holder.vide = (RelativeLayout) row.findViewById(R.id.vide);
        holder.nomDevice = (TextView) row.findViewById(R.id.nom_device);

        holder.nbVote.setText("+" + currSong.getNbVote());
        holder.songTitle.setText(currSong.getTitle());
        holder.songArtist.setText(currSong.getArtist());
        holder.songDuration.setText(currSong.getStringDuration());
        holder.nomDevice.setText(WiFiDirectBroadcastReceiver.mydeviceName);

        //Try to get the cover art from cache, or get it from the storage
        Bitmap tmp = ImageCache.tryGetImage(currSong.getAlbumId());
        if (tmp != null) {
            holder.coverart.setImageBitmap(tmp);
        } else {
            DownloadImageTask task = new DownloadImageTask(holder.coverart, this.context, currSong.getAlbumId());
            task.execute(currSong.getBitmapUri());
        }


        holder.noteButton.setBackgroundColor(Color.rgb(1, 145, 216));

        if (whereWeAre.equals("ChooseCategoryActivity") || whereWeAre.equals("SearchActivity")) {
            holder.vide.setVisibility(View.VISIBLE);
            holder.nbVote.setVisibility(View.GONE);
            holder.nomDevice.setVisibility(View.GONE);
            holder.remove_addButton.setText("Add");
            holder.remove_addButton.setBackgroundColor(Color.rgb(108, 142, 72));
        } else if (whereWeAre.equals("PlaylistActivity")) {
            holder.vide.setVisibility(View.GONE);
            holder.nbVote.setVisibility(View.VISIBLE);
            holder.nomDevice.setVisibility(View.VISIBLE);
            holder.remove_addButton.setText("Remove");
            holder.remove_addButton.setBackgroundColor(Color.rgb(170, 0, 0));
        }

        final NewsHolder finalHolder = holder;
        holder.noteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                long nbVote = currSong.getNbVote() + 1;
                currSong.setNbVote(nbVote);


                for (int i = 0; i < MusicService.PlaylistSongs.size(); i++) {
                    if (MusicService.PlaylistSongs.get(i).getNbVote() > nbVote) {

                    } else if (i == position) {
                        break;
                    } else if (MusicService.PlaylistSongs.get(i).getNbVote() < nbVote) {
                        MusicService.PlaylistSongs.remove(position);
                        MusicService.PlaylistSongs.add(i, currSong);
                        break;
                    }
                }

                currSong.setNbVote(nbVote);
                PlaylistActivity.songAdt.notifyDataSetChanged();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "You voted +1 for " + currSong.getTitle(), duration);
                toast.show();
            }
        });

        holder.remove_addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Get the name of the current activity
                if (whereWeAre.equals("ChooseCategoryActivity") || whereWeAre.equals("SearchActivity")) {
                    if (!MusicService.firstPlay) {
                        if(PlaylistActivity.IsConnected) {
                            if(PlaylistActivity.isAdmin) {
                                MusicService.CurrentSong.add(currSong);
                                PlaylistActivity.musicSrv.onFirstPlay();
                                MusicService.firstPlay = true;
                                MusicService.isPlaying = true;
                            }
                            Intent intent = new Intent("sendFile");
                            intent.putExtra("musique", currSong.getID());
                            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                        }
                        else {
                            MusicService.CurrentSong.add(currSong);
                            PlaylistActivity.musicSrv.onFirstPlay();
                            MusicService.firstPlay = true;
                            MusicService.isPlaying = true;
                        }
                    }
                    else {
                        if(PlaylistActivity.IsConnected) {
                            if(PlaylistActivity.isAdmin) {
                                MusicService.PlaylistSongs.add(currSong);
                            }
                            Intent intent = new Intent("sendFile");
                            intent.putExtra("musique", currSong.getID());
                            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                        }
                        else {
                            MusicService.PlaylistSongs.add(currSong);
                        }
                    }
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, currSong.getTitle() + " added to the playlist", duration);
                    toast.show();
                    finalHolder.remove_addButton.setVisibility(View.GONE);
                } else if (whereWeAre.equals("PlaylistActivity")) {
                    if (MusicService.PlaylistSongs.size() > 0) {
                        MusicService.PlaylistSongs.remove(currSong);
                        PlaylistActivity.songAdt.notifyDataSetChanged();
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
        TextView nbVote;
        ImageView coverart;
        Button remove_addButton;
        Button noteButton;
        RelativeLayout vide;
        TextView nomDevice;
    }
}


