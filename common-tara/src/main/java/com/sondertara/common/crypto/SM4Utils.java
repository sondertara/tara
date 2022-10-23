package com.sondertara.common.crypto;

import com.sondertara.common.exception.TaraException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

/**
 * SM4加密与解密验证工具类
 *
 * @author huangxiaohu
 */
public class SM4Utils {
    private static final String ENCODING = "UTF-8";
    private static final String ALGORITHM_NAME = "SM4";
    /**
     * 加密算法/分组加密模式/分组填充方式
     * PKCS5Padding-以8个字节为一组进行分组加密
     * 定义分组加密模式使用：PKCS5Padding
     */
    public static final String ALGORITHM_NAME_ECB_PADDING = "SM4/ECB/PKCS5Padding";
    /**
     * 128-32位16进制；256-64位16进制
     */
    public static final int DEFAULT_KEY_SIZE = 128;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 生成ECB密码本
     * ECB模式（电子密码本模式：Electronic codebook）
     *
     * @param algorithmName 算法名称
     * @param mode          模式
     * @param key           the key
     * @return Cipher
     */
    private static Cipher generateEcbCipher(String algorithmName, int mode, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance(algorithmName, BouncyCastleProvider.PROVIDER_NAME);
            Key sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
            cipher.init(mode, sm4Key);
            return cipher;
        } catch (Exception e) {
            throw new TaraException("SM4 generateEcbCipher error", e);
        }
    }

    /**
     * 生成密钥：系统自动生成密钥
     *
     * @param keySize 密钥长度，可用DEFAULT_KEY_SIZE
     */
    public static byte[] autoGenerateKey(int keySize) {
        try {
            KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_NAME, BouncyCastleProvider.PROVIDER_NAME);
            kg.init(keySize, new SecureRandom());
            return kg.generateKey().getEncoded();
        } catch (Exception e) {
            throw new TaraException("SM4 generate key error", e);
        }
    }

    /**
     * SM4加密：采集自己提供16进制密钥
     * <p>
     * 加密模式：ECB 密文长度不固定，会随着被加密字符串长度的变化而变化
     *
     * @param key    16进制密钥（忽略大小写）或采集autoGenerateKey自动生成：String
     *               key=ByteUtils.toHexString(autoGenerateKey(DEFAULT_KEY_SIZE))
     * @param srcStr 待加密字符串
     * @return 返回16进制的加密字符串
     */
    public static String encryptByEcb(String srcStr, String key) {
        try {
            // 16进制字符串byte[]
            byte[] keyData = ByteUtils.fromHexString(key);
            // String byte[]
            byte[] srcData = srcStr.getBytes(ENCODING);
            // 加密后的数组
            byte[] cipherArray = encryptEcbPadding(keyData, srcData);
            // 返回加密后的16进制字符串
            return ByteUtils.toHexString(cipherArray);
        } catch (Exception e) {
            throw new TaraException("SM4 encryptByEcb error", e);
        }

    }

    /**
     * 加密模式
     *
     * @param key  密钥
     * @param data 数据
     */
    public static byte[] encryptEcbPadding(byte[] key, byte[] data) throws Exception {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * SM4解密
     * 解密模式：采用ECB密码本解密
     *
     * @param hexKey     16进制密钥
     * @param cipherText 16进制的加密字符串（忽略大小写）
     * @return 解密后的字符串
     */
    public static String decryptEcb(String hexKey, String cipherText) {
        try {
            // hexString byte[]
            byte[] keyData = ByteUtils.fromHexString(hexKey);
            // hexString byte[]
            byte[] cipherData = ByteUtils.fromHexString(cipherText);
            // 解密
            byte[] srcData = decryptEcbPadding(keyData, cipherData);

            // 返回解密后的明文
            return new String(srcData, ENCODING);
        } catch (Exception e) {
            throw new TaraException("SM4 decryptEcb error", e);
        }

    }

    /**
     * 调用密码本解密
     */
    public static byte[] decryptEcbPadding(byte[] key, byte[] cipherText) {
        try {
            Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(cipherText);
        } catch (Exception e) {
            throw new TaraException("SM4 decryptEcbPadding error", e);
        }
    }

    /**
     * 加密数据校验：校验加密前后的字符串是否为同一数据
     *
     * @param hexKey     16进制密钥（忽略大小写）
     * @param cipherText 16进制加密后的字符串
     * @param paramStr   加密前的字符串
     * @return 是否为同一数据
     */
    public static boolean verifyByEcb(String hexKey, String cipherText, String paramStr) {
        try {
            // hexString byte[]
            byte[] keyData = ByteUtils.fromHexString(hexKey);
            // 将16进制字符串转换成数组
            byte[] cipherData = ByteUtils.fromHexString(cipherText);
            // 解密
            byte[] decryptData = decryptEcbPadding(keyData, cipherData);
            // 将原字符串转换成byte[]
            byte[] srcData = paramStr.getBytes(ENCODING);
            // 判断2个数组是否一致
            return Arrays.equals(decryptData, srcData);
        } catch (Exception e) {
            throw new TaraException("SM4 verifyByEcb error", e);
        }
    }

    /**
     * 测试
     */
    public static void main(String[] args) {

        String srcStr = "春宵一刻值千金，花有清香月有阴；歌管楼台声细细，秋千院落夜沉沉！";
        // 自定义的32位16进制密钥
        // String key = "86C63180C2806ED1F47B859DE501215B";
        // 自动生成密钥
        String key = ByteUtils.toHexString(autoGenerateKey(DEFAULT_KEY_SIZE));
        // 加密
        String cipher = SM4Utils.encryptByEcb(srcStr, key);

        System.out.println("自动生成的密钥：" + key);
        // 密文输出
        System.out.println("加密后的密文：" + cipher);
        // 校验
        System.out.println("校验密文是否为明文加密所得：" + SM4Utils.verifyByEcb(key, cipher, srcStr));
        // 解密
        srcStr = SM4Utils.decryptEcb(key, cipher);
        System.out.println("采用密钥：" + key);
        System.out.println("解密后的明文：" + srcStr);

    }
}
