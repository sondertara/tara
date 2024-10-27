package com.sondertara.common.security;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @author huangxiaohu.1ih
 * @date 2024/8/6 14:15
 */
@Deprecated
public class UnsafeUtils {
    public static Unsafe getUnsafe() {
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged((PrivilegedAction<Unsafe>) UnsafeUtils::getUnsafe0);
        }
        return getUnsafe0();
    }

    private static Unsafe getUnsafe0() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (Throwable t) {
            throw new RuntimeException("JDK did not allow accessing unsafe", t);
        }
    }
}
