package com.jeckliu.mediarecorder;

import android.os.Environment;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/***
 * Created by Jeck.Liu on 2017/1/19 0019.
 */
public class FileUtils {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    public static String getOutputMediaFile(int type){
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "com.jeckliu.media");
        if(!mediaStorageDir.exists()){
            mediaStorageDir.mkdirs();
        }
        String rootDir = mediaStorageDir.getAbsolutePath();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        String mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = rootDir + File.separator + "IMG_"+ timeStamp + ".jpg";
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = rootDir + File.separator + "VID_"+ timeStamp + ".mp4";
        } else {
            return null;
        }
        return mediaFile;
    }
}
