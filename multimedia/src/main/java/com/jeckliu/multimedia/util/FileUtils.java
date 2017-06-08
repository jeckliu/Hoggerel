package com.jeckliu.multimedia.util;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    public static String getOutputFile(){
        File rootDirectory = Environment.getExternalStorageDirectory();
        File videoDirectory = new File(rootDirectory,"com.liu.media");
        if(!videoDirectory.exists()){
            videoDirectory.mkdir();
        }
        String path = System.currentTimeMillis()+ "compress.mp4";
        File videoFile = new File(videoDirectory,path);
        return videoFile.getAbsolutePath();
    }

    public static List<String> getInputVideoFiles(){
        File rootFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File videoFile = new File(rootFile,"Camera");
        if(!videoFile.exists()){
            return null;
        }
        File[] files = videoFile.listFiles();
        List<String> paths = new ArrayList<>();
        for(File file : files){
            if(!file.isDirectory()){
                String path = file.getAbsolutePath();
                paths.add(path);
            }
        }
        return paths;
    }

    public static void deleteFile(String path){
        File file = new File(path);
        file.delete();
    }
}
