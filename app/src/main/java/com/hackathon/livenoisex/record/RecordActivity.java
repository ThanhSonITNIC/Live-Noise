package com.hackathon.livenoisex.record;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.hackathon.livenoisex.R;

public class RecordActivity extends AppCompatActivity {

    private static final int REQUEST_READ_WRITE = 33;
    private ImageView ivBack;
    private TextView tvNext;

    private int state;
    public static final int STATE_RECORD = 1;
    public static final int STATE_INFO = 2;

    public static final String KEY_DECIBEL = "decibel";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGTITUDE = "longtitude";
    public static final String KEY_CHILD_PATH = "child_path";

    private int decibel;
    private double latitude, longtitude;
    private FragmentRecord fragmentRecord;
    private FragmentRecordInfo fragmentRecordInfo;

    public static Intent getStartIntent(Context context, int decibel, double latitude, double longtitude){
        Intent intent = new Intent(context, RecordActivity.class);
        intent.putExtra(KEY_DECIBEL, decibel);
        intent.putExtra(KEY_LATITUDE, latitude);
        intent.putExtra(KEY_LONGTITUDE, longtitude);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ivBack = findViewById(R.id.iv_back);
        tvNext = findViewById(R.id.btn_next);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBack();
            }
        });

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNext();
            }
        });

        Intent intent = getIntent();
        decibel = intent.getIntExtra(RecordActivity.KEY_DECIBEL, 0);
        latitude = intent.getDoubleExtra(RecordActivity.KEY_LATITUDE, 0);
        longtitude = intent.getDoubleExtra(RecordActivity.KEY_LONGTITUDE, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_READ_WRITE);
        }
        showFragmentRecord();
    }

    private void showFragmentRecord() {
        state = STATE_RECORD;
        tvNext.setVisibility(View.VISIBLE);
        fragmentRecord = FragmentRecord.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.container, fragmentRecord)
                .commit();
    }

    private void onClickNext() {
        if (state == STATE_RECORD) {
            if(fragmentRecord.isRecorded()){
                showFragmentInfo();
            }
        }
    }

    private void showFragmentInfo() {
        state = STATE_INFO;
        tvNext.setVisibility(View.GONE);
        fragmentRecordInfo = FragmentRecordInfo.newInstance(decibel, latitude, longtitude, fragmentRecord.getChildPath());
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragmentRecordInfo)
                .addToBackStack(null)
                .commit();
    }

    private void onClickBack() {
        if(state == STATE_INFO){
            state = STATE_RECORD;
            tvNext.setVisibility(View.VISIBLE);
        }
        onBackPressed();
    }

}
