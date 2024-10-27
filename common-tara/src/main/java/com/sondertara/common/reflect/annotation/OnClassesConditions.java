package com.sondertara.common.reflect.annotation;

import com.sondertara.common.annotation.OnClasses;
import com.sondertara.common.classpath.ClassLoaders;
import com.sondertara.common.collection.StreamUtils;
import com.sondertara.common.logging.Loggers;
import com.sondertara.common.reflect.ReflectUtils;
import org.slf4j.Logger;

import java.util.function.Predicate;

public class OnClassesConditions {
    private static final Logger logger = Loggers.getLogger(OnClassesConditions.class);
    private OnClassesConditions(){

    }
    public static boolean anyPresent(final Class klass, boolean defaultValueIfMissOnClassesAnnotation) {
        OnClasses annotation = ReflectUtils.getAnnotation(klass, OnClasses.class);
        if (annotation != null) {
            String[] classes = annotation.value();
            return StreamUtils.of(classes).anyMatch(new Predicate<String>() {
                @Override
                public boolean test(String className) {
                    boolean hasClass = ClassLoaders.hasClass(className, klass.getClassLoader());
                    if (!hasClass) {
                        logger.warn("Class {} not found", className);
                    }
                    return hasClass;
                }
            });
        }
        return defaultValueIfMissOnClassesAnnotation;
    }

    public static boolean allPresent(final Class klass, boolean defaultValueIfMissOnClassesAnnotation) {
        OnClasses annotation = ReflectUtils.getAnnotation(klass, OnClasses.class);
        if (annotation != null) {
            String[] classes = annotation.value();
            return StreamUtils.of(classes).allMatch(new Predicate<String>() {
                @Override
                public boolean test(String className) {
                    boolean hasClass = ClassLoaders.hasClass(className, klass.getClassLoader());
                    if (!hasClass) {
                        logger.warn("Class {} not found", className);
                    }
                    return hasClass;
                }
            });
        }
        return defaultValueIfMissOnClassesAnnotation;
    }
}
