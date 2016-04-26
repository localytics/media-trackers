package com.localytics.mediatracker;

import android.app.Application;

import com.localytics.android.Localytics;
import com.localytics.android.LocalyticsActivityLifecycleCallbacks;

/**
 * Created by mriegelhaupt on 12/14/15.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Localytics.setLoggingEnabled(true);
        registerActivityLifecycleCallbacks(new LocalyticsActivityLifecycleCallbacks(this));
    }
}
