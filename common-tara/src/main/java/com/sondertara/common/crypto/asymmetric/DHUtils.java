package com.sondertara.common.crypto.asymmetric;

import com.sondertara.common.exception.TaraException;
import com.sondertara.common.util.HexUtils;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

/**
 * DH，全称为“Diffie-Hellman”，是一种确保共享KEY安全穿越不安全网络的方法，即常说的密钥一致协议。
 * 由公开密钥密码体制的奠基人Diffie和Hellman所提出。
 * 原理是由甲方产出一对密钥（公钥、私钥），乙方依照甲方公钥产生乙方密钥对（公钥、私钥）。
 * 以此为基线，作为数据传输保密基础，同时双方使用同一种对称加密算法构建本地密钥（SecretKey）对数据加密。
 * 在互通了本地密钥（SecretKey）算法后，甲乙双方公开自己的公钥，使用对方的公钥和刚才产生的私钥加密数据，
 * 同时可以使用对方的公钥和自己的私钥对数据解密。 可以扩展为多方共享数据通讯，从而实现网络交互数据的安全通讯！
 * <p>
 * 密钥长度：512-1024，必须是64的整数倍，默认密钥长度为1024
 *
 * @author huangxiaohu
 */
public class DHUtils {
    private static final String KEY_DH = "DH";
    public static final String DH_PUBLIC_KEY = "DHPublicKey";
    public static final String DH_PRIVATE_KEY = "DHPrivateKey";
    /**
     * 利用 DES或AES对称密码算法生成本地密钥SecretKey
     * public static final String KEY_DH_DES = "DES";
     */
    public static final String KEY_DH_AES = "AES";

