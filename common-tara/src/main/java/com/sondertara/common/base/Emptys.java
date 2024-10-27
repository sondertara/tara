package com.sondertara.common.base;

import com.sondertara.common.collection.ArrayUtils;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.StreamUtils;
import com.sondertara.common.function.Functions;
import com.sondertara.common.math.Numbers;
import com.sondertara.common.struct.Holder;
import com.sondertara.common.struct.Reference;
import com.sondertara.common.text.StringUtils;

import java.lang.reflect.Array;
import java.nio.Buffer;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class Emptys {
    private Emptys() {

    }

    public static final int[] EMPTY_INTS = {};
    public static final byte[] EMPTY_BYTES = {};
    public static final char[] EMPTY_CHARS = {};
    public static final Object[] EMPTY_OBJECTS = {};
    public static final Class<?>[] EMPTY_CLASSES = {};
    public static final String[] EMPTY_STRINGS = {};
    public static final String EMPTY_STRING = "";
    public static final SortedMap EMPTY_TREE_MAP = new TreeMap();
    public static final SortedSet EMPTY_TREE_SET = new TreeSet();

    @SafeVarargs
    public static <T> boolean isAnyEmpty(T... args) {
        return CollectionUtils.anyMatch(Functions.emptyPredicate(), args);
    }

    public static boolean isNoneEmpty(Object... args) {
        return CollectionUtils.noneMatch(Functions.emptyPredicate(), args);
    }


    public static boolean isAllEmpty(Object... args) {
        return CollectionUtils.allMatch(Functions.emptyPredicate(), args);
    }

    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        }

        if (object instanceof Collection) {
            return CollectionUtils.isEmpty((Collection) object);
        }

        if (object instanceof Map) {
            return CollectionUtils.isEmpty((Map) object);
        }
        if (object instanceof String) {
            return StringUtils.isEmpty((String) object);
        }

        if (object instanceof CharSequence) {
            CharSequence cs = (CharSequence) object;
            return cs.length() == 0;
        }

        if (object instanceof Number) {
            return Numbers.isZero((Number) object);
        }

        if (object instanceof Buffer) {
            Buffer buff = (Buffer) object;
            return buff.hasRemaining();
        }

        if (object.getClass().isEnum()) {
            return false;
        }
        if (object.getClass().isArray()) {
            return Array.getLength(object) <= 0;
        }

        if (object instanceof EmptyEvalutible) {
            return ((EmptyEvalutible) object).isEmpty();
        }
        if (object instanceof Holder) {
            return ((Holder) object).isEmpty();
        }
        if (object instanceof Reference) {
            return ((Reference) object).isNull();
        }

        return false;
    }

    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }

    public static boolean isNull(Object o) {
        return o == null;
    }

    public static boolean isNotNull(Object o) {
        return o != null;
    }

    public static <T> int getLength(T object) {
        if (isNull(object)) {
            return 0;
        }
        if (object instanceof String) {
            return ((String) object).length();
        }

        if (object instanceof CharSequence) {
            CharSequence cs = (CharSequence) object;
            return cs.length();
        }

        if (object instanceof Number) {
            return 1;
        }

        if (object instanceof Buffer) {
            Buffer buff = (Buffer) object;
            return buff.remaining();
        }

        if (object instanceof Collection) {
            return ((Collection<?>) object).size();
        }

        if (object instanceof Map) {
            return ((Map<?,?>) object).size();
        }
        if (object.getClass().isArray()) {
            return ArrayUtils.getLength(object);
        }

        if (object.getClass().isEnum()) {
            return 1;
        }
        if (object instanceof Holder) {
            Holder<?> holder = (Holder<?>) object;
            return getLength(holder.get());
        }
        return (int) StreamUtils.of(object).count();
    }

}
