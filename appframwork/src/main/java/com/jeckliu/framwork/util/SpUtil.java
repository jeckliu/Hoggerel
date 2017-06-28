package com.jeckliu.framwork.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.jeckliu.framwork.base.BaseApplication;

import java.util.HashSet;
import java.util.Set;

/***
 * Created by Jeck.Liu on 2017/4/5 0005.
 */
public class SpUtil {
    private static SharedPreferences sharedPreferences;
    private static SpUtil instance;

    public static SpUtil getInstance() {
        if (instance == null) {
            instance = new SpUtil();
        }
        return instance;
    }

    private SpUtil() {
        sharedPreferences = BaseApplication.getContext().getSharedPreferences("sp_hoggerel", Context.MODE_PRIVATE);
    }

    public void putStringSet(String key, Set<String> value) {
        sharedPreferences.edit().putStringSet(key, value).apply();
    }

    public Set<String> getStringSet(String key) {
        return sharedPreferences.getStringSet(key, new HashSet<String>());
    }

    public void put(String key, Object value) {
        if (value instanceof String) {
            sharedPreferences.edit().putString(key, (String) value).apply();
        } else if (value instanceof Long) {
            sharedPreferences.edit().putLong(key, (Long) value).apply();
        } else if (value instanceof Integer) {
            sharedPreferences.edit().putInt(key, (Integer) value).apply();
        } else if (value instanceof Boolean) {
            sharedPreferences.edit().putBoolean(key, (Boolean) value).apply();
        }
    }

    public Object get(String key, Object defaultValue) {
        if (defaultValue instanceof String) {
            return sharedPreferences.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Long) {
            return sharedPreferences.getLong(key, (Long) defaultValue);
        } else if (defaultValue instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultValue);
        }
        return defaultValue;
    }

    public void remove(String key){
        sharedPreferences.edit().remove(key).apply();
    }
}
