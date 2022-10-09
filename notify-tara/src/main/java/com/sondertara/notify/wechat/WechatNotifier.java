package com.sondertara.notify.wechat;

import com.alibaba.fastjson2.JSONObject;
import com.sondertara.common.model.ResultDTO;
import com.sondertara.common.util.HttpUtils;
import com.sondertara.notify.Notifier;
import com.sondertara.notify.common.NotifyPlatform;
import com.sondertara.notify.common.NotifyPlatformEnum;
import com.sondertara.notify.wechat.message.MarkdownReq;
import lombok.extern.slf4j.Slf4j;

/**
 * WechatNotifier related
 *
 * @author huangxiaohu
 * @since 1.0.0
 **/
@Slf4j
public class WechatNotifier implements Notifier {

    @Override
    public String platform() {
        return NotifyPlatformEnum.WECHAT.name().toLowerCase();
    }

    /**
     * Execute real wechat send.
     *
     * @param platform send platform
     * @param text     send content
     */
    @Override
    public ResultDTO<String> send(NotifyPlatform platform, String text) {
        String serverUrl = WechatNotifyConst.WECHAT_WEH_HOOK + platform.getUrlKey();
        MarkdownReq markdownReq = new MarkdownReq();
        markdownReq.setMsgtype("markdown");
        MarkdownReq.Markdown markdown = new MarkdownReq.Markdown();
        markdown.setContent(text);
        markdownReq.setMarkdown(markdown);
        try {
            String result = HttpUtils.sendPostJson(serverUrl, markdownReq.toJsonString());

            if (com.sondertara.common.util.StringUtils.isEmpty(result)) {
                return ResultDTO.fail("request error!");
            }
            JSONObject obj = JSONObject.parseObject(result);
            Integer errorCode = obj.getInteger("errcode");
            ResultDTO<String> resultDTO = new ResultDTO<>();
            resultDTO.setCode(errorCode.equals(0) ? "200" : String.valueOf(errorCode));
            resultDTO.setMsg(errorCode.equals(0) ? null : obj.getString("errmsg"));
            resultDTO.setSuccess(errorCode.equals(0));
            resultDTO.setData(errorCode.equals(0) ? result : null);
            return resultDTO;
        } catch (Exception e) {
            log.error("Wechat notify send failed...", e);
            return ResultDTO.fail(e.getMessage());
        }
    }
}
