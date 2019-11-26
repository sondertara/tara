package com.sondertara.common.util;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ServiceUnavailableRetryStrategy;

/**
 * 针对请求地址可达，非200 响应码进行重试
 * <p>
 * * @author huangxiaohu
 * * @since 2018/11/13 12:35 PM
 */
public class CustomServiceUnavailableRetryStrategy implements ServiceUnavailableRetryStrategy {

    private int executionCount;
    private long retryInterval;

    CustomServiceUnavailableRetryStrategy(Builder builder) {
        this.executionCount = builder.executionCount;
        this.retryInterval = builder.retryInterval;
    }

    /**
     * retry逻辑
     */
    @Override
    public boolean retryRequest(HttpResponse response, int executionCount, org.apache.http.protocol.HttpContext context) {
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK && executionCount <= this.executionCount) {
            System.out.println(String.format("响应码为:%s,需要重新请求", response.getStatusLine().getStatusCode()));

            return true;
        } else {
            return false;
        }
    }

    /**
     * retry间隔时间
     */
    @Override
    public long getRetryInterval() {
        return this.retryInterval;
    }

    /**
     * 获取重试控制器
     */

    public static final class Builder {
        private int executionCount;
        private long retryInterval;

        public Builder() {
            executionCount = 3;
            retryInterval = 5000;
        }

        public Builder executionCount(int executionCount) {
            this.executionCount = executionCount;
            return this;
        }

        public Builder retryInterval(long retryInterval) {
            this.retryInterval = retryInterval;
            return this;
        }

        public CustomServiceUnavailableRetryStrategy build() {
            return new CustomServiceUnavailableRetryStrategy(this);
        }
    }

}
