package com.sondertara.notify.dingtalk.client;

import com.alibaba.fastjson.JSONObject;
import com.sondertara.common.model.ResultDTO;
import com.sondertara.common.util.HttpUtils;
import com.sondertara.common.util.StringUtils;
import com.sondertara.notify.dingtalk.message.DingTalkMessage;
import com.sondertara.notify.dingtalk.message.TextMessage;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;


/**
 * @author huangxiaohu
 */
public class DingTalkClient {

    private volatile static DingTalkClient dingTalkClient;

    private DingTalkClient() {
    }

    public static DingTalkClient getInstance() {
        if (dingTalkClient == null) {
            synchronized (DingTalkClient.class) {
                if (dingTalkClient == null) {
                    dingTalkClient = new DingTalkClient();
                }
            }
        }
        return dingTalkClient;
    }

    @SuppressWarnings("rawtypes")
    public ResultDTO sendTextMsg(String webhook, String message) {
        return sendTextMsg(webhook, message, null);
    }

    @SuppressWarnings("rawtypes")
    public ResultDTO sendTextMsg(String webhook, String message, List<String> atMobiles) {
        TextMessage textMessage = new TextMessage(message);
        textMessage.setIsAtAll(false);
        if (CollectionUtils.isNotEmpty(atMobiles)) {
            textMessage.setAtMobiles(atMobiles);
        }
        return send(webhook, textMessage);
    }

    @SuppressWarnings("rawtypes")
    public ResultDTO send(String webhook, DingTalkMessage message) {

        String result = HttpUtils.sendPostJson(webhook, message.toJsonString());

        if (StringUtils.isEmpty(result)) {
            return ResultDTO.fail("request error!");
        }
        JSONObject obj = JSONObject.parseObject(result);
        Integer errorCode = obj.getInteger("errcode");
        ResultDTO<String> resultDTO = new ResultDTO<>();
        resultDTO.setCode(errorCode.equals(0) ? "200" : String.valueOf(errorCode));
        resultDTO.setMsg(errorCode.equals(0) ? null : obj.getString("errmsg"));
        resultDTO.setSuccess(errorCode.equals(0));
        resultDTO.setData(errorCode.equals(0) ? "send success" : null);
        return resultDTO;
    }

}
