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
 * Created by clement on 06/10/2015.
 * Adapter made to display a item list in a Listview (Can be an artist, album or genre)
 */
public class ItemAdapter extends BaseAdapter {

    //Declarations

    private ArrayList<String> items = new ArrayList<>();
    private LayoutInflater songInf;
    private String typeView;
    private Context context;

    public ItemAdapter(Context c, ArrayList<String> theItems, String type) {
        this.items = theItems;
        this.context = c;
        this.typeView = type;
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
        final String itemName = items.get(position).toString();
        //set position as tag
        itemLay.setTag(position);

        //Set on click listener, to launch new activity
        itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String type = ItemAdapter.this.typeView;
                String nextType = "";
                String selection = "";

                if (type.equals("artist")) {
                    nextType = "album";
                    //Restriction by artist
                    selection = MediaStore.Audio.Media.ARTIST + " = '" + itemName + "'";
                    android.util.Log.d("clem", selection);
                }

                if (type.equals("album")) {
                    nextType = "song";
                    selection = MediaStore.Audio.Media.ALBUM + " = '" + itemName + "'";
                    android.util.Log.d("clem", selection);
                }

                //TODO : handle errors
                Intent intent = new Intent(ItemAdapter.this.context, ChooseCategoryActivity.class);
                intent.putExtra("selection", selection);
                intent.putExtra("type", nextType);
                ItemAdapter.this.context.startActivity(intent);
            }
        });

        return itemLay;
    }

}
