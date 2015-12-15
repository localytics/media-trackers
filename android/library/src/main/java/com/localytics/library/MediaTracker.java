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
    private int videoDurationMS;
    private int startedTime;
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
        mergeOrCreateRange(startedTime, timeInMS);
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
        int timeWatchedMS = sumRanges();
        Map<String, String> videoAttributes = new HashMap<>(userDefinedAttributes);
        videoAttributes.put("Did Complete", didComplete ? "true" : "false");
        videoAttributes.put("Percent Played", String.valueOf(Math.round((timeWatchedMS / videoDurationMS) * 100)));
        videoAttributes.put("Media Length (seconds)", inSeconds(videoDurationMS));
        videoAttributes.put("Time Played (seconds)", inSeconds(timeWatchedMS));

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

    private class Range {

        private int start;
        private int end;

        public Range(int s, int e) {
            start = s;
            end = e;
        }

        public int getDuration() {
            return end - start;
        }

        boolean mergeIfOverlapping(int s, int e) {
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
