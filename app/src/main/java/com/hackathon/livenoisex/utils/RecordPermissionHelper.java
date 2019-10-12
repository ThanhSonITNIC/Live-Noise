package com.hackathon.livenoisex.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class RecordPermissionHelper {
    private static final int RECORD_PERMISSION_CODE = 0;
    private static final String RECORD_PERMISSION = Manifest.permission.RECORD_AUDIO;

    public static boolean hasRecordPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, RECORD_PERMISSION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestRecordPermission(Activity activity) {
        ActivityCompat.requestPermissions(
                activity, new String[] {RECORD_PERMISSION}, RECORD_PERMISSION_CODE);
    }

    public static boolean shouldShowRequestPermissionRationale(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, RECORD_PERMISSION);
    }

    public static void launchPermissionSettings(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(intent);
    }
}
