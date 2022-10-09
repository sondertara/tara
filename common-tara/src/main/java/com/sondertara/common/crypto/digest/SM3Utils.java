package com.sondertara.common.crypto.digest;

import com.sondertara.common.exception.TaraException;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import java.security.Security;
import java.util.Arrays;

/**
 * SM3加密与验证工具类
 *
 * @author huangxiaohu
 */
public class SM3Utils {

    private static final String ENCODING = "UTF-8";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * SM3加密方式之：不提供密钥的方式 SM3加密，返回加密后长度为64位的16进制字符串
     *
     * @param src 明文
     * @return str
     */
    public static String encrypt(String src) {
        return ByteUtils.toHexString(getEncryptBySrcByte(src.getBytes()));

    }

    /**
     * 返回长度为32位的加密后的byte数组
     *
     * @param srcByte byte
     * @return array
     */
    public static byte[] getEncryptBySrcByte(byte[] srcByte) {
        SM3Digest sm3 = new SM3Digest();
        sm3.update(srcByte, 0, srcByte.length);
        byte[] encryptByte = new byte[sm3.getDigestSize()];
        sm3.doFinal(encryptByte, 0);
        return encryptByte;
    }

    /**
     * 加密
     *
     * @param src 明文
     * @param key 密钥
     * @return encrypt
     */
    public static String encrypt(String src, String key) {
        return ByteUtils.toHexString(getEncryptByKey(src, key));

    }

    /**
     * SM3加密方式之： 根据自定义密钥进行加密，返回加密后长度为32位的16进制字符串
     *
     * @param src 源数据
     * @param key 密钥
     */
    public static byte[] getEncryptByKey(String src, String key) {
        try {
            byte[] srcByte = src.getBytes(ENCODING);
            byte[] keyByte = key.getBytes(ENCODING);
            KeyParameter keyParameter = new KeyParameter(keyByte);
            SM3Digest sm3 = new SM3Digest();
            HMac hMac = new HMac(sm3);
            hMac.init(keyParameter);
            hMac.update(srcByte, 0, srcByte.length);
            byte[] result = new byte[hMac.getMacSize()];
            hMac.doFinal(result, 0);
            return result;
        } catch (Exception e) {
            throw new TaraException("SM3 encrypt error", e);
        }
    }

    /**
     * 校验源数据与加密数据是否一致
     *
     * @param src       源数据
     * @param sm3HexStr 16进制的加密数据
     */
    public static boolean verify(String src, String sm3HexStr) throws Exception {
        byte[] sm3HashCode = ByteUtils.fromHexString(sm3HexStr);
        byte[] newHashCode = getEncryptBySrcByte(src.getBytes(ENCODING));
        return Arrays.equals(newHashCode, sm3HashCode);
    }

    /**
     * 利用源数据+密钥校验与密文是否一致
     *
     * @param src       源数据
     * @param key       密钥
     * @param sm3HexStr 密文
     */
    public static boolean verify(String src, String key, String sm3HexStr) {
        byte[] sm3HashCode = ByteUtils.fromHexString(sm3HexStr);
        byte[] newHashCode = getEncryptByKey(src, key);
        return Arrays.equals(newHashCode, sm3HashCode);
    }

    /**
     * 测试
     */
    public static void main(String[] args) throws Exception {
        String srcStr = "春宵一刻值千金，花有清香月有阴；歌管楼台声细细，秋千院落夜沉沉！";
        String key = "春宵";
        // ******************************自定义密钥加密及校验*****************************************
        String hexStrByKey = SM3Utils.encrypt(srcStr, key);
        System.out.println("        带密钥加密后的密文：" + hexStrByKey);

        System.out.println("明文(带密钥)与密文校验结果：" + SM3Utils.verify(srcStr, key, hexStrByKey));
        System.out.println(
                "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        // ******************************无密钥的加密及校验******************************************
        String hexStrNoKey = SM3Utils.encrypt(srcStr);
        System.out.println("        不带密钥加密后的密文：" + hexStrNoKey);

        System.out.println("明文(不带密钥)与密文校验结果：" + SM3Utils.verify(srcStr, hexStrNoKey));

    }

}
