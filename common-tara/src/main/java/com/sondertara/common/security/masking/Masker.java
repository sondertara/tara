package com.sondertara.common.security.masking;

import com.sondertara.common.base.Named;
import com.sondertara.common.transformer.ConditionTransformer;

/**
 *  */
public abstract class Masker<DATA> extends ConditionTransformer<DATA, String> implements Named {

    public int getOrder() {
        return 0;
    }

    @Override
    public abstract String doTransform(DATA input);

    @Override
    public abstract String getName();
}
