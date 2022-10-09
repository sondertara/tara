package com.sondertara.common.crypto;

import com.sondertara.common.exception.TaraException;
import com.sondertara.common.util.HexUtils;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密与解密工具类
 *
 * @author huangxiaohu
 */
public class AESUtils {
    static final String AES = "AES";
    static final String SHA1_PRNG = "SHA1PRNG";

    /**
     * 生成密钥
     */
    public static byte[] initKey(String key, int keySize) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
            SecureRandom random = SecureRandom.getInstance(SHA1_PRNG);
            random.setSeed(key.getBytes());
            keyGenerator.init(keySize, random);
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            throw new TaraException("Init secretKey error", e);
        }
    }

    /**
     * 加密
     *
     * @param plainText 明文
     * @param key       加解密密钥
     * @param keySize   密钥长度
     * @return String 密文
     */
    public static String encrypt(String plainText, String key, int keySize) {
        if (plainText == null || plainText.length() < 1) {
            return null;
        }
        try {

            byte[] enCodeFormat = initKey(key, keySize);
            SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, AES);
            Cipher cipher = Cipher.getInstance("AES");
            byte[] byteContent = plainText.getBytes(StandardCharsets.UTF_8);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] bytes = cipher.doFinal(byteContent);
            StringBuffer sb = new StringBuffer();
            for (byte aByte : bytes) {
                StringBuilder hex = new StringBuilder(Integer.toHexString(aByte & 0xFF));
                if (hex.length() == 1) {
                    hex.insert(0, '0');
                }
                sb.append(hex.toString().toUpperCase());
            }
            return sb.toString();
        } catch (Exception e) {
            throw new TaraException("AES encrypt error", e);
        }
    }

    /**
     * 解密
     *
     * @param cipherText 密文
     * @param key        加解密密钥
     * @param keySize    密钥长度
     * @return String 明文
     */
    public static String decrypt(String cipherText, String key, int keySize) {
        if (cipherText == null || cipherText.length() < 1) {
            return null;
        }
        if (cipherText.trim().length() < 19) {
            return cipherText;
        }
        byte[] bytesResult = HexUtils.decodeHex(cipherText);

        try {

            byte[] bytes = initKey(key, keySize);
            SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, AES);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] result = cipher.doFinal(bytesResult);
            return new String(result);
        } catch (Exception e) {
            throw new TaraException("AES decrypt error", e);
        }
    }

    public static void main(String[] args) {
        try {
            // 示例
            String cipherText = AESUtils.encrypt("一片春愁待酒浇，江上舟摇，楼上帘招。秋娘渡与泰娘桥，风又飘飘，雨又萧萧。", "666888", 256);
            System.out.println("密文: " + cipherText);

            String clearText = AESUtils.decrypt(cipherText, "666888", 256);
            System.out.println("明文：" + clearText);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
