package com.sondertara.common.plugin;

import com.sondertara.common.base.Named;
import com.sondertara.common.base.Ordered;
import com.sondertara.common.lifecycle.Destroyable;
import com.sondertara.common.lifecycle.Initializable;

public interface Plugin<E> extends Initializable, Destroyable, Named, Ordered {
    boolean availableFor(E e);
}
