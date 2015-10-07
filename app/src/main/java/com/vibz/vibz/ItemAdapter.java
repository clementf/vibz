package com.vibz.vibz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by clement on 06/10/2015.
 * Adapter made to display a item list in a Listview (Can be an artist, album or genre)
 */
public class ItemAdapter extends BaseAdapter {

    //Declarations

    private ArrayList<String> items = new ArrayList<>();
    private LayoutInflater songInf;

    public ItemAdapter(Context c, ArrayList<String> theItems) {
        this.items = theItems;
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
                (R.layout.artist_album_genre, parent, false);
        //get title and artist views
        TextView itemView = (TextView) itemLay.findViewById(R.id.item_title);
        //get item
        itemView.setText(items.get(position).toString());
        //set position as tag
        itemLay.setTag(position);
        return itemLay;
    }
}
