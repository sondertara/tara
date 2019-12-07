package com.sondertara.notify.dingtalk.message;

import com.alibaba.fastjson.JSON;
import com.sondertara.common.exception.TaraException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huangxiaohu
 */
public class LinkMessage implements DingTalkMessage {

    private String title;
    private String text;
    private String picUrl;
    private String messageUrl;

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

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getMessageUrl() {
        return messageUrl;
    }

    public void setMessageUrl(String messageUrl) {
        this.messageUrl = messageUrl;
    }


    @Override
    public String toJsonString() {
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("msgtype", "link");

        Map<String, String> linkContent = new HashMap<String, String>();
        if (StringUtils.isBlank(title)) {
            throw new TaraException("title should not be blank");
        }
        linkContent.put("title", title);

        if (StringUtils.isBlank(messageUrl)) {
            throw new  TaraException("messageUrl should not be blank");
        }
        linkContent.put("messageUrl", messageUrl);

        if (StringUtils.isBlank(text)) {
            throw new  TaraException("text should not be blank");
        }
        linkContent.put("text", text);

        if (StringUtils.isNotBlank(picUrl)) {
            linkContent.put("picUrl", picUrl);
        }

        items.put("link", linkContent);

        return JSON.toJSONString(items);
    }
}
