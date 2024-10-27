package com.sondertara.common.id;

import com.sondertara.common.random.ThreadLocalRandom;

import java.util.Random;
import java.util.UUID;

public class ConcurrentUuidGenerator implements IdGenerator<String> {
    public static final ConcurrentUuidGenerator INSTANCE = new ConcurrentUuidGenerator();

    public String get() {
        Random rnd = ThreadLocalRandom.current();
        long mostSig = rnd.nextLong();
        long leastSig = rnd.nextLong();
        mostSig &= -61441L;
        mostSig |= 16384L;
        leastSig &= 4611686018427387903L;
        leastSig |= Long.MIN_VALUE;
        return new UUID(mostSig, leastSig).toString();
    }

}
