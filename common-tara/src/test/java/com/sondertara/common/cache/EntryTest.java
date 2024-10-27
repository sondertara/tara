package com.sondertara.common.cache;

import com.sondertara.common.reflect.reference.ReferenceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.lang.ref.ReferenceQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.MockitoAnnotations.initMocks;

class EntryTest {

    @Mock
    private ReferenceQueue<?> mockReferenceQueue;

    private Entry<String, String> entryUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        entryUnderTest = new Entry<>("key", ReferenceType.STRONG, "value", ReferenceType.STRONG, mockReferenceQueue,
                false, 0L);
    }

    @Test
    void testGetValue1() {
        assertEquals("result", entryUnderTest.getValue());
    }

    @Test
    void testGetValue2() {
        assertEquals("result", entryUnderTest.getValue(false));
    }

    @Test
    void testSetValue() {
        // Setup
        // Run the test
        entryUnderTest.setValue("value");

        // Verify the results
    }

    @Test
    void testGetAge() {
        // Setup
        // Run the test
        final long result = entryUnderTest.getAge();

        // Verify the results
        assertEquals(0L, result);
    }

    @Test
    void testIncrementAge() {
        // Setup
        // Run the test
        entryUnderTest.incrementAge();

        // Verify the results
    }

    @Test
    void testIncrementUseCount() {
        // Setup
        // Run the test
        entryUnderTest.incrementUseCount();

        // Verify the results
    }

    @Test
    void testExpireTimeGetterAndSetter() {
        final long expireTime = 0L;
        entryUnderTest.setExpireTime(expireTime);
        assertEquals(expireTime, entryUnderTest.getExpireTime());
    }

    @Test
    void testIsExpired() {
        assertFalse(entryUnderTest.isExpired());
    }

    @Test
    void testLastUsedTimeGetterAndSetter() {
        final long lastUsedTime = 0L;
        entryUnderTest.setLastUsedTime(lastUsedTime);
        assertEquals(lastUsedTime, entryUnderTest.getLastUsedTime());
    }

    @Test
    void testLastReadTimeGetterAndSetter() {
        final long lastReadTime = 0L;
        entryUnderTest.setLastReadTime(lastReadTime);
        assertEquals(lastReadTime, entryUnderTest.getLastReadTime());
    }

    @Test
    void testLastWriteTimeGetterAndSetter() {
        final long lastWriteTime = 0L;
        entryUnderTest.setLastWriteTime(lastWriteTime);
        assertEquals(lastWriteTime, entryUnderTest.getLastWriteTime());
    }

    @Test
    void testEquals() {
        // Setup
        // Run the test
        final boolean result = entryUnderTest.equals("object");

        // Verify the results
        assertFalse(result);
    }

    @Test
    void testHashCode() {
        // Setup
        // Run the test
        final int result = entryUnderTest.hashCode();

        // Verify the results
        assertEquals(0, result);
    }
}
