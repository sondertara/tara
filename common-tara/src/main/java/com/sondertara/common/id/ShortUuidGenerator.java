package com.sondertara.common.id;

/**
 *
 */
public class ShortUuidGenerator implements IdGenerator<String> {

    @Override
    public String get() {
        return ShortUuid.shortUuid();
    }
}
