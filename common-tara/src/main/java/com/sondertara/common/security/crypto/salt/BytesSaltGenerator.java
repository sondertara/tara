package com.sondertara.common.security.crypto.salt;


import java.util.function.Function;

public interface BytesSaltGenerator extends Function<Integer, byte[]> {
}