    /**
     * 初始化并返回甲方密钥对
     * <p>
     * keySize 512--1024之间，且为64的倍数,当keySize小于512时，默认1024
     */
    public static Map<String, Key> initKey(int keySize) {
        try {
            // 实例化密钥对生成器
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_DH);
            // 初始化密钥对生成器 默认是1024 512-1024且为64的倍数
            keyPairGenerator.initialize(keySize < 512 ? 1024 : keySize);
            // 生成密钥对
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            // 将公钥和私钥封装在Map中
            Map<String, Key> keyMap = new HashMap<>(2);
            keyMap.put(DH_PUBLIC_KEY, keyPair.getPublic());
            keyMap.put(DH_PRIVATE_KEY, keyPair.getPrivate());
            return keyMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 乙方根据甲方公钥初始化并返回密钥对
     *
     * @param publicKey 甲方的公钥
     */
    public static Map<String, Key> initKey(byte[] publicKey) {
        try {
            // 将甲方公钥从字节数组转换为PublicKey
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
            // 实例化密钥工厂
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_DH);
            // 产生甲方公钥pubKey
            DHPublicKey dhPublicKey = (DHPublicKey) keyFactory.generatePublic(x509KeySpec);
            // 获取甲方公钥中的参数信息
            DHParameterSpec dhParameterSpec = dhPublicKey.getParams();
            // 实例化密钥对生成器
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_DH);
            // 用甲方公钥初始化密钥对生成器
            keyPairGenerator.initialize(dhParameterSpec);

            // 生成乙方密钥对
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            // 将公钥和私钥封装在Map中， 方便之后使用
            Map<String, Key> keyMap = new HashMap<>(2);
            keyMap.put(DH_PUBLIC_KEY, keyPair.getPublic());
            keyMap.put(DH_PRIVATE_KEY, keyPair.getPrivate());
            return keyMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 根据对方的公钥和自己的私钥生成 本地密钥,返回SecretKey对象的字节数组
     *
     * @param publicKey  公钥
     * @param privateKey 私钥
     */
    public static byte[] getSecretKeyBytes(byte[] publicKey, byte[] privateKey) {
        try {
            // 实例化密钥工厂
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_DH);
            // 将公钥从字节数组转换为PublicKey
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
            // 将私钥从字节数组转换为PrivateKey
            PKCS8EncodedKeySpec priKeySpec = new PKCS8EncodedKeySpec(privateKey);
            PrivateKey priKey = keyFactory.generatePrivate(priKeySpec);

            // 利用公钥和私钥生成本地密钥SecretKey
            KeyAgreement keyAgreement = KeyAgreement.getInstance(KEY_DH);
            // 用自己的私钥初始化keyAgreement
            keyAgreement.init(priKey);
            // 结合对方的公钥进行运算
            keyAgreement.doPhase(pubKey, true);
            // 返回本地密钥SecretKey 密钥算法为对称密码算法
            return keyAgreement.generateSecret(KEY_DH_AES).getEncoded();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据对方的公钥和自己的私钥生成 本地密钥,返回的是SecretKey对象
     *
     * @param publicKey  公钥
     * @param privateKey 私钥
     */
    public static SecretKey getSecretKey(byte[] publicKey, byte[] privateKey) {
        try {
            // 实例化密钥工厂
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_DH);
            // 将公钥从字节数组转换为PublicKey
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
            // 将私钥从字节数组转换为PrivateKey
            PKCS8EncodedKeySpec priKeySpec = new PKCS8EncodedKeySpec(privateKey);
            PrivateKey priKey = keyFactory.generatePrivate(priKeySpec);

            // 根据以上公钥和私钥生成本地密钥SecretKey
            KeyAgreement keyAgreement = KeyAgreement.getInstance(KEY_DH);
            // 用自己的私钥初始化keyAgreement
            keyAgreement.init(priKey);
            // 结合对方的公钥进行运算
            keyAgreement.doPhase(pubKey, true);
            // 返回本地密钥SecretKey 密钥算法为对称密码算法
            return keyAgreement.generateSecret(KEY_DH_AES);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 从 Map 中取得公钥
     */
    public static byte[] getPublicKey(Map<String, Key> keyMap) {
        Key key = keyMap.get(DH_PUBLIC_KEY);
        return key.getEncoded();
    }

    /**
     * 从 Map 中取得私钥
     */
    public static byte[] getPrivateKey(Map<String, Key> keyMap) {
        Key key = keyMap.get(DH_PRIVATE_KEY);
        return key.getEncoded();
    }

    /**
     * DH 加密
     *
     * @param data       待加密数据
     * @param publicKey  甲方公钥
     * @param privateKey 乙方私钥
     */
    public static byte[] encrypt(byte[] data, byte[] publicKey, byte[] privateKey) {
        try {
            //
            SecretKey secretKey = getSecretKey(publicKey, privateKey);
            // 数据加密
            Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new TaraException("DH encrypt error", e);
        }
    }

    /**
     * DH 解密
     *
     * @param data       待解密数据
     * @param publicKey  乙方公钥
     * @param privateKey 甲方私钥
     */
    public static byte[] decrypt(byte[] data, byte[] publicKey, byte[] privateKey) {
        try {
            SecretKey secretKey = getSecretKey(publicKey, privateKey);
            Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new TaraException("DH encrypt error", e);
        }
    }

    public static void main(String[] args) throws Exception {

        System.setProperty("jdk.crypto.KeyAgreement.legacyKDF", "true");
        // 初始化长度1024的密钥， 并生成甲方密钥对
        Map<String, Key> keyMap1 = DHUtils.initKey(1024);
        // 甲方公钥
        byte[] publicKey1 = DHUtils.getPublicKey(keyMap1);
        // 甲方私钥
        byte[] privateKey1 = DHUtils.getPrivateKey(keyMap1);
        System.out.println("甲方公钥 : " + HexUtils.encodeHexStr(publicKey1));
        System.out.println("甲方私钥 : " + HexUtils.encodeHexStr(privateKey1));

        // 乙方根据甲方公钥产生乙方密钥对
        Map<String, Key> keyMap2 = DHUtils.initKey(publicKey1);
        // 乙方公钥
        byte[] publicKey2 = DHUtils.getPublicKey(keyMap2);
        // 乙方私钥
        byte[] privateKey2 = DHUtils.getPrivateKey(keyMap2);
        System.out.println("乙方公钥 : " + HexUtils.encodeHexStr(publicKey2));
        System.out.println("乙方私钥 : " + HexUtils.encodeHexStr(privateKey2));

        // 根据甲方私钥和乙方的公钥， 生成甲方本地密钥secretKey1
        byte[] secretKey1 = DHUtils.getSecretKeyBytes(publicKey2, privateKey1);
        System.out.println("甲方本地密钥 : " + HexUtils.encodeHexStr(secretKey1));

        // 乙方根据其私钥和甲方公钥， 生成乙方本地密钥secretKey2
        byte[] secretKey2 = DHUtils.getSecretKeyBytes(publicKey1, privateKey2);
        System.out.println("乙方本地密钥 : " + HexUtils.encodeHexStr(secretKey2));

        // 原始信息
        String srcStr = "春宵一刻值千金，花有清香月有阴；歌管楼台声细细，秋千院落夜沉沉！";
        // 测试数据加密和解密
        System.out.println("加密前的数据：" + srcStr);
        // 甲方进行数据的加密
        // 用的是甲方的私钥和乙方的公钥
        byte[] encrypt = DHUtils.encrypt(srcStr.getBytes(), publicKey2, privateKey1);
        System.out.println("加密后的数据 字节数组转16进制：" + HexUtils.encodeHexStr(encrypt));
        // 乙方进行数据的解密
        // 用的是乙方的私钥和甲方的公钥
        byte[] decrypt = DHUtils.decrypt(encrypt, publicKey1, privateKey2);

        System.out.println("解密后的数据：" + new String(decrypt));
    }

}
