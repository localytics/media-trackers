package com.localytics.mediatracker;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.widget.VideoView;

import com.localytics.library.MediaTracker;

/**
 * Created by mriegelhaupt on 12/14/15.
 */
public class TrackedVideoView extends VideoView {

    private MediaTracker mediaTracker;
    private double startTime = 0;

    public TrackedVideoView(Context context) {
        super(context);
        setup();
    }

    public TrackedVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public TrackedVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    public void setMediaTracker(MediaTracker mt) {
        mediaTracker = mt;
    }

    @Override
    public void setOnCompletionListener(final MediaPlayer.OnCompletionListener l) {
        super.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                completionHook();
                l.onCompletion(mp);
            }
        });
    }

    @Override
    public void start() {
        super.start();
        mediaTracker.playAtTime(startTime);
    }

    @Override
    public void pause() {
        super.pause();
        startTime = getCurrentPosition();
        mediaTracker.stopAtTime(startTime);
    }

    @Override
    public void resume() {
        super.resume();
        mediaTracker.playAtTime(0);
    }

    @Override
    public void seekTo(int timeInMS) {
        super.seekTo(timeInMS);
        startTime = timeInMS;
    }

    private void setup() {
        super.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                completionHook();
            }
        });
    }

    private void completionHook() {
        mediaTracker.complete();
    }

}