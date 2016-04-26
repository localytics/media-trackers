package com.localytics.library;

import java.util.Map;

/**
 * Created by mriegelhaupt on 12/14/15.
 */
public interface EventTagger {

    void tagEvent(String eventName, Map<String, String> videoAttributes);

}
