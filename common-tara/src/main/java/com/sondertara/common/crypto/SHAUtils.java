package com.sondertara.common.crypto;

import com.sondertara.common.exception.TaraException;
import com.sondertara.common.util.HexUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA加密，不可逆
 *
 * @author huangxiaohu
 */
public class SHAUtils {

    public static String sha1(String message) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA");
            byte[] shaEncode = sha1.digest(message.getBytes());
            return HexUtils.encodeHexStr(shaEncode);
        } catch (NoSuchAlgorithmException e) {
            throw new TaraException("SHA1 encrypt error", e);
        }
    }

    public static String sha256(String message) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] sha256Encode = sha256.digest(message.getBytes());
            return HexUtils.encodeHexStr(sha256Encode);

        } catch (NoSuchAlgorithmException e) {
            throw new TaraException("SHA256 encrypt error", e);
        }
    }

    public static String sha384(String message) {
        try {
            MessageDigest sha384 = MessageDigest.getInstance("SHA-384");
            byte[] sha384Encode = sha384.digest(message.getBytes());
            return HexUtils.encodeHexStr(sha384Encode);
        } catch (NoSuchAlgorithmException e) {
            throw new TaraException("SHA384 encrypt error", e);
        }
    }

    public static String sha512(String message) {
        try {
            MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
            byte[] sha512Encode = sha512.digest(message.getBytes());
            return HexUtils.encodeHexStr(sha512Encode);
        } catch (NoSuchAlgorithmException e) {
            throw new TaraException("SHA384 encrypt error", e);
        }
    }

}
