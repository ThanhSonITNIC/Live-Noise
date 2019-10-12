package com.hackathon.livenoisex;

import android.app.Application;
import android.content.Context;

import com.hackathon.livenoisex.utils.SharedPreferenceHelper;

public class MyApplication extends Application {

    public static String idDevice;

    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        idDevice = SharedPreferenceHelper.getInstance()
                .getSharedPreferenceString(SharedPreferenceHelper.KEY_DEVICE_ID, "");
    }
}
