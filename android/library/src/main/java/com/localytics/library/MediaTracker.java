package com.localytics.library;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mriegelhaupt on 12/14/15.
 */
public class MediaTracker {

    static final String DID_COMPLETE = "Did Complete";
    static final String PERCENT_PLAYED = "Percent Played";
    static final String MEDIA_LENGTH_SECONDS = "Media Length (seconds)";
    static final String TIME_PLAYED_SECONDS = "Time Played (seconds)";

    private Map<String, String> userDefinedAttributes = new HashMap<>();
    private int videoDurationMS;
    private int startedTime = -1;
    private EventTagger tagger;
    private List<Range> rangesWatched = new ArrayList<>();

    private boolean didComplete = false;

    public static MediaTracker create(int videoDurationMS, EventTagger tagger) {
        return create(videoDurationMS, tagger, new HashMap<String, String>());
    }

    public static MediaTracker create(int videoDurationMS, EventTagger tagger, Map<String, String> userDefinedAttributes) {
        return new MediaTracker(videoDurationMS, tagger, userDefinedAttributes);
    }

    private MediaTracker(int videoDurationMS, EventTagger tagger, Map<String, String> userDefinedAttributes) {
        this.videoDurationMS = videoDurationMS;
        this.tagger = tagger;
        if (tagger == null) {
            throw new IllegalArgumentException("You must provide an EventTagger.");
        }
        if (userDefinedAttributes == null) {
            userDefinedAttributes = new HashMap<>();
        }
        this.userDefinedAttributes = userDefinedAttributes;
    }

    public void tagEventAtTime(int timeInMS) {
        if (timeInMS == videoDurationMS) {
            didComplete = true;
        }
        if (startedTime >= 0) {
            mergeOrCreateRange(startedTime, timeInMS);
        }
        tagEvent();
    }

    public void playAtTime(int timeInMS) {
        startedTime = timeInMS;
    }

    public void stopAtTime(int timeInMS) {
        mergeOrCreateRange(startedTime, timeInMS);
    }

    public void complete() {
        mergeOrCreateRange(startedTime, videoDurationMS);
        this.didComplete = true;
    }


    private void mergeOrCreateRange(int watchStart, int watchEnd) {
        if (watchStart < 0) {
            Log.i("LocalyticsMediaTracker", "Media Tracker dropping stop datapoint.  A stop was " +
                    "called before a start, this can occur when a start was never called or stop " +
                    "was called multiple times in a row.");
            return;
        }
        if (watchEnd > videoDurationMS) {
            Log.i("LocalyticsMediaTracker", "Media Tracker dropping stop datapoint.  A stop was " +
                    "called with a time that is larger than the media duration.");
            return;
        }
        boolean overlap = false;
        int i = 0;
        while (!overlap && i < rangesWatched.size()) {
            Range range = rangesWatched.get(i++);
            overlap = range.mergeIfOverlapping(watchStart, watchEnd);
            if (overlap) {
                removeOverlappingRanges(range);
            }
        }
        if (!overlap) {
            rangesWatched.add(new Range(watchStart, watchEnd));
        }
        startedTime = -1;
    }

    private void removeOverlappingRanges(Range input)
    {
        List<Range> toRemove = new ArrayList<>();
        for (int i = 0; i < rangesWatched.size(); i++) {
            Range range = rangesWatched.get(i);
            if (range != input) { //avoid merging myself
                if (input.mergeIfOverlapping(range.getStart(), range.getEnd())) {
                    toRemove.add(range);
                }
            }
        }
        rangesWatched.removeAll(toRemove);
    }

    private void tagEvent() {
        int timeWatchedMS = sumRanges();
        Map<String, String> videoAttributes = new HashMap<>(userDefinedAttributes);
        videoAttributes.put(DID_COMPLETE, didComplete ? "true" : "false");
        videoAttributes.put(PERCENT_PLAYED, String.valueOf(Math.round(((double) timeWatchedMS / videoDurationMS) * 100)));
        videoAttributes.put(MEDIA_LENGTH_SECONDS, inSeconds(videoDurationMS));
        videoAttributes.put(TIME_PLAYED_SECONDS, inSeconds(timeWatchedMS));

        tagger.tagEvent("Media Played", videoAttributes);
    }


    private int sumRanges() {
        int sum = 0;
        for (Range r : rangesWatched) {
            sum += r.getDuration();
        }
        return sum;
    }

    private String inSeconds(int lengthInMS) {
        return String.valueOf(Math.round(lengthInMS / 1000));
    }

}
