package com.localytics.library;

/**
 * Created by mriegelhaupt on 12/16/15.
 */
class Range {

    private int start;
    private int end;

    public Range(int s, int e) {
        start = s;
        end = e;
    }

    public int getDuration() {
        return end - start;
    }

    int getStart() {
        return start;
    }

    int getEnd() {
        return end;
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
