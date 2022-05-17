package com.sondertara.common.crypto;

import com.sondertara.common.exception.TaraException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author huangxiaohu
 */
public class MD5Utils {
    static final String MD5 = "MD5";

    /**
     * 字符串加密
     *
     * @param data 原字符串
     * @return 加密后新字符串
     */
    public static String encrypt(String data) {

        byte[] dataBytes = data.getBytes();
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance(MD5);
            md5.update(dataBytes);
            byte[] resultBytes = md5.digest();

            StringBuilder sb = new StringBuilder();
            for (byte b : resultBytes) {
                if (Integer.toHexString(0xFF & b).length() == 1) {
                    sb.append("0").append(Integer.toHexString(0xFF & b));
                } else {
                    sb.append(Integer.toHexString(0xFF & b));
                }
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new TaraException("MD5 encrypt error", e);
        }

    }

    /**
     * 文件加密
     *
     * @param filePath 文件路径
     * @return 加密后的字符串
     */
    public static String encryptFile(String filePath) {

        File file = new File(filePath);
        try (FileInputStream fis = new FileInputStream(file)) {
            StringBuilder sb = new StringBuilder();
            MappedByteBuffer byteBuffer = fis.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());

            MessageDigest md5 = MessageDigest.getInstance(MD5);
            md5.update(byteBuffer);
            byte[] resultBytes = md5.digest();
            for (byte b : resultBytes) {
                if (Integer.toHexString(0xFF & b).length() == 1) {
                    sb.append("0").append(Integer.toHexString(0xFF & b));
                } else {
                    sb.append(Integer.toHexString(0xFF & b));
                }
            }
            return sb.toString();
        } catch (Exception e) {
            throw new TaraException("MD5 encryptFile error", e);
        }
    }


    /**
     * 对字符串 MD5 加盐值加密
     *
     * @param value     传入要加密的字符串
     * @param saltValue 传入要加的盐值
     * @return MD5加密后生成32位(小写字母 + 数字)字符串
     */
    public static String encrypt(String value, String saltValue) {
        try {
            // 1、获得MD5摘要算法的 MessageDigest 对象
            MessageDigest md = MessageDigest.getInstance(MD5);

            // 2、使用指定的字节更新摘要
            md.update(value.getBytes());
            md.update(saltValue.getBytes());

            // digest()最后确定返回md5 hash值，返回值为8位字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值。1 固定值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("md5 " + value + "加密失败！");
            return null;
        }
    }

    /**
     * 多次MD5加密
     *
     * @param data text
     * @param time 重复加密次数
     * @return encrypt
     */
    public static String repeatEncrypt(String data, int time) {

        if (StringUtils.isEmpty(data)) {
            return "";
        }

        String result = encrypt(data);
        for (int i = 0; i < time - 1; i++) {
            result = encrypt(result);
        }
        return encrypt(result);
    }


    public static void main(String[] args) {
        String srcStr = "春宵一刻值千金，花有清香月有阴；歌管楼台声细细，秋千院落夜沉沉！";
        System.out.println("原始信息：" + srcStr);
        System.out.println("MD5加密后(密文长度32位)：" + encrypt(srcStr));
    }
}
