package com.vibz.vibz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;

/**
 * Created by clement on 21/11/2015.
 */
public class BlurImageTask  extends AsyncTask<Uri, Integer, Bitmap> {
    private Context context;
    private long albumId;
    private int data = 0;
    private LinearLayout layout;

    public BlurImageTask(LinearLayout layout, Context c) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        this.context = c;
        this.layout = layout;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Uri... params) {
        Uri trackUri = params[0];
        Bitmap bitmap = null;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        if(trackUri != null)
            mmr.setDataSource(this.context, trackUri);
        else
            return null;
        byte[] data = mmr.getEmbeddedPicture();

        // convert the byte array to a bitmap
        if (data != null) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        } else {
            Drawable myDrawable = context.getResources().getDrawable(R.drawable.ic_fond);
            bitmap = ((BitmapDrawable) myDrawable).getBitmap();
        }


        return ImageProcessing.fastblur(bitmap, 0.1f, 10);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (this.layout != null && bitmap != null) {
            BitmapDrawable ob = new BitmapDrawable(this.context.getResources(), bitmap);
            this.layout.setBackgroundDrawable(ob);

        }
    }
}
