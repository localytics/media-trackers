package com.localytics.library;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by mriegelhaupt on 12/14/15.
 */
public class TrackedVideoView extends VideoView {

    private MediaTracker mediaTracker;
    private int startTime = 0;

    public TrackedVideoView(Context context) {
        this(context, null);
    }

    public TrackedVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrackedVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaTracker.complete();
            }
        });
    }

    public void setMediaTracker(MediaTracker mt) {
        mediaTracker = mt;
    }

    @Override
    public void setOnCompletionListener(final MediaPlayer.OnCompletionListener l) {
        super.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaTracker.complete();
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

}