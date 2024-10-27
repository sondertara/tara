package com.sondertara.common.net.idn;

import com.sondertara.common.codec.CodecException;

public class PunycodeException extends CodecException {
    public static final String OVERFLOW = "Overflow.";
    public static final String BAD_INPUT = "Bad input.";

    /**
     * Creates a new PunycodeException.
     *
     * @param m message.
     */
    public PunycodeException(String m)
    {
        super(m);
    }
}
