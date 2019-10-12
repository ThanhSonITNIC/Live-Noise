package com.hackathon.livenoisex.utils;

import android.content.Context;

public class Utils {

    private static Utils instance;
    private Context mContext;

    public Utils(Context context) {
        this.mContext = context;
    }

    public void init(Context context){
        instance = new Utils(context);
    }

    public static Utils shared() {
        return instance;
    }

    public float convertDpToPx(float size) {
        return size * mContext.getResources().getDisplayMetrics().density;
    }
}
