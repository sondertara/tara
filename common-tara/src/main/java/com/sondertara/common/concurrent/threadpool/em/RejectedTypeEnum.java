/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sondertara.common.concurrent.threadpool.em;

import com.sondertara.common.concurrent.threadpool.exceptions.ThreadPoolCreateException;
import com.sondertara.common.concurrent.threadpool.policy.AbortPolicyWithReport;
import com.sondertara.common.concurrent.threadpool.policy.CallerRunsPolicyWithReport;
import com.sondertara.common.concurrent.threadpool.policy.DiscardOldestPolicyWithReport;
import com.sondertara.common.concurrent.threadpool.policy.DiscardPolicyWithReport;
import com.sondertara.common.concurrent.threadpool.policy.RejectedPolicyWithReport;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.RejectedExecutionHandler;

/**
 * RejectedTypeEnum related
 *
 * @author yanhom
 *  **/
@Slf4j
@Getter
public enum RejectedTypeEnum {

    /**
     * RejectedExecutionHandler type while triggering reject policy.
     */
    ABORT_POLICY(1, "AbortPolicy"),

    CALLER_RUNS_POLICY(2, "CallerRunsPolicy"),

    DISCARD_OLDEST_POLICY(3, "DiscardOldestPolicy"),

    DISCARD_POLICY(4, "DiscardPolicy"),
    REJECTED_POLICY(5, "RejectedPolicy");

    private final int code;

    private final String name;

    RejectedTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RejectedExecutionHandler buildRejectedHandler(String rejectType, @Nullable String poolName) {
        if (Objects.equals(rejectType, ABORT_POLICY.getName())) {
            return new AbortPolicyWithReport(poolName);
        } else if (Objects.equals(rejectType, CALLER_RUNS_POLICY.getName())) {
            return new CallerRunsPolicyWithReport(poolName);
        } else if (Objects.equals(rejectType, DISCARD_OLDEST_POLICY.getName())) {
            return new DiscardOldestPolicyWithReport(poolName);
        } else if (Objects.equals(rejectType, DISCARD_POLICY.getName())) {
            return new DiscardPolicyWithReport(poolName);
        } else if (Objects.equals(rejectType, REJECTED_POLICY.getName())) {
            return new RejectedPolicyWithReport(poolName);
        }


        log.error("Cannot find specified rejectedHandler {}", rejectType);
        throw new ThreadPoolCreateException("Cannot find specified rejectedHandler " + rejectType);
    }
}
