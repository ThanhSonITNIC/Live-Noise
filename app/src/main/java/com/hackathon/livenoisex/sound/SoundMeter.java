package com.hackathon.livenoisex.sound;

import android.media.MediaRecorder;

import java.io.IOException;

public class SoundMeter {
    // Amplitude reference
    static final private double EMA_FILTER = 0.6;
    private static final int BUFFER_RANGE = 256;

    private MediaRecorder mRecorder = null;
    private int[] amplitude = new int[BUFFER_RANGE];
    private double mEMA = 0.0;
    private boolean isRunning = false;

    public void start() {

        if (mRecorder == null) {

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try {
                mRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mRecorder.start();
            isRunning = true;
            mEMA = 0.0;
        }
    }

    public void stop() {
        if (mRecorder != null) {
            isRunning = false;
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public double getAmplitude() {
        if (mRecorder != null && isRunning)
            return mRecorder.getMaxAmplitude();
        else
            return 0;

    }

    public double getAmplitudeEMA() {
        double amp = getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }

    public double getDecibel(){
        return 20 * Math.log10(getAmplitudeEMA()/EMA_FILTER);
    }

}
