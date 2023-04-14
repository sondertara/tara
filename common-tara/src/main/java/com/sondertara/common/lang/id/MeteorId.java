package com.sondertara.common.lang.id;

/**
 * Based on SnowFlake
 * bit:     0     1~29  30~49  50~60   61~63
 * section: keep  data  node   serial  random
 */
public class MeteorId {
    /**
     * initial value is 2022-01-01 00:00:00
     */
    private final static long initSecond = 1640966400L;
    /**
     * node section number bits
     */
    private final static long nodeBits = 20;

    /**
     * serial section number bits
     */
    private final static long seqBits = 11;

    /**
     * random  section number bits
     */

    private final static long randBits = 3;


    /**
     * the high bits of data section
     */
    private final static long secBits = 64 - 1 - nodeBits - seqBits - randBits;

    /**
     * max second
     */
    private final static long maxSecond = ~(-1 << secBits);
    /**
     * max node
     */
    private final static long maxNode = ~(-1 << nodeBits);
    /**
     * max data
     */
    private final static long maxSeq = ~(-1 << secBits);
    /**
     * max random
     */
    private final static long maxRand = ~(-1 << randBits);

    /**
     * the data section left shift
     */
    private final static long timeShift = nodeBits + seqBits + randBits;
    /**
     * the node section left shift
     */
    private final static long nodeShift = seqBits + randBits;
    /**
     * the serial section left shift
     */
    private final static long seqShift = randBits;


    private static final Node INSTANCE = new Node(0);

    public static long nextId() {
        return INSTANCE.nexId();
    }


    public static void setNodeId(long nodeId) {
        INSTANCE.setNodeId(nodeId);
    }

    static class Node {
        private long seqNum;
        private long randNum;
        private long second;
        private long seed;
        private long nodeId;

        public Node(long nodeId) {
            if (nodeId < 0 || nodeId > maxNode) {
                throw new IllegalArgumentException("Node id must between 0 and " + maxNode);
            }
            this.nodeId = nodeId;
            this.seqNum = 0;
            this.randNum = 0;
            this.seed = 1;
            this.second = System.currentTimeMillis() / 1000 - initSecond;
        }

        public synchronized long nexId() {
            this.seqNum = (seqNum + 1) & maxSeq;
            randNum = rand(this);
            if (seqNum == 0) {
                second = (second + 1) & maxSecond;
                if (second == 0) {
                    throw new IllegalStateException("Seconds overflow. The max second is " + maxSecond);
                }
            }
            long l = second << timeShift | nodeId << nodeShift | seqNum << seqShift | randNum;
            if (l < 0) {
                System.out.println("Seconds overflow");
            }
            return l;
        }

        public synchronized void setNodeId(long nodeId) {
            this.nodeId = nodeId;
        }
    }

    /**
     * Generate random number
     * base on xor shift
     *
     * @param node the node
     * @return the random number
     */
    private static long rand(Node node) {
        long x = node.seed;
        x ^= x << 13;
        x ^= x << 17;
        x ^= x << 5;
        node.seed = x % MeteorId.maxRand;
        return node.seed;
    }

}
