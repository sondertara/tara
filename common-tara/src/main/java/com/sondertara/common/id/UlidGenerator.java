package com.sondertara.common.id;

/**
 *
 */
public class UlidGenerator implements IdGenerator<String> {

    private static final Ulid ULID = new Ulid();


    @Override
    public String get() {
        return ULID.next();
    }
}
