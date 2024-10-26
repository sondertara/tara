package com.sondertara.common.loader;

import com.github.f4b6a3.tsid.Tsid;
import com.github.f4b6a3.tsid.TsidFactory;
import com.sondertara.common.classpath.classloader.ClassScanner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;


public class ClassScannerTest {

    @Test
    public void scan() {
        //[7, -77, 61, -4, 104, 9, -128, -115]
        int nodeMask = 0x003fffff >>> 6;

        long l5 = ~(-1L << 22);

        long nn = Long.parseLong("1111111111111111000000", 2);
        String hexString = Long.toHexString(nn);

        long l6 = 1L << 6;
        String binaryString4 = Long.toBinaryString(l6);

        String binaryString3 = Long.toBinaryString(l5);
        String binaryString6 = Long.toBinaryString(l5>>7);



        //1111111111111111111111
        //1111111111111111000000
        long l11= (9 & 0xffL) << 16;
        long l2 = (-128 & 0xffL) << 8;
        long l3 = (-115 & 0xffL);

        long l4 = l11 | l2 | l3;
        long ssss=l4>>6&nodeMask;
        long i5 =l4&Long.parseLong(binaryString6+binaryString4,2);


        int i1 = 9 & 0x03f;


        int i2 = -115 >>> 7;

        String string = Integer.toBinaryString(i1) + Integer.toBinaryString(-128) + Integer.toBinaryString(i2);
        int parseInt = Integer.parseInt(string, 2);

        int nodeId = 38 * 256 + 2 % 65536;
        TsidFactory tsidFactory = TsidFactory.builder().withNode(nodeId).withNodeBits(16).build();

        Tsid tsid = tsidFactory.create();
        long aLong = tsid.toLong();
        byte[] tsidBytes = tsid.toBytes();
        String binaryString = Long.toBinaryString(aLong);
        long l1 = aLong >> 6;
        String binaryString1 = Long.toBinaryString(l1);


        String binaryString2 = Integer.toBinaryString(0x003fffff);

        int countMask = 0x003fffff >>> 16;
        String countMaskStr = Integer.toBinaryString(countMask);

        int node = nodeId & nodeMask;
        String nodeMaskStr = Integer.toBinaryString(nodeMask);

        int i = 0xFFFF;
        long l = l1 & i;
        Set<Class<?>> scan = new ClassScanner("com.sondertara.common.command").scan();
        Assertions.assertEquals(scan.size(), 5);
    }
}
