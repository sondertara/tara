package com.sondertara.common.security.masking.impl;

import com.sondertara.common.security.masking.AbstractStringMarker;
import com.sondertara.common.security.masking.Maskings;

/**
 * 使用 6 颗星星替代
 *  */
public class Star6Masker extends AbstractStringMarker {
    @Override
    public String doTransform(String text) {
        return "******";
    }

    @Override
    public String getName() {
        return Maskings.Strategy.STAR_6;
    }
}
