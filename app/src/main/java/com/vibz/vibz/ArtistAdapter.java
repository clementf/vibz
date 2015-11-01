package com.vibz.vibz;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hugo on 31/10/2015.
 * Adapter made to display a Artist list in a Listview
 */
public class ArtistAdapter extends BaseAdapter {

    //Declarations

    private ArrayList<String> items = new ArrayList<>();
    private LayoutInflater songInf;
    private Context context;

    public ArtistAdapter(Context c, ArrayList<String> theItems) {
        this.items = theItems;
        this.context = c;
        this.songInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return items.size();
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
     * Add the content for each row of the list
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout itemLay = (LinearLayout) songInf.inflate
                (R.layout.artist, parent, false);
        //get title and artist views
        TextView itemView = (TextView) itemLay.findViewById(R.id.artist_title);
        //get item
        itemView.setText(MusicAccess.getArtistById(this.context, Integer.parseInt(items.get(position))));
        final String itemName = items.get(position).toString();
        //set position as tag
        itemLay.setTag(position);

        //Set on click listener, to launch new activity
        itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String nextType = "";
                String selection = "";

                nextType = "album";
                //Restriction by artist
                selection = MediaStore.Audio.Media.ARTIST_ID + " = '" + itemName + "'";

                //TODO : handle errors
                Intent intent = new Intent(ArtistAdapter.this.context, ChooseCategoryActivity.class);
                intent.putExtra("selection", selection);
                intent.putExtra("type", nextType);
                ArtistAdapter.this.context.startActivity(intent);
            }
        });
        return itemLay;
    }

}
