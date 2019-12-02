package com.sondertara.notify.dingtalk.message;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author huangxiaohu
 */
@Data
@AllArgsConstructor
public class ActionCardAction {
    /**
     * title
     */
    private String title;
    /**
     * url
     */
    private String actionURL;
}
