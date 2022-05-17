package com.sondertara.common.crypto;

import com.sondertara.common.exception.TaraException;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

/**
 * RSA: 既能用于数据加密也能用于数字签名的算法
 * RSA算法原理如下：
 * <p>
 * 1.随机选择两个大质数p和q，p不等于q，计算N=pq
 * 2.选择一个大于1小于N的自然数e，e必须与(p-1)(q-1)互素
 * 3.用公式计算出d：d×e = 1 (mod (p-1)(q-1))
 * 4.销毁p和q
 * 5.最终得到的N和e就是“公钥”，d就是“私钥”，发送方使用N去加密数据，接收方只有使用d才能解开数据内容
 * <p>
 * 基于大数计算，比DES要慢上几倍，通常只能用于加密少量数据或者加密密钥
 * 私钥加解密都很耗时，服务器要求解密效率高，客户端私钥加密，服务器公钥解密比较好一点
 *
 * @author Song
 * @date 2017/2/22
 */

public class RSAUtils {
    /**
     * 非对称加密密钥算法
     */

    public static final String RSA = "RSA";

    /**
     * 加密填充方式
     */
    public static final String ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";
    /**
     * 秘钥默认长度
     */
    public static final int DEFAULT_KEY_SIZE = 2048;
    /**
     * 当要加密的内容超过bufferSize，则采用partSplit进行分块加密
     */
    public static final byte[] DEFAULT_SPLIT = "#PART#".getBytes();
    /**
     * 当前秘钥支持加密的最大字节数
     */
    public static final int DEFAULT_BUFFER_SIZE = (DEFAULT_KEY_SIZE / 8) - 11;


