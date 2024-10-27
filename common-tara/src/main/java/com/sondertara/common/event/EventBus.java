package com.sondertara.common.event;

import com.sondertara.common.base.Named;

/**
 *
 * @author huangxiaohu.1ih
 */
public interface EventBus extends Named {
    /**
     * 发布一个 event 消息
     */
  <T> void publish(DomainEvent<T> event);

    /**
     * @return 返回 bus route 名（可以理解为 bus 路线名称）
     */
    @Override
    String getName();
}
