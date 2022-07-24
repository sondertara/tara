package com.sondertara.notify.dingtalk.message;

import com.alibaba.fastjson2.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangdongbo
 * @since 2019/3/5.
 */
public class ActionCardSingleMessage implements DingTalkMessage {

    private String title;

    private String text;

    private String singleTitle;

    private String singleURL;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSingleTitle() {
        return singleTitle;
    }

    public void setSingleTitle(String singleTitle) {
        this.singleTitle = singleTitle;
    }

    public String getSingleURL() {
        return singleURL;
    }

    public void setSingleURL(String singleURL) {
        this.singleURL = singleURL;
    }

    @Override
    public String toJsonString() {
        Map<String, Object> message = new HashMap<>(4);
        message.put("title", title);
        message.put("text", text);
        message.put("singleTitle", singleTitle);
        message.put("singleURL", singleURL);
        Map<String, Object> card = new HashMap<>(4);
        card.put("msgtype", "actionCard");
        card.put("actionCard", message);
        return JSON.toJSONString(card);
    }
}
