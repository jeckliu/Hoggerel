package com.jeckliu.framwork.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

/***
 * Created by Jeck.Liu on 2017/6/8 0008.
 */

public class DateUtil {
    private static SimpleDateFormat formatHM = new SimpleDateFormat("HH:mm", Locale.CHINA);
    private static SimpleDateFormat formatYMD_HMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
    private static SimpleDateFormat formatMS = new SimpleDateFormat("mm:ss",Locale.CHINA);


    public static String getMS(long time){
        return formatMS.format(time);
    }

    public static String getYMD_HMS(long time){
        return formatYMD_HMS.format(time);
    }
}