    /**
     * 随机生成RSA密钥对
     *
     * @param keyLength 密钥长度，范围：512～2048
     *                  一般1024
     *                  <p>
     *                  使用：
     *                  KeyPair keyPair=RSAUtils.initKey(RSAUtils.DEFAULT_KEY_SIZE);
     *                  公钥
     *                  RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
     *                  私钥
     *                  RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
     * @return KeyPair
     */
    public static KeyPair initKey(int keyLength) {

        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
            SecureRandom secureRandom = new SecureRandom();
            kpg.initialize(keyLength, secureRandom);
            return kpg.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new TaraException("RSA init key pair error", e);
        }
    }


    /**
     * 公钥对字符串进行加密
     *
     * @param data 原文
     */
    public static byte[] encryptByPublicKey(byte[] data, byte[] publicKey) {

        try {
            // 得到公钥
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
            KeyFactory kf = KeyFactory.getInstance(RSA);
            PublicKey keyPublic = kf.generatePublic(keySpec);
            // 加密数据
            Cipher cp = Cipher.getInstance(ECB_PKCS1_PADDING);
            cp.init(Cipher.ENCRYPT_MODE, keyPublic);
            return cp.doFinal(data);
        } catch (Exception e) {

            throw new TaraException("RSA encryptByPublicKey error", e);
        }
    }

    public static String encryptByPublicKey(String plainText, String publicKeyBase64) {

        byte[] bytes = encryptByPublicKey(plainText.getBytes(StandardCharsets.UTF_8), Base64.decodeBase64(publicKeyBase64));

        return Base64.encodeBase64String(bytes);
    }


    /**
     * 私钥加密
     *
     * @param data       待加密数据
     * @param privateKey 密钥
     * @return byte[] 加密数据
     */
    public static byte[] encryptByPrivateKey(byte[] data, byte[] privateKey) {

        try {
            // 得到私钥
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
            KeyFactory kf = KeyFactory.getInstance(RSA);
            PrivateKey keyPrivate = kf.generatePrivate(keySpec);
            // 数据加密
            Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, keyPrivate);
            return cipher.doFinal(data);
        } catch (Exception e) {

            throw new TaraException("RSA encryptByPublicKey error", e);
        }
    }

    public static String encryptByPrivateKey(String plainText, String privateKeyBase64) {

        byte[] bytes = encryptByPrivateKey(plainText.getBytes(StandardCharsets.UTF_8), Base64.decodeBase64(privateKeyBase64));

        return Base64.encodeBase64String(bytes);
    }

    /**
     * 公钥解密
     *
     * @param data      待解密数据
     * @param publicKey 密钥
     * @return byte[] 解密数据
     */
    public static byte[] decryptByPublicKey(byte[] data, byte[] publicKey) {

        try {
            // 得到公钥
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
            KeyFactory kf = KeyFactory.getInstance(RSA);
            PublicKey keyPublic = kf.generatePublic(keySpec);
            // 数据解密
            Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, keyPublic);
            return cipher.doFinal(data);
        } catch (Exception e) {

            throw new TaraException("RSA encryptByPublicKey error", e);
        }
    }

    public static String decryptByPublicKey(String cipherBase64, String publicKeyBase64) {

        byte[] bytes = decryptByPublicKey(Base64.decodeBase64(cipherBase64), Base64.decodeBase64(publicKeyBase64));

        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 使用私钥进行解密
     */
    public static byte[] decryptByPrivateKey(byte[] encrypted, byte[] privateKey) {

        try {
            // 得到私钥
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
            KeyFactory kf = KeyFactory.getInstance(RSA);
            PrivateKey keyPrivate = kf.generatePrivate(keySpec);
            // 解密数据
            Cipher cp = Cipher.getInstance(ECB_PKCS1_PADDING);
            cp.init(Cipher.DECRYPT_MODE, keyPrivate);
            return cp.doFinal(encrypted);
        } catch (Exception e) {

            throw new TaraException("RSA encryptByPublicKey error", e);
        }
    }

    public static String decryptByPrivateKey(String cipherBase64, String privateKeyBase64) {

        byte[] bytes = decryptByPrivateKey(Base64.decodeBase64(cipherBase64), Base64.decodeBase64(privateKeyBase64));

        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 实现分段加密：
     * RSA非对称加密内容长度有限制，1024位key的最多只能加密127位数据，
     * 否则就会报错(javax.crypto.IllegalBlockSizeException: Data must not be longer than 117 bytes)
     * 最近使用时却出现了“不正确的长度”的异常，研究发现是由于待加密的数据超长所致。
     * RSA 算法规定：
     * 待加密的字节数不能超过密钥的长度值除以 8 再减去 11（即：KeySize / 8 - 11），
     * 而加密后得到密文的字节数，正好是密钥的长度值除以 8（即：KeySize / 8）
     * <p>
     * 用公钥对字符串进行分段加密
     */
    public static byte[] encryptByPublicKeyForSpilt(byte[] data, byte[] publicKey) {

        int dataLen = data.length;
        if (dataLen <= DEFAULT_BUFFER_SIZE) {
            return encryptByPublicKey(data, publicKey);
        }
        List<Byte> allBytes = new ArrayList<>(2048);
        int bufIndex = 0;
        int subDataLoop = 0;
        byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
        for (int i = 0; i < dataLen; i++) {
            assert buf != null;
            buf[bufIndex] = data[i];
            if (++bufIndex == DEFAULT_BUFFER_SIZE || i == dataLen - 1) {
                subDataLoop++;
                if (subDataLoop != 1) {
                    for (byte b : DEFAULT_SPLIT) {
                        allBytes.add(b);
                    }
                }
                byte[] encryptBytes = encryptByPublicKey(buf, publicKey);
                for (byte b : encryptBytes) {
                    allBytes.add(b);
                }
                bufIndex = 0;
                if (i == dataLen - 1) {
                    buf = null;
                } else {
                    buf = new byte[Math.min(DEFAULT_BUFFER_SIZE, dataLen - i - 1)];
                }
            }
        }
        byte[] bytes = new byte[allBytes.size()];
        {
            int i = 0;
            for (Byte b : allBytes) {
                bytes[i++] = b;
            }
        }
        return bytes;
    }

    /**
     * 私钥分段加密
     *
     * @param data       要加密的原始数据
     * @param privateKey 秘钥
     */
    public static byte[] encryptByPrivateKeyForSpilt(byte[] data, byte[] privateKey) {
        int dataLen = data.length;
        if (dataLen <= DEFAULT_BUFFER_SIZE) {
            return encryptByPrivateKey(data, privateKey);
        }
        List<Byte> allBytes = new ArrayList<>(2048);
        int bufIndex = 0;
        int subDataLoop = 0;
        byte[] buf = new byte[DEFAULT_BUFFER_SIZE];

        for (int i = 0; i < dataLen; i++) {
            assert buf != null;
            buf[bufIndex] = data[i];
            if (++bufIndex == DEFAULT_BUFFER_SIZE || i == dataLen - 1) {
                subDataLoop++;
                if (subDataLoop != 1) {
                    for (byte b : DEFAULT_SPLIT) {
                        allBytes.add(b);
                    }
                }
                byte[] encryptBytes = encryptByPrivateKey(buf, privateKey);
                for (byte b : encryptBytes) {
                    allBytes.add(b);
                }
                bufIndex = 0;
                if (i == dataLen - 1) {
                    buf = null;
                } else {
                    buf = new byte[Math.min(DEFAULT_BUFFER_SIZE, dataLen - i - 1)];
                }
            }
        }
        byte[] bytes = new byte[allBytes.size()];
        {
            int i = 0;
            for (Byte b : allBytes) {
                bytes[i++] = b;
            }
        }
        return bytes;
    }

    /**
     * 公钥分段解密
     *
     * @param encrypted 待解密数据
     * @param publicKey 密钥
     */
    public static byte[] decryptByPublicKeyForSpilt(byte[] encrypted, byte[] publicKey) throws Exception {

        int splitLen = DEFAULT_SPLIT.length;
        if (splitLen <= 0) {
            return decryptByPublicKey(encrypted, publicKey);
        }
        int dataLen = encrypted.length;
        List<Byte> allBytes = new ArrayList<>(1024);
        int latestStartIndex = 0;
        for (int i = 0; i < dataLen; i++) {
            byte bt = encrypted[i];
            boolean isMatchSplit = false;
            if (i == dataLen - 1) {
                // 到data的最后了
                byte[] part = new byte[dataLen - latestStartIndex];
                System.arraycopy(encrypted, latestStartIndex, part, 0, part.length);
                byte[] decryptPart = decryptByPublicKey(part, publicKey);
                for (byte b : decryptPart) {
                    allBytes.add(b);
                }
                latestStartIndex = i + splitLen;
                i = latestStartIndex - 1;
            } else if (bt == DEFAULT_SPLIT[0]) {
                // 这个是以split[0]开头
                if (splitLen > 1) {
                    if (i + splitLen < dataLen) {
                        // 没有超出data的范围
                        for (int j = 1; j < splitLen; j++) {
                            if (DEFAULT_SPLIT[j] != encrypted[i + j]) {
                                break;
                            }
                            if (j == splitLen - 1) {
                                // 验证到split的最后一位，都没有break，则表明已经确认是split段
                                isMatchSplit = true;
                                break;
                            }
                        }
                    }
                } else {
                    // split只有一位，则已经匹配了
                    isMatchSplit = true;
                }
            }
            if (isMatchSplit) {
                byte[] part = new byte[i - latestStartIndex];
                System.arraycopy(encrypted, latestStartIndex, part, 0, part.length);
                byte[] decryptPart = decryptByPublicKey(part, publicKey);
                for (byte b : decryptPart) {
                    allBytes.add(b);
                }
                latestStartIndex = i + splitLen;
                i = latestStartIndex - 1;
            }
        }
        byte[] bytes = new byte[allBytes.size()];
        {
            int i = 0;
            for (Byte b : allBytes) {
                bytes[i++] = b;
            }
        }
        return bytes;
    }

    /**
     * 私钥分段解密
     */
    public static byte[] decryptByPrivateKeyForSpilt(byte[] encrypted, byte[] privateKey) {

        int splitLen = DEFAULT_SPLIT.length;
        if (splitLen <= 0) {
            return decryptByPrivateKey(encrypted, privateKey);
        }
        int dataLen = encrypted.length;
        List<Byte> allBytes = new ArrayList<>(1024);
        int latestStartIndex = 0;
        for (int i = 0; i < dataLen; i++) {
            byte bt = encrypted[i];
            boolean isMatchSplit = false;
            if (i == dataLen - 1) {
                // 到data的最后了
                byte[] part = new byte[dataLen - latestStartIndex];
                System.arraycopy(encrypted, latestStartIndex, part, 0, part.length);
                byte[] decryptPart = decryptByPrivateKey(part, privateKey);
                for (byte b : decryptPart) {
                    allBytes.add(b);
                }
                latestStartIndex = i + splitLen;
                i = latestStartIndex - 1;
            } else if (bt == DEFAULT_SPLIT[0]) {
                // 这个是以split[0]开头
                if (splitLen > 1) {
                    if (i + splitLen < dataLen) {
                        // 没有超出data的范围
                        for (int j = 1; j < splitLen; j++) {
                            if (DEFAULT_SPLIT[j] != encrypted[i + j]) {
                                break;
                            }
                            if (j == splitLen - 1) {
                                // 验证到split的最后一位，都没有break，则表明已经确认是split段
                                isMatchSplit = true;
                                break;
                            }
                        }
                    }
                } else {
                    // split只有一位，则已经匹配了
                    isMatchSplit = true;
                }
            }
            if (isMatchSplit) {
                byte[] part = new byte[i - latestStartIndex];
                System.arraycopy(encrypted, latestStartIndex, part, 0, part.length);
                byte[] decryptPart = decryptByPrivateKey(part, privateKey);
                for (byte b : decryptPart) {
                    allBytes.add(b);
                }
                latestStartIndex = i + splitLen;
                i = latestStartIndex - 1;
            }
        }
        byte[] bytes = new byte[allBytes.size()];
        {
            int i = 0;
            for (Byte b : allBytes) {
                bytes[i++] = b;
            }
        }
        return bytes;
    }

    /**
     * 将密钥采用Base64加密并返回加密后的密文
     *
     * @param key 密钥
     * @return base64
     */
    public static String keyEncrypt(Key key) {
        return new String(Base64.encodeBase64(key.getEncoded()));
    }

    public static void main(String[] args) throws Exception {

        String srcStr = "春宵一刻值千金，花有清香月有阴；歌管楼台声细细，秋千院落夜沉沉！";
        // 获取非对称密钥
        KeyPair keyPair = RSAUtils.initKey(4196);

        System.out.println("公钥：" + keyEncrypt(keyPair.getPublic()));
        System.out.println("私钥：" + keyEncrypt(keyPair.getPrivate()));

        byte[] bytes = RSAUtils.encryptByPublicKey(srcStr.getBytes(StandardCharsets.UTF_8), keyPair.getPublic().getEncoded());
        System.out.println("原始信息：" + srcStr + " 加密后的密文为：");
        System.out.println(Base64.encodeBase64String(bytes));
        byte[] bytes1 = RSAUtils.decryptByPrivateKey(bytes, keyPair.getPrivate().getEncoded());
        System.out.println("密文解密后为：" + new String(bytes1));
        String s = encryptByPublicKey(srcStr, keyEncrypt(keyPair.getPublic()));
        String s1 = decryptByPrivateKey(s, keyEncrypt(keyPair.getPrivate()));
        System.out.println(s1);

    }
}