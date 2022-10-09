package com.sondertara.notify.wechat.message;

import com.alibaba.fastjson2.JSON;
import com.sondertara.notify.dingtalk.message.AtNode;
import com.sondertara.notify.dingtalk.message.NotifyMessage;
import lombok.Data;

/**
 * MarkdownReq related
 *
 * @author huangxiaohu
 * @since 1.0.0
 */
@Data
public class MarkdownReq implements NotifyMessage {

    /**
     * msgType
     */
    private String msgtype;

    /**
     * markdown
     */
    private Markdown markdown;

    private AtNode at;

    @Override
    public String toJsonString() {
        return JSON.toJSONString(this);
    }

    @Data
    public static class Markdown {
        private String title;

        // for wechat
        private String content;
    }

}
