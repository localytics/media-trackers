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
        MediaTracker mediaTracker = MediaTracker.create(1000, new EventTagger() {
            @Override
            public void tagEvent(String eventName, Map<String, String> videoAttributes) {
                assertEquals("false", videoAttributes.get(MediaTracker.DID_COMPLETE));
                assertEquals("1000", videoAttributes.get(MediaTracker.MEDIA_LENGTH_SECONDS));
                assertEquals("50", videoAttributes.get(MediaTracker.TIME_PLAYED_SECONDS));
                assertEquals("5", videoAttributes.get(MediaTracker.PERCENT_PLAYED));
            }
        });

        mediaTracker.playAtTime(0);
        mediaTracker.stopAtTime(50);
    }

    @Test
    public void testOverlapAccumulation() throws Exception {
        MediaTracker mediaTracker = MediaTracker.create(1000, new EventTagger() {
            @Override
            public void tagEvent(String eventName, Map<String, String> videoAttributes) {
                assertEquals("false", videoAttributes.get(MediaTracker.DID_COMPLETE));
                assertEquals("1000", videoAttributes.get(MediaTracker.MEDIA_LENGTH_SECONDS));
                assertEquals("750", videoAttributes.get(MediaTracker.TIME_PLAYED_SECONDS));
                assertEquals("75", videoAttributes.get(MediaTracker.PERCENT_PLAYED));
            }
        });

        mediaTracker.playAtTime(0);
        mediaTracker.stopAtTime(500);
        mediaTracker.playAtTime(250);
        mediaTracker.stopAtTime(750);
    }

    @Test
    public void testCompletion() throws Exception {
        MediaTracker mediaTracker = MediaTracker.create(1000, new EventTagger() {
            @Override
            public void tagEvent(String eventName, Map<String, String> videoAttributes) {
                assertEquals("true", videoAttributes.get(MediaTracker.DID_COMPLETE));
                assertEquals("1000", videoAttributes.get(MediaTracker.MEDIA_LENGTH_SECONDS));
                assertEquals("1000", videoAttributes.get(MediaTracker.TIME_PLAYED_SECONDS));
                assertEquals("100", videoAttributes.get(MediaTracker.PERCENT_PLAYED));
            }
        });

        mediaTracker.playAtTime(0);
        mediaTracker.stopAtTime(500);
        mediaTracker.playAtTime(250);
        mediaTracker.complete();
    }

    @Test
    public void testOverlapWithCompletion() throws Exception {
        MediaTracker mediaTracker = MediaTracker.create(1000, new EventTagger() {
            @Override
            public void tagEvent(String eventName, Map<String, String> videoAttributes) {
                assertEquals("true", videoAttributes.get(MediaTracker.DID_COMPLETE));
                assertEquals("1000", videoAttributes.get(MediaTracker.MEDIA_LENGTH_SECONDS));
                assertEquals("750", videoAttributes.get(MediaTracker.TIME_PLAYED_SECONDS));
                assertEquals("75", videoAttributes.get(MediaTracker.PERCENT_PLAYED));
            }
        });

        mediaTracker.playAtTime(250);
        mediaTracker.stopAtTime(500);
        mediaTracker.playAtTime(300);
        mediaTracker.complete();
    }
}
