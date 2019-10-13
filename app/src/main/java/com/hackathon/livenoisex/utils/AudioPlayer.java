package com.hackathon.livenoisex.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.IOException;

public class AudioPlayer {

    private MediaPlayer mMediaPlayer;

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void play(Context c, String url, final AudioPlayerEvent audioPlayerEvent) {
        stop();

        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(url);

        } catch (IOException e) {
            Toast.makeText(c, "Audio url invalid", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
                if (audioPlayerEvent != null)
                    audioPlayerEvent.onCompleted();
            }
        });

        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer.start();
            }
        });
    }

    public int getAudioSessionId() {
        if (mMediaPlayer == null)
            return -1;
        return mMediaPlayer.getAudioSessionId();
    }

    public interface AudioPlayerEvent {
        void onCompleted();
    }

}
