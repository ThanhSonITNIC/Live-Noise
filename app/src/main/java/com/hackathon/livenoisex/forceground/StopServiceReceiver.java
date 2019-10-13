package com.hackathon.livenoisex.forceground;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StopServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, DeepSoundListener.class);
        context.stopService(service);
    }

}