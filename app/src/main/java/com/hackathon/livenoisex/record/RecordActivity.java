package com.hackathon.livenoisex.record;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.hackathon.livenoisex.R;

public class RecordActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvNext;

    private int state;
    public static final int STATE_RECORD = 1;
    public static final int STATE_INFO = 2;

    public static final String KEY_DECIBEL = "decibel";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGTITUDE = "longtitude";

    private int decibel;
    private double latitude, longtitude;

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

        showFragmentInfo();
    }

    private void showFragmentRecord() {
        state = STATE_RECORD;
        tvNext.setVisibility(View.VISIBLE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.container, FragmentRecord.newInstance())
                .commit();
    }

    private void onClickNext() {
        if (state == STATE_RECORD) {
            showFragmentInfo();
        }
    }

    private void showFragmentInfo() {
        state = STATE_INFO;
        tvNext.setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.container, FragmentRecordInfo.newInstance(decibel, latitude, longtitude))
                .commit();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

            }
        });
    }

    private void onClickBack() {
        onBackPressed();
    }

}
