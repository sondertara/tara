package com.sondertara.notify.dingtalk.message;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  @author huangxiaohu
 */
public class FeedCardMessage implements DingTalkMessage {

    private List<FeedCardMessageItem> feedItems;

    public List<FeedCardMessageItem> getFeedItems() {
        return feedItems;
    }

    public void setFeedItems(List<FeedCardMessageItem> feedItems) {
        this.feedItems = feedItems;
    }

    @Override
    public String toJsonString() {
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("msgtype", "feedCard");

        Map<String, Object> feedCard = new HashMap<String, Object>();

        if (feedItems == null || feedItems.isEmpty()) {
            throw new IllegalArgumentException("feedItems should not be null or empty");
        }
        for (FeedCardMessageItem item : feedItems) {
            if (StringUtils.isBlank(item.getTitle())) {
                throw new IllegalArgumentException("title should not be blank");
            }
            if (StringUtils.isBlank(item.getMessageURL())) {
                throw new IllegalArgumentException("messageURL should not be blank");
            }
            if (StringUtils.isBlank(item.getPicURL())) {
                throw new IllegalArgumentException("picURL should not be blank");
            }
        }
        feedCard.put("links", feedItems);
        items.put("feedCard", feedCard);

        return JSON.toJSONString(items);
    }
}
