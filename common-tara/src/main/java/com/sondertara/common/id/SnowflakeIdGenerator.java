package com.sondertara.common.id;

import com.sondertara.common.id.snowflake.SnowflakeIdWorkerProviderLoader;

public class SnowflakeIdGenerator implements IdGenerator<Long> {

    @Override
    public Long get() {
        return SnowflakeIdWorkerProviderLoader.getProvider().get().nextId();
    }
}
