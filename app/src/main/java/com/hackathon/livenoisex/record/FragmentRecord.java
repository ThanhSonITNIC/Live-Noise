package com.hackathon.livenoisex.record;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hackathon.livenoisex.R;
import com.taishi.library.Indicator;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class FragmentRecord extends Fragment implements View.OnClickListener {

    private boolean recorded;
    private String childPath = new Date().getTime()+"_ln_record.mp3";

    public static FragmentRecord newInstance() {
        return new FragmentRecord();
    }

    private MediaRecorder mediaRecorder;
    private File audioFile;

    private ImageView btnStart, btnStop;
    private Indicator viewIndicator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recorder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnStart = view.findViewById(R.id.btn_start);
        btnStop = view.findViewById(R.id.btn_stop);
        viewIndicator = view.findViewById(R.id.indicator);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        btnStop.setVisibility(View.GONE);
        audioFile = new File(Environment.getExternalStorageDirectory(),
                childPath);
    }

    // this process must be done prior to the start of recording
    private void resetRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setAudioEncodingBitRate(16);
        mediaRecorder.setAudioSamplingRate(44100);
        mediaRecorder.setOutputFile(audioFile.getAbsolutePath());

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                resetRecorder();
                try{
                    mediaRecorder.start();
                }catch (IllegalStateException e){
                    e.printStackTrace();
                    return;
                }

                recorded = true;
                viewIndicator.setVisibility(View.VISIBLE);
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                btnStart.setVisibility(View.GONE);
                btnStop.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_stop:
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                viewIndicator.setVisibility(View.GONE);

                btnStart.setEnabled(true);
                btnStop.setEnabled(false);

                btnStart.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.GONE);
                ((RecordActivity)getActivity()).showFragmentInfo();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            recorded = false;
        }
        viewIndicator.setVisibility(View.GONE);
    }

    public boolean isRecorded() {
        return recorded;
    }

    public String getChildPath() {
        return childPath;
    }
}
