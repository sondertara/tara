package com.sondertara.common.id;

import org.jspecify.annotations.NonNull;

import java.util.Date;

/**
 *
 */
public class XidGenerator implements IdGenerator<String> {

    private String get(@NonNull Date date) {
        return new Xid(date).toString();
    }

    @Override
    public String get() {
        return get(new Date());
    }
}
