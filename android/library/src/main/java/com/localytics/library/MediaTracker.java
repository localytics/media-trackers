package com.localytics.library;

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
            throw new IllegalStateException("The video can't be tagged as stopped before it is tagged as started.");
        }
        if (watchEnd > videoDurationMS || watchStart > videoDurationMS) {
            throw new IllegalArgumentException("The time input is greater than the duration of the media.");
        }
        boolean overlap = false;
        int i = 0;
        while (!overlap && i < rangesWatched.size()) {
            overlap = rangesWatched.get(i++).mergeIfOverlapping(watchStart, watchEnd);
        }
        if (!overlap) {
            rangesWatched.add(new Range(watchStart, watchEnd));
        }
        startedTime = -1;
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
