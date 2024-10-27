package com.sondertara.common.pipeline.simplex;

import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.struct.Holder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SharedSimplexPipeline implements SimplexPipeline {
    private List<SimplexHandler> handlers = new ArrayList<>();
    private boolean resultNullable = false;

    @Override
    public void addFirst(SimplexHandler handler) {
        this.handlers.add(0, handler);
    }

    @Override
    public void addLast(SimplexHandler handler) {
        this.handlers.add(handler);
    }

    @Override
    public void clear() {
        this.handlers.clear();
    }

    @Override
    public Object handle(final Object message) {
        final Holder<Object> result = new Holder<Object>(message);
        CollectionUtils.forEach(handlers, new Consumer<SimplexHandler>() {
            @Override
            public void accept(SimplexHandler handler) {
                handler.apply(result.get());
            }
        }, new Predicate<SimplexHandler>() {
            @Override
            public boolean test(SimplexHandler handler) {
                return result.isNull() && !resultNullable;
            }
        });
        return result.get();
    }


}
