package com.sondertara.common.id.snowflake;

public interface SnowflakeIdWorkerProvider {
    SnowflakeIdWorker get();

    String getProviderId();
}
