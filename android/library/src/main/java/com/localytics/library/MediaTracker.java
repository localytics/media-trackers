package com.localytics.library;

/**
 * Created by mriegelhaupt on 12/14/15.
 */
public interface MediaTracker {

    void tagEventAtTime(double timeInMilliseconds);

    void playAtTime(double timeInMilliseconds);

    void stopAtTime(double timeInMilliseconds);

    void complete();
}

