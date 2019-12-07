package com.sondertara.notify.dingtalk.message;

/**
 *  @author huangxiaohu
 */
public interface DingTalkMessage {

    /**
     * 返回消息的Json格式字符串
     *
     * @return 消息的Json格式字符串
     */
    String toJsonString();
}
