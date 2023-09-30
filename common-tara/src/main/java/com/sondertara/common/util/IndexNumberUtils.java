package com.sondertara.common.util;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author huangxiaohu
 */
public final class IndexNumberUtils {

    private IndexNumberUtils() {
    }

    private static final AtomicReference<Map<String, AtomicLong>> indexNumberMap = new AtomicReference<>(Maps.newConcurrentMap());

    private static final AtomicLong TaskItemIdIndex = new AtomicLong(0);

    public static long getRequestMessageId() {
        return incrementAndGet("RequestMessageId");
    }

    public static long getTaskItemId() {
        return TaskItemIdIndex.incrementAndGet();
    }

    public static long getBootstrapEventIndex() {
        return incrementAndGet("BootstrapEventIndex");
    }

    public static void resetBootstrapEventIndex() {
        if (indexNumberMap.get().containsKey("BootstrapEventIndex")) {
            indexNumberMap.get().get("BootstrapEventIndex").set(0);
        }
    }

    public static void reset(String name) {
        if (indexNumberMap.get().containsKey(name)) {
            indexNumberMap.get().get(name).set(0);
        }
    }

    public static void resetAll() {
        for (Map.Entry<String, AtomicLong> entry : indexNumberMap.get().entrySet()) {
            entry.getValue().set(0);
        }
    }

    public static long incrementAndGet(String name) {
        if (!indexNumberMap.get().containsKey(name)) {
            indexNumberMap.get().put(name, new AtomicLong(0));
        }
        return indexNumberMap.get().get(name).incrementAndGet();
    }

    public static long incrementAndGet(String name, int i) {
        if (!indexNumberMap.get().containsKey(name)) {
            indexNumberMap.get().put(name, new AtomicLong(0));
        }
        return indexNumberMap.get().get(name).addAndGet(i);
    }

    public static long get(String name) {
        if (!indexNumberMap.get().containsKey(name)) {
            indexNumberMap.get().put(name, new AtomicLong(0));
        }
        return indexNumberMap.get().get(name).get();
    }

}
