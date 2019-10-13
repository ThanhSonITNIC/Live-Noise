package com.hackathon.livenoisex.forceground;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.maps.model.LatLng;
import com.google.protobuf.Internal;
import com.hackathon.livenoisex.R;
import com.hackathon.livenoisex.main.MainActivity;
import com.hackathon.livenoisex.models.Device;
import com.hackathon.livenoisex.models.SoundModel;
import com.hackathon.livenoisex.sound.CheckNoiseActivity;
import com.hackathon.livenoisex.sound.SoundMeter;

import java.util.ArrayList;
import java.util.List;

public class DeepSoundListener extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final String KEY_BUTTON_EXIT = "btnExit";
    private static final String STOPFOREGROUND_ACTION = "ActionStop";
    private Device device;
    public static Location location;

    public DeepSoundListener(){

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("xxx", String.valueOf(flags));
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, StopServiceReceiver.class);
        notificationIntent.setAction(STOPFOREGROUND_ACTION);

        PendingIntent pendingIntent = PendingIntent.getService(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Live Noise")
                .setContentText("Đang chia sẻ cường độ âm thanh")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
//                .addAction(R.drawable.bg_button_white,"Dừng", pendingIntent)
                .build();

        startForeground(1, notification);

        //do heavy work on a background thread
        soundListen();

        return START_NOT_STICKY;
    }

    private void stopForegroundService() {

        stopForeground(true);
        stopSelf();
    }

    private void soundListen() {
        final SoundMeter soundMeter = new SoundMeter();
        final SoundModel soundModel = new SoundModel();
        soundMeter.start();

        final ArrayList<Integer> intensities = new ArrayList<>();

        final CheckNoiseActivity checkNoiseActivity = new CheckNoiseActivity();
        checkNoiseActivity.getDeviceLocation(null);


        // update
        new CountDownTimer(Long.MAX_VALUE, 30000) {
            public void onTick(long millisUntilFinished) {
                new CountDownTimer(29 * 1000, 1000){

                    @Override
                    public void onTick(long millisUntilFinished) {
                        int i = (int) soundMeter.getDecibel();
                        if(i > 0){
                            intensities.add(i);
                        }
                    }

                    @Override
                    public void onFinish() {
                        device = new Device(location.getLatitude(), location.getLongitude(), (int) calculateAverage(intensities));
                        soundModel.updateInsensity(device);
                        intensities.clear();
                    }
                }.start();
            }

            public void onFinish() {
                this.start();
            }
        }.start();

    }

    private double calculateAverage(List <Integer> marks) {
        Integer sum = 0;
        if(!marks.isEmpty()) {
            for (Integer mark : marks) {
                sum += mark;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}

