package com.sondertara.common.security.masking;
/**
 *  */
public abstract class AbstractStringMarker extends Masker<String> {
    @Override
    public abstract String doTransform(String text);
}
