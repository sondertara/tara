package com.sondertara.common.lang.loader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;


public class ClassScannerTest {

    @Test
    public void scan() {
        Set<Class<?>> scan = new ClassScanner("com.sondertara.common.command").scan();
        Assertions.assertEquals(scan.size(), 5);
    }
}
