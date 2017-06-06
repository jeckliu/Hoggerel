package com.jeckliu.hoggerel.util;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import java.util.ArrayList;
import java.util.List;

/***
 * Created by Jeck.Liu on 2017/2/14 0014.
 */
public class VideoUtil {
    private static VideoUtil instance;
    private static MediaMetadataRetriever metadataRetriever;
    private static String duration;

    public static VideoUtil getInstance(){
        if(instance == null){
            instance = new VideoUtil();
        }
        return instance;
    }

    public void init(String path){
        metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(path);
        duration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
    }

    public List<Bitmap> getBitmapsForVideo(){
        List<Bitmap> bitmaps = new ArrayList<>();
        int time = Integer.valueOf(duration) / 1000;
        for(int i = 1 ; i <= time ; i++){
            bitmaps.add(metadataRetriever.getFrameAtTime(i * 1000 * 1000));
        }
        return bitmaps;
    }

    public int getVideoDuration(){
        return Integer.valueOf(duration) / 1000;
    }
}
