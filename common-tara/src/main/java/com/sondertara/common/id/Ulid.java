//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.sondertara.common.id;

import java.security.SecureRandom;

public final class Ulid {
    private static final char[] ENCODING_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z'};
    private static final int MASK = 31;
    private static final int MASK_BITS = 5;
    private static final long TIMESTAMP_OVERFLOW_MASK = -281474976710656L;
    private static final long TIMESTAMP_MSB_MASK = -65536L;
    private static final long RANDOM_MSB_MASK = 65535L;
    private static final long HALF_RANDOM_COMPONENT = 1099511627775L;
    private static final long MAX_INCREMENT = 1099511627776L;
    private static final long TIMESTAMP_MAX = (long)Math.pow(2.0, 48.0) - 1L;
    private static final byte A = 6;
    private static final byte B = 127;
    private static final byte C = -72;
    private static final byte D = -63;
    private long v0;
    private long v1;
    private long v2;
    private long v3;
    private long lastUsedTimestamp;
    private long randomMaxMsb;
    private long randomMaxLsb;
    private long randomMsb;
    private long randomLsb;
    private final SecureRandom randomGenerator;

    public Ulid() {
        this(new SecureRandom());
    }

    public Ulid(SecureRandom random) {
        this.v0 = 0L;
        this.v1 = 0L;
        this.v2 = 0L;
        this.v3 = 0L;
        this.randomMsb = 0L;
        this.randomLsb = 0L;
        this.randomGenerator = random;
        this.reseed();
    }

    public String create() {
        long k0 = sipHash24(this.v0, this.v1, this.v2, this.v3, (byte)6);
        long k1 = sipHash24(this.v0, this.v1, this.v2, this.v3, (byte)127);
        long msb = sipHash24(this.v0, this.v1, this.v2, this.v3, (byte)-72) & -61441L | 16384L;
        long lsb = sipHash24(this.v0, this.v1, this.v2, this.v3, (byte)-63) << 2 >>> 2 | Long.MIN_VALUE;
        this.reseed(k0, k1);
        return asString(System.currentTimeMillis(), msb, lsb);
    }

    public String next() {
        long timestamp = this.getTimestamp();
        long msbRandom = this.randomMsb & 1099511627775L;
        long lsbRandom = this.randomLsb & 1099511627775L;
        long msb = timestamp << 16 | msbRandom >>> 24;
        long lsb = msbRandom << 40 | lsbRandom;
        return asString(msb, lsb);
    }

    public void reseed() {
        byte[] seed = new byte[128];
        this.randomGenerator.nextBytes(seed);
        this.reseed(this.randomGenerator.nextLong(), this.randomGenerator.nextLong());
    }

    private void reseed(long k0, long k1) {
        this.v0 = k0 ^ 8317987319222330741L;
        this.v1 = k1 ^ 7237128888997146477L;
        this.v2 = k0 ^ 7816392313619706465L;
        this.v3 = k1 ^ 8387220255154660723L;
    }

    private long getTimestamp() {
        long timestamp = System.currentTimeMillis();
        if (timestamp == this.lastUsedTimestamp) {
            this.increment();
        } else {
            this.reset();
        }

        this.lastUsedTimestamp = timestamp;
        return timestamp;
    }

    private synchronized void reset() {
        long k0 = sipHash24(this.v0, this.v1, this.v2, this.v3, (byte)6);
        long k1 = sipHash24(this.v0, this.v1, this.v2, this.v3, (byte)127);
        this.randomMsb = sipHash24(this.v0, this.v1, this.v2, this.v3, (byte)-72) & -61441L | 16384L;
        this.randomLsb = sipHash24(this.v0, this.v1, this.v2, this.v3, (byte)-63) << 2 >>> 2 | Long.MIN_VALUE;
        this.reseed(k0, k1);
        this.randomMaxMsb = this.randomMsb | 1099511627776L;
        this.randomMaxLsb = this.randomLsb | 1099511627776L;
    }

    private synchronized void increment() {
        if (++this.randomLsb >= this.randomMaxLsb) {
            this.randomLsb &= 1099511627775L;
            if (++this.randomMsb >= this.randomMaxMsb) {
                this.reset();
            }
        }

    }

    private static String asString(long timestamp, long msb, long lsb) {
        checkTimestamp(timestamp);
        return crockfordBase32(timestamp, msb, lsb);
    }

    private static String asString(long msb, long lsb) {
        long time = (msb & -65536L) >>> 16;
        long random1 = (msb & 65535L) << 24 | (lsb & -1099511627776L) >>> 40;
        long random2 = lsb & 1099511627775L;
        return crockfordBase32(time, random1, random2);
    }

