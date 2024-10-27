package com.sondertara.common.codec;

/**
 * Defines encoding and decoding policies.
 *
 *  */
public enum CodecPolicy {

    /**
     * The strict policy. Data that causes a codec to fail should throw an exception.
     */
    STRICT,

    /**
     * The lenient policy. Data that causes a codec to fail should not throw an exception.
     */
    LENIENT
}
