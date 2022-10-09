package com.sondertara.common.crypto.symmetric;

import com.sondertara.common.exception.TaraException;
import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * DES加密说明
 * <p>
 * DES是一种对称加密算法，对称加密即：加密和解密使用相同密钥的算法。
 * <p>
 * 注意：DES加密和解密过程中，密钥长度必须是8的倍数;
 *
 * @author huangxiaohu
 */
public class DESUtils {

    static final String DES = "DES";

    /**
     * 加密过程
     *
     * @param plainBytes 原始信息
     * @param password   密码
     * @return base64编码
     */
    public static byte[] encrypt(byte[] plainBytes, String password) {

        try {
            password = formatPassword(password);
            // DES算法要求有一个可信任的随机数源
            SecureRandom secureRandom = new SecureRandom();

            // 创建一个DESKeySpec对象
            DESKeySpec desKeySpec = new DESKeySpec(password.getBytes());

            // 创建密匙工厂
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(DES);

            // 将密码利用密匙工厂转换成密匙
            SecretKey securekey = secretKeyFactory.generateSecret(desKeySpec);

            // 创建Cipher对象，用于完成实际加密操作
            Cipher cipher = Cipher.getInstance(DES);

            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, secureRandom);

            // 执行加密操作并返回密文
            return cipher.doFinal(plainBytes);

        } catch (Throwable e) {
            throw new TaraException("DES encrypt error.", e);
        }
    }

    /**
     * 解密
     *
     * @param cipherBytes 密文
     * @param password    密码 String
     * @return byte[]
     */

    public static byte[] decrypt(byte[] cipherBytes, String password) {

        try {
            password = formatPassword(password);
            // DES算法要求有一个可信任的随机数源
            SecureRandom random = new SecureRandom();

            // 创建一个DESKeySpec对象
            DESKeySpec desKey = new DESKeySpec(password.getBytes());

            // 创建一个密匙工厂
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

            // 将DESKeySpec对象转换成SecretKey对象
            SecretKey securekey = keyFactory.generateSecret(desKey);

            // Cipher对象，用于完成实际解密操作
            Cipher cipher = Cipher.getInstance(DES);

            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, securekey, random);

            // 开始解密
            return cipher.doFinal(cipherBytes);
        } catch (Exception e) {
            throw new TaraException("DES decrypt error", e);
        }
    }

    /**
     * 加密，返回Base64字符串
     *
     * @param plainText 明文
     * @param password  密码
     * @return Base64 str
     */
    public static String encrypt(String plainText, String password) {
        byte[] bytes = encrypt(plainText.getBytes(StandardCharsets.UTF_8), password);
        return Base64.encodeBase64String(bytes);
    }

    /**
     * 解密
     *
     * @param base64Str Base64 密文
     * @param password  密码
     * @return 明文
     */
    public static String decrypt(String base64Str, String password) {
        byte[] bytes = Base64.decodeBase64(base64Str);
        byte[] decrypt = decrypt(bytes, password);
        return new String(decrypt, StandardCharsets.UTF_8);
    }

    private static String formatPassword(String password) {
        int i = password.length() % 8;
        if (i != 0) {
            StringBuilder buffer = new StringBuilder(password);
            for (int j = i; j < 8; j++) {
                buffer.append(0);
            }

            return buffer.toString();
        }
        return password;
    }

    public static void main(String[] args) {

        String formatPassword = formatPassword("1");
        System.out.println(formatPassword);
        // 待加密内容
        String srcStr = "春宵一刻值千金，花有清香月有阴；歌管楼台声细细，秋千院落夜沉沉！";
        // 密码，长度必须是8的倍数
        String password = "ABCDEFGH12345678";
        String encrypt = encrypt(srcStr, password);
        System.out.println(encrypt);
        String decrypt = decrypt(encrypt, password);
        System.out.println(decrypt);
    }

}
