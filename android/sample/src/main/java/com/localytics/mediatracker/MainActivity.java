package com.localytics.mediatracker;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;

import com.localytics.library.MediaTracker;
import com.localytics.library.MediaTrackerImpl;

public class MainActivity extends FragmentActivity {

    private TrackedVideoView videoView;
    private MediaTracker mediaTracker;
    private MediaController mediaController;
    private ProgressDialog progressDialog;
    private int position = 0;

    private static String VIDEO_URL = "http://www.androidbegin.com/tutorial/AndroidCommercial.3gp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mediaController == null) {
            mediaController = new MediaController(this);
        }

        videoView = (TrackedVideoView) findViewById(R.id.video_view);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Video File");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(Uri.parse(VIDEO_URL));
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaTracker = MediaTrackerImpl.create(videoView.getDuration());
                videoView.setMediaTracker(mediaTracker);
                progressDialog.dismiss();
                videoView.seekTo(position);
                if (position == 0) {
                    videoView.start();
                } else {
                    videoView.pause();
                }
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaTracker.tagEventAtTime(videoView.getDuration());
            }
        });

        Button tagButton = (Button) findViewById(R.id.tag_button);
        tagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaTracker != null) {
                    mediaTracker.tagEventAtTime(videoView.getCurrentPosition());
                }

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", videoView.getCurrentPosition());
        videoView.pause();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt("position");
        videoView.seekTo(position);
    }

}
