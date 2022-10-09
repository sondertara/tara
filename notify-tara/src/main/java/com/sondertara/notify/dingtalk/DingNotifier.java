package com.sondertara.notify.dingtalk;

import com.alibaba.fastjson2.JSONObject;
import com.sondertara.common.model.ResultDTO;
import com.sondertara.common.util.CollectionUtils;
import com.sondertara.common.util.HttpUtils;
import com.sondertara.notify.Notifier;
import com.sondertara.notify.common.NotifyPlatform;
import com.sondertara.notify.common.NotifyPlatformEnum;
import com.sondertara.notify.dingtalk.message.AtNode;
import com.sondertara.notify.dingtalk.message.MarkdownMessage;
import com.sondertara.notify.dingtalk.util.DingTalkSignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.sondertara.notify.dingtalk.DingNotifyConst.DING_NOTICE_TITLE;

/**
 * DingNotifier related
 *
 * @author huangxiaohu
 * @since 1.0.0
 **/
@Slf4j
public class DingNotifier implements Notifier {

    @Override
    public String platform() {
        return NotifyPlatformEnum.DING.name().toLowerCase();
    }

    /**
     * Execute real ding send.
     *
     * @param platform send platform
     * @param text     send content
     */
    @Override
    public ResultDTO<String> send(NotifyPlatform platform, String text) {

        MarkdownMessage markdown = new MarkdownMessage();
        markdown.setTitle(DING_NOTICE_TITLE);
        markdown.add(text);
        AtNode at = new AtNode();

        List<String> mobiles = com.sondertara.common.util.StringUtils.split(platform.getReceivers(), ',');
        at.setAtMobiles(mobiles);
        if (CollectionUtils.isEmpty(mobiles)) {
            at.setAtAll(true);
        }
        markdown.setAt(at);

        String hookUrl = getTargetUrl(platform.getSecret(), platform.getUrlKey());
        try {
            String result = HttpUtils.sendPostJson(hookUrl, markdown.toJsonString());

            if (com.sondertara.common.util.StringUtils.isEmpty(result)) {
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
        } catch (Exception e) {
            log.error("DingTalk notify send failed...", e);
            return ResultDTO.fail(e.getMessage());
        }
    }

    /**
     * Build target url.
     *
     * @param secret      secret
     * @param accessToken accessToken
     * @return url
     */
    private String getTargetUrl(String secret, String accessToken) {
        if (StringUtils.isBlank(secret)) {
            return DingNotifyConst.DING_WEBHOOK + accessToken;
        }
        long timestamp = System.currentTimeMillis();
        String sign = DingTalkSignUtil.sign(timestamp, secret);
        return DingNotifyConst.DING_WEBHOOK + accessToken + "&timestamp=" + timestamp + "&sign=" + sign;
    }
}
