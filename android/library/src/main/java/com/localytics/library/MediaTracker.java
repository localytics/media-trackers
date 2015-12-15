package com.localytics.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mriegelhaupt on 12/14/15.
 */
public class MediaTracker {

    private Map<String, String> userDefinedAttributes = new HashMap<>();
    private double videoDurationMS;
    private double startedTime;
    private EventTagger tagger;
    private List<Range> rangesWatched = new ArrayList<>();

    private boolean didComplete = false;

    public static MediaTracker create(double videoDurationMS, EventTagger tagger) {
        return create(videoDurationMS, tagger, new HashMap<String, String>());
    }

    public static MediaTracker create(double videoDurationMS, EventTagger tagger, Map<String, String> userDefinedAttributes) {
        return new MediaTracker(videoDurationMS, tagger, userDefinedAttributes);
    }

    private MediaTracker(double videoDurationMS, EventTagger tagger, Map<String, String> userDefinedAttributes) {
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

    public void tagEventAtTime(double timeInMS) {
        mergeOrCreateRange(startedTime, timeInMS);
        tagEvent();
    }

    public void playAtTime(double timeInMS) {
        startedTime = timeInMS;
    }

    public void stopAtTime(double timeInMS) {
        mergeOrCreateRange(startedTime, timeInMS);
    }

    public void complete() {
        mergeOrCreateRange(startedTime, videoDurationMS);
        this.didComplete = true;
    }


    private void mergeOrCreateRange(double watchStart, double watchEnd) {
        boolean overlap = false;
        int i = 0;
        while (!overlap && i < rangesWatched.size()) {
            overlap = rangesWatched.get(i++).mergeIfOverlapping(watchStart, watchEnd);
        }
        if (!overlap) {
            rangesWatched.add(new Range(watchStart, watchEnd));
        }
    }

    private void tagEvent() {
        double timeWatchedMS = sumRanges();
        Map<String, String> videoAttributes = initMapWithUserAttributes();
        videoAttributes.put("Did Complete", didComplete ? "true" : "false");
        videoAttributes.put("Percent Played", String.valueOf((int) Math.round((timeWatchedMS / videoDurationMS) * 100)));
        videoAttributes.put("Media Length (seconds)", inSeconds(videoDurationMS));
        videoAttributes.put("Time Played (seconds)", inSeconds(timeWatchedMS));

        tagger.tagEvent("Media Played", videoAttributes);
    }


    private double sumRanges() {
        int sum = 0;
        for (Range r : rangesWatched) {
            sum += r.getDuration();
        }
        return sum;
    }

    private Map<String, String> initMapWithUserAttributes() {
        Map<String, String> attrs = new HashMap<>();
        for (Map.Entry<String, String> e : userDefinedAttributes.entrySet()) {
            attrs.put(e.getKey(), e.getValue());
        }
        return attrs;
    }

    private String inSeconds(double lengthInMS) {
        return String.valueOf(Math.round(lengthInMS * 0.001));
    }

    private class Range {

        private double start;
        private double end;

        public Range(double s, double e) {
            start = s;
            end = e;
        }

        public double getDuration() {
            return end - start;
        }

        boolean mergeIfOverlapping(double s, double e) {
            if (start <= s && s <= end && end < e) { //watched from somewhere in the middle to after the end
                end = e;
                return true;
            } else if (start > s && start <= e && end >= e) { //watched from somewhere before the range into the middle
                start = s;
                return true;
            } else if (start > s && end < e) { // watched portion is strictly larger on both ends
                start = s;
                end = e;
                return true;
            } else if (start <= s && end >= e) { //complete overlap
                return true;
            }
            return false;
        }
    }
}
