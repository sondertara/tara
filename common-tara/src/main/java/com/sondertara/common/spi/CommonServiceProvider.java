package com.sondertara.common.spi;

import com.sondertara.common.function.Functions;
import com.sondertara.common.logging.Loggers;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Predicate;

public class CommonServiceProvider<T> implements ServiceProvider<T> {
    private static final Logger logger = Loggers.getLogger(CommonServiceProvider.class);
    private Predicate<T> predicate = Functions.truePredicate();

    @Nullable
    private Comparator<T> comparator;

    public void setPredicate(Predicate<T> predicate) {
        this.predicate = predicate;
    }

    public void setComparator(@Nullable Comparator<T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public Iterator<T> get(Class<T> serviceClass) {
        ServiceLoader<T> loader = ServiceLoader.load(serviceClass);
        Iterator<T> iter = loader.iterator();
        List<T> ret = new ArrayList<>();
        while (iter.hasNext()) {
            try {
                T t = iter.next();
                if (predicate.test(t)) {
                    ret.add(t);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        }
        if (comparator != null) {
            ret.sort(comparator);
        }
        return ret.iterator();
    }
}
