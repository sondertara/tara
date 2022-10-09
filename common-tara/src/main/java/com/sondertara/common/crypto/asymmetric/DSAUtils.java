package com.sondertara.common.crypto.asymmetric;

import com.sondertara.common.exception.TaraException;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * DSA算法 DSA（Digital Signature
 * Algorithm）是Schnorr和ElGamal签名算法的变种，被美国NIST作为DSS(DigitalSignature Standard)。
 * DSA加密算法主要依赖于整数有限域离散对数难题，素数P必须足够大，且p-1至少包含一个大素数因子以抵抗Pohlig &Hellman算法的攻击。
 * M一般都应采用信息的HASH值。DSA加密算法的安全性主要依赖于p和g，若选取不当则签名容易伪造，应保证g对于p-1的大素数因子不可约。
 * 其安全性与RSA相比差不多。
 * DSA一般用于数字签名和认证。
 * 在DSA数字签名和认证中，发送者使用自己的私钥对文件或消息进行签名，接受者收到消息后使用发送者的
 * 公钥来验证签名的真实性。DSA只是一种算法，和RSA不同之处在于它不能用作加密和解密，也不能进行密钥交换，只用于签名,它比RSA要快很多.
 *
 * @author huangxiaohu
 */
public class DSAUtils {
    public static final String ALGORITHM = "DSA";

    /**
     * SHA384withDSA, SHA512withDSA,SHA384withECDSA, SHA512withECDSA;
     */

    public enum DsaSignatureAlgorithm {
        /**
         *
         */
        SHA1withDSA, SHA224withDSA, SHA256withDSA;

        public String getName() {
            return this.name();
        }
    }

    /**
     * 初始化密钥
     *
     * @return key pair
     */
    public static KeyPair initKey() {
        // 密钥长度范围：512-65536（64的整数倍）
        return initKey(1024);
    }

    /**
     * 初始化密钥
     *
     * @param keySize 长度范围：512-65536（64的整数倍）
     * @return key pair
     */
    public static KeyPair initKey(int keySize) {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGen.initialize(keySize);
            return keyPairGen.generateKeyPair();
        } catch (Exception e) {
            throw new TaraException(e);
        }
    }

    /**
     * 签名
     */
    public static byte[] sign(byte[] data, byte[] privateKey, String signatureAlgorithm) throws Exception {
        try {
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);

            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

            PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

            Signature signature = Signature.getInstance(signatureAlgorithm);
            signature.initSign(priKey);
            signature.update(data);

            return signature.sign();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * 验签
     */
    public static boolean verify(byte[] data, byte[] publicKey, byte[] sign, String signatureAlgorithm)
            throws Exception {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);

            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

            PublicKey pubKey = keyFactory.generatePublic(keySpec);

            Signature signature = Signature.getInstance(signatureAlgorithm);
            signature.initVerify(pubKey);
            signature.update(data);

            return signature.verify(sign);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public static void main(String[] args) throws Exception {
        String str = "春宵一刻值千金，花有清香月有阴；歌管楼台声细细，秋千院落夜沉沉！";
        System.out.println("明文：" + str);
        KeyPair keyPair = DSAUtils.initKey();

        byte[] keyPairPrivate = keyPair.getPrivate().getEncoded();
        byte[] keyPairPublic = keyPair.getPublic().getEncoded();
        System.out.println("私钥：" + Base64.getEncoder().encodeToString(keyPairPrivate));
        System.out.println("公钥：" + Base64.getEncoder().encodeToString(keyPairPublic));
        for (DsaSignatureAlgorithm algorithm : DsaSignatureAlgorithm.values()) {
            System.out.println("-----------------------------------------");
            System.out.println("签名算法：" + algorithm.getName());
            byte[] signed = DSAUtils.sign(str.getBytes(), keyPairPrivate, algorithm.getName());
            System.out.println("签名：" + Base64.getEncoder().encodeToString(signed));

            boolean verify = DSAUtils.verify(str.getBytes(), keyPairPublic, signed, algorithm.getName());
            System.out.println("验签：" + verify);
        }

    }
}
