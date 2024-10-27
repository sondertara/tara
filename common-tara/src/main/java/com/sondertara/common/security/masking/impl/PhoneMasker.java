package com.sondertara.common.security.masking.impl;

import com.sondertara.common.text.StringUtils;
import com.sondertara.common.security.masking.AbstractStringMarker;
import com.sondertara.common.security.masking.Maskings;

/**
 *  */
public class PhoneMasker extends AbstractStringMarker {
    @Override
    public String doTransform(String text) {
        String mask = "********";
        if (text.length() >= 8) {
            int start = text.length() - 8;
            int end = text.length() - 4;
            mask = StringUtils.replace(text, start, end, '*');
        }
        return mask;
    }

    @Override
    public String getName() {
        return Maskings.Strategy.PHONE;
    }
}
