package com.sondertara.common.datetime;

import java.util.TimeZone;

/**
 *  */
public interface TimeZoneAware {
    TimeZone getTimeZone();

    void setTimeZone(TimeZone tz);
}
