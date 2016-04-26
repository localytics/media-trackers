package com.localytics.library;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    public void testStopBeforeStartFailure() throws Exception {
        MediaTracker mediaTracker = MediaTracker.create(1000000, new EventTagger() {
            @Override
            public void tagEvent(String eventName, Map<String, String> videoAttributes) { }
        });
        boolean exceptionCaught = false;
        try {
            mediaTracker.stopAtTime(500000);
        } catch (Exception e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }

    @Test
    public void testStopAfterMediaLengthFailure() throws Exception {
        MediaTracker mediaTracker = MediaTracker.create(1000000, new EventTagger() {
            @Override
            public void tagEvent(String eventName, Map<String, String> videoAttributes) { }
        });
        boolean exceptionCaught = false;
        try {
            mediaTracker.playAtTime(0);
            mediaTracker.stopAtTime(5000000);
        } catch (Exception e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }


}
