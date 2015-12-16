package com.localytics.library;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class RangeTest {

    @Test
    public void testCompleteOverlap() throws Exception {
        Range r = new Range(0, 1000);
        boolean overlap = r.mergeIfOverlapping(50, 100);
        assertTrue(overlap);
        assertEquals(r.getStart(), 0);
        assertEquals(r.getEnd(), 1000);
    }

    @Test
    public void testLeftOverlap() throws Exception {
        Range r = new Range(50, 1000);
        boolean overlap = r.mergeIfOverlapping(0, 100);
        assertTrue(overlap);
        assertEquals(r.getStart(), 0);
        assertEquals(r.getEnd(), 1000);
    }

    @Test
    public void testRightOverlap() throws Exception {
        Range r = new Range(0, 1000);
        boolean overlap = r.mergeIfOverlapping(100, 1050);
        assertTrue(overlap);
        assertEquals(r.getStart(), 0);
        assertEquals(r.getEnd(), 1050);
    }

    @Test
    public void testFullyLarger() throws Exception {
        Range r = new Range(50, 1000);
        boolean overlap = r.mergeIfOverlapping(0, 1050);
        assertTrue(overlap);
        assertEquals(r.getStart(), 0);
        assertEquals(r.getEnd(), 1050);
    }

    @Test
    public void testNoOverlap() throws Exception {
        Range r = new Range(0, 1000);
        boolean overlap = r.mergeIfOverlapping(1050, 2000);
        assertFalse(overlap);
        assertEquals(r.getStart(), 0);
        assertEquals(r.getEnd(), 1000);
    }

}