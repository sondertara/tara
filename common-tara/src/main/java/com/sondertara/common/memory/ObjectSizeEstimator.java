package com.sondertara.common.memory;

import java.lang.instrument.Instrumentation;

/**
 * TODO
 *
 * @author huangxiaohu.1ih
 * @date 2024/8/28 11:04
 */
public class ObjectSizeEstimator {
    private static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) {
        instrumentation = inst;
    }

    public static long getObjectSize(Object obj) {

        return instrumentation.getObjectSize(obj);
    }
}
