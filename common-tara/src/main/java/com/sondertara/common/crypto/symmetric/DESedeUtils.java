package com.sondertara.common.crypto.symmetric;

import com.sondertara.common.exception.TaraException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author huangxiaohu
 * @date 2021/2/22
 */

public class DESedeUtils {

    static final String DES_EDE = "DESede";

    /**
     * 生成秘钥
     */
    public static byte[] initKey() {

        KeyGenerator keyGen;
        try {
            // 秘钥生成器
            keyGen = KeyGenerator.getInstance(DES_EDE);
            // 初始秘钥生成器
            keyGen.init(168, new SecureRandom());
            // 生成秘钥
            SecretKey secretKey = keyGen.generateKey();
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            throw new TaraException("DESede init key error", e);
        }
    }

    /**
     * 加密
     *
     * @return bytes
     */
    public static byte[] encrypt(byte[] data, byte[] key) {

        SecretKey secretKey = new SecretKeySpec(key, DES_EDE);
        Cipher cipher;
        byte[] cipherBytes;
        try {
            cipher = Cipher.getInstance(DES_EDE);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            cipherBytes = cipher.doFinal(data);
            return cipherBytes;
        } catch (Exception e) {
            throw new TaraException("DESede encrypt key error", e);
        }
    }

    /**
     * 解密
     */
    public static byte[] decrypt(byte[] data, byte[] key) {

        SecretKey secretKey = new SecretKeySpec(key, DES_EDE);
        Cipher cipher;
        byte[] plainBytes;

        try {
            cipher = Cipher.getInstance(DES_EDE);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            plainBytes = cipher.doFinal(data);
        } catch (Exception e) {
            throw new TaraException("DESede encrypt key error", e);
        }

        return plainBytes;
    }
}
