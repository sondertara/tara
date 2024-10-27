package com.sondertara.common.codec.z85;

import com.sondertara.common.codec.CodecException;
import com.sondertara.common.codec.Decoder;

public class Z85Decoder implements Decoder<String, byte[]> {
    @Override
    public byte[] decode(String string) throws CodecException {
        int remainder = string.length() % 5;
        int padding = 5 - (remainder == 0 ? 5 : remainder);
        StringBuilder stringBuilder = new StringBuilder(string);
        for (int p = 0; p < padding; ++p) {
            stringBuilder.append(Z85.encodeTable[Z85.encodeTable.length - 1]);
        }
        string = stringBuilder.toString();
        int length = string.length();
        byte[] bytes = new byte[(length * 4 / 5) - padding];
        long value = 0;
        int index = 0;
        for (int i = 0; i < length; ++i) {
            int code = string.charAt(i) - 32;
            value = value * 85 + Z85.decodeTable[code];
            if ((i + 1) % 5 == 0) {
                int divisor = 256 * 256 * 256;
                while (divisor >= 1) {
                    if (index < bytes.length) {
                        bytes[index++] = (byte)((value / divisor) % 256);
                    }
                    divisor /= 256;
                }
                value = 0;
            }
        }
        return bytes;
    }
}
