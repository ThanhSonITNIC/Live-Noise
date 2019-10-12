package com.hackathon.livenoisex.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.hackathon.livenoisex.MyApplication;

public class SharedPreferenceHelper {
    public static final String KEY_DEVICE_ID = "device_id";
    private final static String PREF_FILE = "live_noise";

    private final SharedPreferences settings;
    private final SharedPreferences.Editor editor;
    private Context context;

    private static SharedPreferenceHelper instance;

    public static SharedPreferenceHelper getInstance() {
        if (instance == null) {
            instance = new SharedPreferenceHelper(MyApplication.getInstance());
        }
        return instance;
    }

    public SharedPreferenceHelper(Context context) {
        this.context = context;
        settings = context.getSharedPreferences(PREF_FILE, 0);
        editor = settings.edit();
    }


    public void setSharedPreferenceString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public void setSharedPreferenceInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public void setSharedPreferenceBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public String getSharedPreferenceString(String key, String defValue) {
        return settings.getString(key, defValue);
    }

    public int getSharedPreferenceInt(String key, int defValue) {
        return settings.getInt(key, defValue);
    }

    public boolean getSharedPreferenceBoolean(String key, boolean defValue) {
        return settings.getBoolean(key, defValue);
    }
}
