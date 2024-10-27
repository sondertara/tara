package com.sondertara.common.id;

import java.util.UUID;

public class UuidGenerator implements IdGenerator<String> {
    public static final UuidGenerator INSTANCE = new UuidGenerator();

    public String get() {
      return   UUID.randomUUID().toString();
    }
}
