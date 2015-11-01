package com.vibz.vibz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hugo on 31/10/2015.
 * Adapter made to display a Album list in a Listview
 */
public class AlbumAdapter extends ArrayAdapter {

    private ArrayList<Album> albums = new ArrayList<>();
    private LayoutInflater songInf;
    private Context context;
    int layoutResID;

    public AlbumAdapter(Context c, int layoutResourceId, ArrayList<Album> theAlbum) {
        super(c, layoutResourceId, theAlbum);

        this.context = c;
        this.layoutResID = layoutResourceId;
        this.albums = theAlbum;
        this.songInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return albums.size();
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
        final Album currAlbum = albums.get(position);


        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResID, parent, false);

        holder = new NewsHolder();

        holder.albumTitle = (TextView) row.findViewById(R.id.album_title);
        holder.coverart = (ImageView) row.findViewById(R.id.album_cover);

        holder.albumTitle.setText(currAlbum.getTitle());
        holder.coverart.setImageBitmap(currAlbum.getCoverart());

        final long itemName = albums.get(position).getID();

        //Set on click listener, to launch new activity
        holder.albumTitle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String nextType = "";
                String selection = "";

                nextType = "song";
                //Restriction by artist, and album
                selection = MediaStore.Audio.Media.ALBUM_ID + " = '" + itemName + "'";


                //TODO : handle errors
                Intent intent = new Intent(AlbumAdapter.this.context, ChooseCategoryActivity.class);
                intent.putExtra("selection", selection);
                intent.putExtra("type", nextType);
                AlbumAdapter.this.context.startActivity(intent);
            }
        });


        row.setTag(position);
        return row;
    }

    static class NewsHolder {

        TextView albumTitle;
        ImageView coverart;
    }
}