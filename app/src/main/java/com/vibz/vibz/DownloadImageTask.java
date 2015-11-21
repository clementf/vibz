package com.vibz.vibz;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by clement on 21/11/2015.
 */
public class DownloadImageTask extends AsyncTask<Uri, Integer, Bitmap> {

    private Context context;
    private long albumId;
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;

        public DownloadImageTask(ImageView imageView, Context c, long albumId) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
            this.context = c;
            this.albumId = albumId;
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

            return ImageProcessing.getResizedBitmap(bitmap,200);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                ImageCache.cacheImage(this.albumId, bitmap);

                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }



}
