package com.hackathon.livenoisex.sound;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hackathon.livenoisex.R;
import com.hackathon.livenoisex.main.MainActivity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RecordActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView tvDecibel, btnAction, txtResult, btnReport;
    private SoundMeter soundMeter = null;
    private GetSoundThread getSoundThread;
    private List<Double> decibelBuffer = new CopyOnWriteArrayList<>();
    private int bufferIndex = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        progressBar = findViewById(R.id.progressBar);
        tvDecibel = findViewById(R.id.tv_decibel);
        btnAction = findViewById(R.id.btn_action);
        txtResult = findViewById(R.id.txt_result);
        btnReport = findViewById(R.id.btn_report);


        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecord();
            }
        });

        if (!RecordPermissionHelper.hasRecordPermission(this)) {
            RecordPermissionHelper.requestRecordPermission(this);
        }


    }

    private void startRecord() {
        if (!RecordPermissionHelper.hasRecordPermission(this)) {
            RecordPermissionHelper.requestRecordPermission(this);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvDecibel.setVisibility(View.VISIBLE);

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
        }, 10000);
    }

    public void showResult() {
        progressBar.setVisibility(View.INVISIBLE);
        txtResult.setVisibility(View.VISIBLE);

        tvDecibel.setText(getDecibelValue() + "");

        btnReport.setVisibility(View.VISIBLE);
        btnReport.setClickable(true);
        btnReport.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='http://www.google.com'> Report </a>";
        btnReport.setText(Html.fromHtml(text));

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecordActivity.this, MainActivity.class));
                finish();
            }
        });
        btnAction.setText(R.string.view_noise_map);
    }

    private int getDecibelValue() {
        double sum = 0;
        int n = decibelBuffer.size();
        for (int i = 0; i < n; ++i) {
            sum += decibelBuffer.get(i);
        }
        return (int) (sum / n);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startRecord();

    }

    @Override
    protected void onDestroy() {
        stopRecord();
        super.onDestroy();
    }

    private void stopRecord() {
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
