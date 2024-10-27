package com.sondertara.common.codec;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.text.StringUtils;
import com.sondertara.common.codec.base64.Base64;
import com.sondertara.common.codec.hex.Hex;

/**
 *  */
public class Stringifys {
    public static String stringify(byte[] bytes, StringifyFormat format) {
        format = ObjectUtils.defaultIfEmpty(format, StringifyFormat.UTF8);
        switch (format) {
            case HEX:
                return Hex.encodeHexString(bytes);
            case BASE64:
                return Base64.encodeBase64ToString(bytes);
            default:
                return StringUtils.newStringUtf8(bytes);
        }
    }

    public static byte[] toBytes(String text, StringifyFormat format ){
        format = ObjectUtils.defaultIfEmpty(format, StringifyFormat.UTF8);
        byte[] bytes;
        switch (format) {
            case HEX:
                bytes = Hex.decodeHex(text);
                break;
            case BASE64:
                bytes = Base64.decodeBase64(text);
                break;
            default:
                bytes = StringUtils.getBytesUtf8(text);
                break;
        }
        return bytes;
    }
}
