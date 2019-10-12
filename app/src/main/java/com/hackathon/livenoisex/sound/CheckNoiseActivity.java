package com.hackathon.livenoisex.sound;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.hackathon.livenoisex.R;
import com.hackathon.livenoisex.main.MainActivity;
import com.hackathon.livenoisex.models.Device;
import com.hackathon.livenoisex.models.SoundModel;
import com.hackathon.livenoisex.record.RecordActivity;
import com.hackathon.livenoisex.utils.RecordPermissionHelper;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CheckNoiseActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 22;
    private ProgressBar progressBar;
    private TextView tvDecibel, btnAction, txtResult, btnReport, btnViewMap;
    private SoundMeter soundMeter = null;
    private GetSoundThread getSoundThread;
    private List<Double> decibelBuffer = new CopyOnWriteArrayList<>();
    private Location mLastKnownLocation;
    private SoundModel soundModel = new SoundModel();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;
    private int decibelValue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_noise);

        progressBar = findViewById(R.id.progressBar);
        tvDecibel = findViewById(R.id.tv_decibel);
        btnAction = findViewById(R.id.btn_action);
        txtResult = findViewById(R.id.txt_result);
        btnReport = findViewById(R.id.btn_report);
        btnViewMap = findViewById(R.id.btn_view_map);

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(RecordActivity.getStartIntent(CheckNoiseActivity.this,
                        decibelValue,
                        mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
            }
        });

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecord();
            }
        });
        btnViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CheckNoiseActivity.this, MainActivity.class));
            }
        });
        if (!RecordPermissionHelper.hasRecordPermission(this)) {
            RecordPermissionHelper.requestRecordPermission(this);
        }
        getLocationPermission();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getDeviceLocation();


    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();

                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void startRecord() {
        if (!RecordPermissionHelper.hasRecordPermission(this)) {
            RecordPermissionHelper.requestRecordPermission(this);
            return;
        }
        btnAction.setClickable(false);
        progressBar.setVisibility(View.VISIBLE);
        txtResult.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        tvDecibel.setText(R.string.loading);
        tvDecibel.setTextSize(24);
        tvDecibel.setTextColor(getResources().getColor(R.color.text_color_white));

        if (soundMeter == null) {
            soundMeter = new SoundMeter();
        }

        soundMeter.start();

        getSoundThread = new GetSoundThread();
        getSoundThread.start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRecord();
                showResult();
            }
        }, 5000);
    }

    public void showResult() {
        progressBar.setVisibility(View.INVISIBLE);
        txtResult.setVisibility(View.VISIBLE);
        decibelValue = (int) getDecibelValue();

        if (decibelValue < 20) {
            tvDecibel.setTextColor(getResources().getColor(R.color.text_color_white));
            txtResult.setText(R.string.result_1);
        } else if (decibelValue < 80) {
            tvDecibel.setTextColor(getResources().getColor(R.color.text_color_blue));
            txtResult.setText(R.string.result_2);
        } else if (decibelValue < 90) {
            btnReport.setVisibility(View.VISIBLE);
            tvDecibel.setTextColor(getResources().getColor(R.color.text_color_orange));
            txtResult.setText(R.string.result_3);
        } else {
            btnReport.setVisibility(View.VISIBLE);
            txtResult.setText(R.string.result_4);
            tvDecibel.setTextColor(getResources().getColor(R.color.text_color_red));
        }

        tvDecibel.setText(decibelValue + "");

        btnReport.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='http://www.google.com'> Report </a>";
        btnReport.setText(Html.fromHtml(text));

        btnViewMap.setVisibility(View.VISIBLE);

        btnAction.setClickable(true);
        if (mLastKnownLocation != null) {
            soundModel.addInsensity(new Device(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), decibelValue));
        }
    }

    private double getDecibelValue() {
        double sum = 0;
        int n = decibelBuffer.size();
        for (int i = 0; i < n; ++i) {
            sum += decibelBuffer.get(i);
        }
        return (sum / n);
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                );
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getDeviceLocation();
                }
                break;
            }
            default:
                startRecord();
                break;
        }

    }

    @Override
    protected void onDestroy() {
        stopRecord();
        super.onDestroy();
    }

    private void stopRecord() {
        btnAction.setClickable(true);
        if (soundMeter != null) {
            soundMeter.stop();
        }

        if (getSoundThread != null && getSoundThread.isRunning()) {
            getSoundThread.stopRunning();
        }
    }

    class GetSoundThread extends Thread {
        private volatile boolean flag = true;

        public void stopRunning() {
            flag = false;
        }

        public boolean isRunning() {
            return flag;
        }

        @Override
        public void run() {
            super.run();
            while (flag) {
                double decibel = soundMeter.getDecibel();
                if (decibel > 0) {
                    decibelBuffer.add(decibel);
                    final String s = String.valueOf((int) decibel);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvDecibel.setTextSize(48.0f);
                            tvDecibel.setText(s);
                        }
                    });
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
