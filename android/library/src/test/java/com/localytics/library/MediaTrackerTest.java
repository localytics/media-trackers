package com.localytics.library;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by mriegelhaupt on 12/16/15.
 */
public class MediaTrackerTest {

    @Test
    public void testBasicFunctionality() throws Exception {
        MediaTracker mediaTracker = MediaTracker.create(1000000, new EventTagger() {
            @Override
            public void tagEvent(String eventName, Map<String, String> videoAttributes) {
                assertEquals("false", videoAttributes.get(MediaTracker.DID_COMPLETE));
                assertEquals("1000", videoAttributes.get(MediaTracker.MEDIA_LENGTH_SECONDS));
                assertEquals("50", videoAttributes.get(MediaTracker.TIME_PLAYED_SECONDS));
                assertEquals("5", videoAttributes.get(MediaTracker.PERCENT_PLAYED));
            }
        });

        mediaTracker.playAtTime(0);
        mediaTracker.tagEventAtTime(50000);
    }

    @Test
    public void testOverlapAccumulation() throws Exception {
        MediaTracker mediaTracker = MediaTracker.create(1000000, new EventTagger() {
            @Override
            public void tagEvent(String eventName, Map<String, String> videoAttributes) {
                assertEquals("false", videoAttributes.get(MediaTracker.DID_COMPLETE));
                assertEquals("1000", videoAttributes.get(MediaTracker.MEDIA_LENGTH_SECONDS));
                assertEquals("750", videoAttributes.get(MediaTracker.TIME_PLAYED_SECONDS));
                assertEquals("75", videoAttributes.get(MediaTracker.PERCENT_PLAYED));
            }
        });

        mediaTracker.playAtTime(0);
        mediaTracker.stopAtTime(500000);
        mediaTracker.playAtTime(250000);
        mediaTracker.tagEventAtTime(750000);
    }

    @Test
    public void testCompletion() throws Exception {
        MediaTracker mediaTracker = MediaTracker.create(1000000, new EventTagger() {
            @Override
            public void tagEvent(String eventName, Map<String, String> videoAttributes) {
                assertEquals("true", videoAttributes.get(MediaTracker.DID_COMPLETE));
                assertEquals("1000", videoAttributes.get(MediaTracker.MEDIA_LENGTH_SECONDS));
                assertEquals("1000", videoAttributes.get(MediaTracker.TIME_PLAYED_SECONDS));
                assertEquals("100", videoAttributes.get(MediaTracker.PERCENT_PLAYED));
            }
        });

        mediaTracker.playAtTime(0);
        mediaTracker.stopAtTime(500000);
        mediaTracker.playAtTime(250000);
        mediaTracker.complete();
        mediaTracker.tagEventAtTime(1000000);
    }

    @Test
    public void testOverlapWithCompletion() throws Exception {
        MediaTracker mediaTracker = MediaTracker.create(1000000, new EventTagger() {
            @Override
            public void tagEvent(String eventName, Map<String, String> videoAttributes) {
                assertEquals("true", videoAttributes.get(MediaTracker.DID_COMPLETE));
                assertEquals("1000", videoAttributes.get(MediaTracker.MEDIA_LENGTH_SECONDS));
                assertEquals("750", videoAttributes.get(MediaTracker.TIME_PLAYED_SECONDS));
                assertEquals("75", videoAttributes.get(MediaTracker.PERCENT_PLAYED));
            }
        });

        mediaTracker.playAtTime(250000);
        mediaTracker.stopAtTime(500000);
        mediaTracker.playAtTime(300000);
        mediaTracker.tagEventAtTime(1000000);
    }


    @Test
    public void testDoubleOverlap() throws Exception {
        MediaTracker mediaTracker = MediaTracker.create(1000000, new EventTagger() {
            @Override
            public void tagEvent(String eventName, Map<String, String> videoAttributes) {
                assertEquals("1000", videoAttributes.get(MediaTracker.MEDIA_LENGTH_SECONDS));
                assertEquals("450", videoAttributes.get(MediaTracker.TIME_PLAYED_SECONDS));
                assertEquals("45", videoAttributes.get(MediaTracker.PERCENT_PLAYED));
            }
        });

        //range 1
        mediaTracker.playAtTime(50000);
        mediaTracker.stopAtTime(150000);
        //range 2
        mediaTracker.playAtTime(300000);
        mediaTracker.stopAtTime(500000);
        //overlaps both ranges
        mediaTracker.playAtTime(100000);
        mediaTracker.stopAtTime(350000);

        mediaTracker.tagEventAtTime(500000);
    }

    @Test
    public void testCompleteOverlap() throws Exception {
        MediaTracker mediaTracker = MediaTracker.create(1000000, new EventTagger() {
            @Override
            public void tagEvent(String eventName, Map<String, String> videoAttributes) {
                assertEquals("1000", videoAttributes.get(MediaTracker.MEDIA_LENGTH_SECONDS));
                assertEquals("800", videoAttributes.get(MediaTracker.TIME_PLAYED_SECONDS));
                assertEquals("80", videoAttributes.get(MediaTracker.PERCENT_PLAYED));
            }
        });

        mediaTracker.playAtTime(50000);
        mediaTracker.stopAtTime(150000);

        mediaTracker.playAtTime(300000);
        mediaTracker.stopAtTime(500000);

        mediaTracker.playAtTime(600000);
        mediaTracker.stopAtTime(750000);

        //overlaps all previous ranges
        mediaTracker.playAtTime(0);
        mediaTracker.stopAtTime(800000);

        mediaTracker.tagEventAtTime(500000);
    }


}
