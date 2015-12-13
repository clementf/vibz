package com.vibz.vibz;

import android.graphics.Bitmap;

import java.util.HashMap;

/**
 * Created by clement on 21/11/2015.
 */
public class ImageCache {
    private static HashMap<Long, Bitmap> coverArts = new HashMap<>();
    public static Bitmap tryGetImage(long albumId){
        if(coverArts.get(albumId) != null){
            return coverArts.get(albumId);
        }
        else
            return null;
    }

    public static void cacheImage(long id, Bitmap bmp){
        coverArts.put(id,bmp );
    }
}
