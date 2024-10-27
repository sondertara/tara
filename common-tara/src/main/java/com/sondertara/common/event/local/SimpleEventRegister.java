package com.sondertara.common.event.local;

import com.sondertara.common.event.CommonEventRegister;

public class SimpleEventRegister extends CommonEventRegister {

    public SimpleEventRegister() {
        setName("Simple-EventBus-" + COUNTER.getAndIncrement());
        setDispatcher(SimpleEventDispatcher.INSTANCE);
    }
}
