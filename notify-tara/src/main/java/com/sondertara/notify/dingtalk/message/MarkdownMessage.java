package com.sondertara.notify.dingtalk.message;

import com.alibaba.fastjson2.JSON;
import com.sondertara.common.exception.TaraException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangxiaohu
 */
public class MarkdownMessage implements NotifyMessage {

    private String title;

    private AtNode at;

    public AtNode getAt() {
        return at;
    }

    public void setAt(AtNode at) {
        this.at = at;
    }

    private List<String> items = new ArrayList<String>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void add(String text) {
        items.add(text);
    }

    public void addAll(List<String> textList) {
        items.addAll(textList);
    }

    public static String getBoldText(String text) {
        return "**" + text + "**";
    }

    public static String getItalicText(String text) {
        return "*" + text + "*";
    }

    public static String getLinkText(String text, String href) {
        return "[" + text + "](" + href + ")";
    }

    public static String getImageText(String imageUrl) {
        return "![image](" + imageUrl + ")";
    }

    public static String getHeaderText(int headerType, String text) {
        if (headerType < 1 || headerType > 6) {
            throw new TaraException("headerType should be in [1, 6]");
        }

        StringBuffer numbers = new StringBuffer();
        for (int i = 0; i < headerType; i++) {
            numbers.append("#");
        }
        return numbers + " " + text;
    }

    public static String getReferenceText(String text) {
        return "> " + text;
    }

    public static String getOrderListText(List<String> orderItem) {
        if (orderItem.isEmpty()) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 1; i <= orderItem.size() - 1; i++) {
            sb.append(String.valueOf(i) + ". " + orderItem.get(i - 1) + "  \n ");
        }
        sb.append(String.valueOf(orderItem.size()) + ". " + orderItem.get(orderItem.size() - 1));
        return sb.toString();
    }

    public static String getUnorderListText(List<String> unorderItem) {
        if (unorderItem.isEmpty()) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < unorderItem.size() - 1; i++) {
            sb.append("- " + unorderItem.get(i) + "  \n ");
        }
        sb.append("- " + unorderItem.get(unorderItem.size() - 1));
        return sb.toString();
    }

    @Override
    public String toJsonString() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("msgtype", "markdown");

        Map<String, Object> markdown = new HashMap<String, Object>();
        markdown.put("title", title);

        StringBuffer markdownText = new StringBuffer();
        for (String item : items) {
            markdownText.append(item + "  \n ");
        }

        markdown.put("text", markdownText.toString());
        result.put("markdown", markdown);
        result.put("at", JSON.toJSON(at));

        return JSON.toJSONString(result);
    }
}
