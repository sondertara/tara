package com.sondertara.common.lang.id;

import com.sondertara.common.util.IpUtils;

import java.util.SplittableRandom;

/**
 * Based on SnowFlake
 * bit:     0     1~30  31~48  49~60   61~63
 * section: keep  data  node   serial  random
 * length:  1 bit 30bit	18bit  12bit   3bit
 */
public class MeteorId {
    /**
     * initial value is 2023-04-24 12:13:00
     */
    public final static long INIT_SECOND = 1682309580L;
    /**
     * node section number bits
     */
    public final static int NODE_BITS = 18;

    /**
     * serial section number bits
     */
    public final static int SEQ_BITS = 12;

    /**
     * random  section number bits
     */

    public final static long RAND_BITS = 3;
    private long second;
    private long seqNum;
    private long randNum;


    /**
     * the high bits of data section
     */
    public final long secBits;


    /**
     * max second
     */
    public final long maxSecond;
    /**
     * max node
     */
    public final long maxNode;
    /**
     * max serial
     */
    public final long maxSeq;
    /**
     * max random
     */
    public final long maxRand = ~(-1 << RAND_BITS);

    /**
     * the data section left shift
     */
    public final long timeShift;
    /**
     * the node section left shift
     */
    public final long nodeShift;
    /**
     * the serial section left shift
     */
    public final long seqShift = RAND_BITS;


    private long nodeId;


    public static long nextId() {
        return MeteorIdFactory.INSTANCE.nexId();
    }


    protected MeteorId(long initSecond, long nodeId, int nodeBits, int seqBits) {

        this.maxNode = ~(-1L << nodeBits);
        if (nodeId < 0 || nodeId > maxNode) {
            throw new IllegalArgumentException("Node id must between 0 and " + maxNode);
        }
        if (nodeId == 0) {
            try {
                String[] split = IpUtils.getLocalIp().split("\\.");
                long id = Long.parseLong(split[2]) << 8 | Long.parseLong(split[3]);
                this.nodeId = id & this.maxNode;

            } catch (Exception e) {
                this.nodeId = random.nextInt(1 << 8);
            }
        }
        this.secBits = 64 - 1 - nodeBits - seqBits - RAND_BITS;
        if (seqBits < 0) {
            throw new IllegalArgumentException("nodeBits or seqBits is too long");
        }
        this.maxSecond = ~(-1L << secBits);
        this.maxSeq = ~(-1L << seqBits);
        this.timeShift = nodeBits + seqBits + RAND_BITS;
        this.nodeShift = seqBits + RAND_BITS;
        this.seqNum = 0;
        this.randNum = 0;
        this.second = System.currentTimeMillis() / 1000 - initSecond;
    }

    private final SplittableRandom random = new SplittableRandom();

    public synchronized long nexId() {
        this.seqNum = (seqNum + 1) & maxSeq;
        randNum = random.nextInt((int) (maxRand + 1));
        if (seqNum == 0) {
            second = (second + 1) & maxSecond;
            if (second == 0) {
                throw new IllegalStateException("Seconds overflow. The max second is " + maxSecond);
            }
        }
        long l = second << timeShift | nodeId << nodeShift | seqNum << seqShift | randNum;
        if (l < 0) {
            throw new IllegalStateException("MeteorId seconds overflow");
        }
        return l;
    }


}
