package com.sondertara.notify.dingtalk.client;

import com.sondertara.common.model.ResultDTO;
import com.sondertara.common.util.PropertiesUtil;
import com.sondertara.notify.dingtalk.util.DingTalkSignUtil;
import org.junit.jupiter.api.Test;

/**
 * dingtalk test.
 * <p>
 * date 2019/12/2 3:17 下午
 *
 * @author huangxiaohu
 * @version 1.0
 * @since 1.0
 **/
public class DingTalkClientTest {
    @Test
    public void sendText() {

        String url = PropertiesUtil.getInstance("dingtalk").getProperty("webhook");
        // direct send dingtalk msg;
        {
            ResultDTO resultDTO = DingTalkClient.getInstance().sendTextMsg(url, "我是谁");
            System.out.println(resultDTO);
        }
        //send dingtalk msg with secret;
        {
            long timeMillis = System.currentTimeMillis();
            String sign = DingTalkSignUtil.sign(timeMillis, PropertiesUtil.getInstance("dingtalk").getProperty("secret"));

            String webhook = url + "&timestamp=" + timeMillis + "&sign=" + sign;

            ResultDTO resultDTO = DingTalkClient.getInstance().sendTextMsg(webhook, "love you");
            System.out.println(resultDTO);
        }

    }

}