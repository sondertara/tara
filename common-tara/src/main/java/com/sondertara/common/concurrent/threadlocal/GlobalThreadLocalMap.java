package com.sondertara.common.concurrent.threadlocal;

import com.sondertara.common.text.StringUtils;
import com.sondertara.common.collection.NonAbsentHashMap;
import com.sondertara.common.collection.WrappedNonAbsentMap;
import com.sondertara.common.datetime.DateFormatCacheKey;
import com.sondertara.common.random.IRandom;
import com.sondertara.common.random.PooledBytesRandom;
import com.sondertara.common.random.RandomProxy;
import com.sondertara.common.random.ThreadLocalRandom;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.text.SimpleDateFormat;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Function;

public class GlobalThreadLocalMap {
    private static final ThreadLocal<GlobalThreadLocalMap> CACHE = ThreadLocal.withInitial(GlobalThreadLocalMap::new);

    private static GlobalThreadLocalMap get() {
        return CACHE.get();
    }

    /**
     * encoder, decoder
     */
    private final Map<Charset, CharsetEncoder> encoderMap = WrappedNonAbsentMap.wrap(new IdentityHashMap<Charset, CharsetEncoder>(), new Function<Charset, CharsetEncoder>() {
        @Override
        public CharsetEncoder apply(Charset charset) {
            CharsetEncoder e = charset.newEncoder();
            e.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
            return e;
        }
    });

    private final Map<Charset, CharsetDecoder> decoderMap = WrappedNonAbsentMap.wrap(new IdentityHashMap<>(), charset -> {
        CharsetDecoder d = charset.newDecoder();
        d.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
        return d;
    });

    public static CharsetDecoder getDecoder(Charset charset) {
        return get().decoderMap.get(charset);
    }

    public static CharsetEncoder getEncoder(Charset charset) {
        return get().encoderMap.get(charset);
    }


    /**
     * date formatter
     * key: pattern
     */
    private final Map<DateFormatCacheKey, SimpleDateFormat> simpleDateFormatMap = new NonAbsentHashMap<>(key -> {
        SimpleDateFormat df = new SimpleDateFormat(key.pattern, key.locale);
        if (StringUtils.isNotEmpty(key.timeZoneId)) {
            df.setTimeZone(TimeZone.getTimeZone(key.timeZoneId));
        }
        return df;
    });


    public static SimpleDateFormat getSimpleDateFormat(@NonNull String pattern) {
        return getSimpleDateFormat(new DateFormatCacheKey(pattern));
    }

    public static SimpleDateFormat getSimpleDateFormat(@NonNull String pattern, @Nullable Locale locale) {
        return getSimpleDateFormat(new DateFormatCacheKey(pattern, locale));
    }

    public static SimpleDateFormat getSimpleDateFormat(@NonNull String pattern, @Nullable TimeZone timeZone) {
        return getSimpleDateFormat(new DateFormatCacheKey(pattern, timeZone));
    }

    public static SimpleDateFormat getSimpleDateFormat(@NonNull String pattern, @Nullable String timeZoneId) {
        return getSimpleDateFormat(new DateFormatCacheKey(pattern, timeZoneId));
    }

    public static SimpleDateFormat getSimpleDateFormat(@NonNull String pattern, @Nullable TimeZone timeZone, @Nullable Locale locale) {
        return getSimpleDateFormat(new DateFormatCacheKey(pattern, timeZone, locale));
    }

    public static SimpleDateFormat getSimpleDateFormat(@NonNull String pattern, @Nullable String timeZoneId, @Nullable Locale locale) {
        return getSimpleDateFormat(new DateFormatCacheKey(pattern, timeZoneId, locale));
    }

    private static SimpleDateFormat getSimpleDateFormat(DateFormatCacheKey key) {
        return get().simpleDateFormatMap.get(key);
    }

    private final char[] charBuffer = new char[1024];

    public static char[] getCharBuffer() {
        return get().charBuffer;
    }


    public void clear() {
        CACHE.remove();
    }

    public static IRandom getRandom() {
        return new RandomProxy(ThreadLocalRandom.current());
    }

    private static final PooledBytesRandom pooledBytesRandom = new PooledBytesRandom();

    public static PooledBytesRandom pooledBytesRandom() {
        return pooledBytesRandom;
    }
}
