package com.sondertara.notify.dingtalk.util;

import com.sondertara.common.exception.TaraException;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;

/**
 * 当钉钉开启签名方式的安全设置时，需要签名拼接到url上
 * <p>
 * https://oapi.dingtalk.com/robot/send?access_token=XXXXXX&timestamp=XXX&sign=XXX
 * date 2019/12/2 6:01 下午
 *
 * @author huangxiaohu
 * @version 1.0
 * @since 1.0
 **/
public class DingTalkSignUtil {

    public static String sign(long timestamp, String secret) {
        try {
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            return URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
        } catch (Exception e) {
            throw new TaraException("generate sign error!", e);
        }
    }
}
