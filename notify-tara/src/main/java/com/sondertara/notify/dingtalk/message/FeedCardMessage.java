package com.sondertara.notify.dingtalk.message;

import com.alibaba.fastjson.JSON;
import com.sondertara.common.exception.TaraException;
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
            throw new TaraException("feedItems should not be null or empty");
        }
        for (FeedCardMessageItem item : feedItems) {
            if (StringUtils.isBlank(item.getTitle())) {
                throw new  TaraException("title should not be blank");
            }
            if (StringUtils.isBlank(item.getMessageURL())) {
                throw new  TaraException("messageURL should not be blank");
            }
            if (StringUtils.isBlank(item.getPicURL())) {
                throw new  TaraException("picURL should not be blank");
            }
        }
        feedCard.put("links", feedItems);
        items.put("feedCard", feedCard);

        return JSON.toJSONString(items);
    }
}
