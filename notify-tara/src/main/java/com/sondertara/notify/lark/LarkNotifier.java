package com.sondertara.notify.lark;

import com.alibaba.fastjson2.JSONObject;
import com.sondertara.common.model.ResultDTO;
import com.sondertara.common.util.StringUtils;
import com.sondertara.notify.Notifier;
import com.sondertara.notify.common.NotifyPlatform;
import com.sondertara.notify.common.NotifyPlatformEnum;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;

/**
 * LarkNotifier
 *
 * @author huangxiaohu
 * @version v1.0
 * @since 2022/4/28 23:25
 */
@Slf4j
public class LarkNotifier implements Notifier {

    @Override
    public String platform() {
        return NotifyPlatformEnum.LARK.name().toLowerCase();
    }

    /**
     * Execute real Lark send.
     *
     * @param notifyPlatform {@link NotifyPlatform}
     * @param text           send content
     */
    @Override
    public ResultDTO<String> send(NotifyPlatform notifyPlatform, String text) {
        String serverUrl = LarkNotifyConst.LARK_WEBHOOK + notifyPlatform.getUrlKey();
        try {

            String json = Unirest.post(serverUrl).body(text).asString().getBody();

            if (StringUtils.isEmpty(json)) {
                return ResultDTO.fail("request error!");
            }
            JSONObject obj = JSONObject.parseObject(json);
            Integer errorCode = obj.getInteger("code");
            ResultDTO<String> resultDTO = new ResultDTO<>();
            resultDTO.setCode(errorCode.equals(0) ? "200" : String.valueOf(errorCode));
            resultDTO.setMsg(errorCode.equals(0) ? null : obj.getString("errmsg"));
            resultDTO.setSuccess(errorCode.equals(0));
            if (resultDTO.getSuccess()) {
                resultDTO.setData(obj.getJSONObject("data").toJSONString());
            }
            return resultDTO;

        } catch (Exception e) {
            log.error("Lark send fail...", e);
            return ResultDTO.fail(e.getMessage());
        }
    }

}
