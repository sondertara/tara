package com.sondertara.common.io.close;

import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Maps;
import com.sondertara.common.io.Closer;
import com.sondertara.common.logging.Loggers;

import java.io.Closeable;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.function.Consumer;

public class ObjectCloser {
    private static Map<Class, Closer> closerMap = Maps.newConcurrentMap();
    private static ForceCloser forceCloser = new ForceCloser();

    static {
        CollectionUtils.forEach(ServiceLoader.load(Closer.class), ObjectCloser::register);
    }

    private ObjectCloser(){

    }
    public static void register(final Closer closer) {
        if (closer != null) {
            CollectionUtils.forEach(closer.applyTo(), new Consumer<Class>() {
                @Override
                public void accept(Class aClass) {
                    closerMap.put(aClass, closer);
                }
            });
        }
    }

    public static void close(Object obj) {
        if (obj == null) {
            return;
        }

        if(obj instanceof Closeable){
            try {
                ((Closeable) obj).close();
            }catch (Exception e){
                Loggers.getLogger(ObjectCloser.class).warn("close fail: {}", obj);
            }
        }
        Class type = obj.getClass();
        findCloser(type).close(obj);
    }

    private static Closer findCloser(final Class type) {
        Objects.requireNonNull(type);
        Closer closer = closerMap.get(type);
        if (closer == null) {
            Class t = CollectionUtils.findFirst(closerMap.keySet(), expectClass -> expectClass.isAssignableFrom(type));

            if (t != null) {
                closer = closerMap.get(t);
            }
        }

        if (closer == null) {
            closer = forceCloser;
        }

        return closer;
    }


}
