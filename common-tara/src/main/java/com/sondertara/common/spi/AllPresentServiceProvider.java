package com.sondertara.common.spi;

import com.sondertara.common.reflect.annotation.OnClassesConditions;

import java.util.function.Predicate;


public class AllPresentServiceProvider<T> extends CommonServiceProvider<T> {
    private boolean defaultValueIfMissOnClassesAnnotation;

    public AllPresentServiceProvider() {
        this(true);
    }

    public AllPresentServiceProvider(boolean defaultValueIfMissOnClassesAnnotation) {
        this.defaultValueIfMissOnClassesAnnotation = defaultValueIfMissOnClassesAnnotation;
        this.setPredicate(new Predicate<T>() {
            @Override
            public boolean test(T t) {
                return OnClassesConditions.allPresent(t.getClass(), AllPresentServiceProvider.this.defaultValueIfMissOnClassesAnnotation);
            }
        });
    }

}