    private static String crockfordBase32(long timeComponent, long msb, long lsb) {
        char[] buffer = new char[]{ENCODING_CHARS[(int)(timeComponent >>> 45 & 31L)], ENCODING_CHARS[(int)(timeComponent >>> 40 & 31L)], ENCODING_CHARS[(int)(timeComponent >>> 35 & 31L)], ENCODING_CHARS[(int)(timeComponent >>> 30 & 31L)], ENCODING_CHARS[(int)(timeComponent >>> 25 & 31L)], ENCODING_CHARS[(int)(timeComponent >>> 20 & 31L)], ENCODING_CHARS[(int)(timeComponent >>> 15 & 31L)], ENCODING_CHARS[(int)(timeComponent >>> 10 & 31L)], ENCODING_CHARS[(int)(timeComponent >>> 5 & 31L)], ENCODING_CHARS[(int)(timeComponent >>> 0 & 31L)], ENCODING_CHARS[(int)(msb >>> 35 & 31L)], ENCODING_CHARS[(int)(msb >>> 30 & 31L)], ENCODING_CHARS[(int)(msb >>> 25 & 31L)], ENCODING_CHARS[(int)(msb >>> 20 & 31L)], ENCODING_CHARS[(int)(msb >>> 15 & 31L)], ENCODING_CHARS[(int)(msb >>> 10 & 31L)], ENCODING_CHARS[(int)(msb >>> 5 & 31L)], ENCODING_CHARS[(int)(msb >>> 0 & 31L)], ENCODING_CHARS[(int)(lsb >>> 35 & 31L)], ENCODING_CHARS[(int)(lsb >>> 30 & 31L)], ENCODING_CHARS[(int)(lsb >>> 25 & 31L)], ENCODING_CHARS[(int)(lsb >>> 20 & 31L)], ENCODING_CHARS[(int)(lsb >>> 15 & 31L)], ENCODING_CHARS[(int)(lsb >>> 10 & 31L)], ENCODING_CHARS[(int)(lsb >>> 5 & 31L)], ENCODING_CHARS[(int)(lsb >>> 0 & 31L)]};
        return new String(buffer);
    }

    private static long sipHash24(long v0, long v1, long v2, long v3, byte data) {
        long m = (long)data & 255L | 72057594037927936L;
        v3 ^= m;

        int i;
        for(i = 0; i < 2; ++i) {
            v0 += v1;
            v2 += v3;
            v1 = Long.rotateLeft(v1, 13);
            v3 = Long.rotateLeft(v3, 16);
            v1 ^= v0;
            v3 ^= v2;
            v0 = Long.rotateLeft(v0, 32);
            v2 += v1;
            v0 += v3;
            v1 = Long.rotateLeft(v1, 17);
            v3 = Long.rotateLeft(v3, 21);
            v1 ^= v2;
            v3 ^= v0;
            v2 = Long.rotateLeft(v2, 32);
        }

        v0 ^= m;
        v2 ^= 255L;

        for(i = 0; i < 4; ++i) {
            v0 += v1;
            v2 += v3;
            v1 = Long.rotateLeft(v1, 13);
            v3 = Long.rotateLeft(v3, 16);
            v1 ^= v0;
            v3 ^= v2;
            v0 = Long.rotateLeft(v0, 32);
            v2 += v1;
            v0 += v3;
            v1 = Long.rotateLeft(v1, 17);
            v3 = Long.rotateLeft(v3, 21);
            v1 ^= v2;
            v3 ^= v0;
            v2 = Long.rotateLeft(v2, 32);
        }

        return v0 ^ v1 ^ v2 ^ v3;
    }

    private static void checkTimestamp(long timestamp) {
        if ((timestamp & -281474976710656L) != 0L) {
            throw new IllegalArgumentException("ULID does not support timestamps after +10889-08-02T05:31:50.655Z!");
        }
    }

    public static long unixTime(String ulidStr) {
        char[] tb = ulidStr.toCharArray();
        char[] timestampComponent = new char[10];
        System.arraycopy(tb, 0, timestampComponent, 0, 10);
        return toLong(timestampComponent);
    }

    public static boolean isValid(String ulidStr) {
        if (ulidStr == null) {
            return false;
        } else {
            char[] chars = ulidStr.toCharArray();
            if (chars.length == 26 && containsValidBase32Chars(chars)) {
                long timestamp = unixTime(ulidStr);
                return timestamp >= 0L && timestamp <= TIMESTAMP_MAX;
            } else {
                return false;
            }
        }
    }

    protected static long toLong(char[] input) {
        long n = 0L;

        for(int i = 0; i < input.length; ++i) {
            int d = decodeBase32(input[i]);
            n = 32L * n + (long)d;
        }

        return n;
    }

    private static int decodeBase32(char c) {
        for(int i = 0; i < ENCODING_CHARS.length; ++i) {
            if (ENCODING_CHARS[i] == c) {
                return (byte)i;
            }
        }

        return 48;
    }

    private static boolean containsValidBase32Chars(char[] chars) {
        char[] input = toUpperCase(chars);

        for(int i = 0; i < input.length; ++i) {
            if (!isBase32Char(input[i])) {
                return false;
            }
        }

        return true;
    }

    private static boolean isBase32Char(char c) {
        for(int j = 0; j < ENCODING_CHARS.length; ++j) {
            if (c == ENCODING_CHARS[j]) {
                return true;
            }
        }

        return false;
    }

    private static char[] toUpperCase(char[] input) {
        char[] output = new char[input.length];

        for(int i = 0; i < output.length; ++i) {
            output[i] = Character.toUpperCase(input[i]);
        }

        return output;
    }
}
