package com.sondertara.notify.dingtalk.message;

import lombok.Data;

import java.util.List;

/**
 * @author huangxiaohu
 */
@Data
public class AtNode {

    private List<String> atMobiles;

    private boolean isAtAll;
}