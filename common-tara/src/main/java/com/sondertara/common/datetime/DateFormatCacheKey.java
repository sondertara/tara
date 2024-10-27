package com.sondertara.common.datetime;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/**
 * @author huangxiaohu.1ih
 */
public class DateFormatCacheKey {
    @NonNull
    public String pattern;
    @Nullable
    public String timeZoneId;
    @Nullable
    public Locale locale;

    public DateFormatCacheKey(@NonNull String pattern) {
        this(pattern, (String) null, null);
    }

    public DateFormatCacheKey(@NonNull String pattern, @Nullable String timeZoneId) {
        this(pattern, timeZoneId, null);
    }

    public DateFormatCacheKey(@NonNull String pattern, @Nullable TimeZone timeZone) {
        this(pattern, timeZone, null);
    }

    public DateFormatCacheKey(@NonNull String pattern, @Nullable Locale locale) {
        this(pattern, (String) null, locale);
    }

    public DateFormatCacheKey(@NonNull String pattern, @Nullable TimeZone timeZone, @Nullable Locale locale) {
        this(pattern, timeZone == null ? null : timeZone.getID(), locale);
    }

    public DateFormatCacheKey(@NonNull String pattern, @Nullable String timeZoneId, @Nullable Locale locale) {
        Objects.requireNonNull(pattern);
        this.locale = locale == null ? Locale.getDefault() : locale;
        this.timeZoneId = timeZoneId;
        this.pattern = pattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DateFormatCacheKey that = (DateFormatCacheKey) o;

        if (!pattern.equals(that.pattern)) {
            return false;
        }
        if (!timeZoneId.equals(that.timeZoneId)) {
            return false;
        }
        return locale.equals(that.locale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pattern, this.timeZoneId, this.locale);
    }
}
