package com.sondertara.common.lang.id;

import com.sondertara.common.util.IpUtils;
import lombok.Data;

/**
 * @author huangxiaohu
 */
public class MeteorIdFactory {
    private static long nodeId = 1;

    static {
        try {
            String localIp = IpUtils.getLocalIp();
            long ipv4ToLong = IpUtils.ipv4ToLong(localIp);
            nodeId = ipv4ToLong & (~(-1L << MeteorId.NODE_BITS));
        } catch (Exception ignored) {

        }
    }

    static MeteorId INSTANCE = MeteorIdFactory.newInstance().create();

    @Data
    public static class Builder {
        private long nodeId = 0;
        private int nodeBits = MeteorId.NODE_BITS;
        private int seqBits = MeteorId.SEQ_BITS;
        private long initSecond = MeteorId.INIT_SECOND;

        public Builder withNodeId(long nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public Builder withNodeBits(int nodeBits) {
            this.nodeBits = nodeBits;
            return this;
        }

        public Builder withInitSecond(long initSecond) {
            this.initSecond = initSecond;
            return this;
        }

        public Builder withSeqBits(int seqBits) {
            this.seqBits = seqBits;
            return this;
        }

        public MeteorIdFactory build() {
            return new MeteorIdFactory(this);
        }
    }

    public static MeteorIdFactory newInstance() {
        return builder().withNodeId(nodeId).build();
    }

    public static MeteorIdFactory newInstance(long nodeId) {
        return builder().withNodeId(nodeId).build();
    }

    private final Builder builder;

    private MeteorIdFactory(Builder builder) {
        this.builder = builder;

    }

    public synchronized MeteorId create() {
        return new MeteorId(builder.initSecond, builder.nodeId, builder.nodeBits, builder.seqBits);
    }

    public static Builder builder() {
        return new Builder();
    }


}
