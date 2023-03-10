package com.kk.onlineencryptor.tools;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class RSAUtils {

    // RSA最大加密明文大小
    private static final int MAX_ENCRYPT_BLOCK = 117;

    // RSA最大解密密文大小
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 获取公钥和私钥对，key为公钥，value为私钥
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static Map<String, String> genKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1024, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKeyString = null;
        String privateKeyString = null;

        try {
            publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()), "UTF-8");
            privateKeyString = new String(Base64.encodeBase64(privateKey.getEncoded()), "UTF-8");
        } catch (UnsupportedEncodingException var7) {
            var7.printStackTrace();
        }

        Map<String, String> keyPairMap = new HashMap<>();
        keyPairMap.put("publicKey", publicKeyString);
        keyPairMap.put("privateKey", privateKeyString);
        return keyPairMap;
    }

    public static String encrypt(String str, String publicKey) throws Exception {
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decoded));
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(1, pubKey);
        // 分段加密
        // URLEncoder编码解决中文乱码问题
        byte[] data = URLEncoder.encode(str, "UTF-8").getBytes("UTF-8");
        // 加密时超过117字节就报错。为此采用分段加密的办法来加密
        byte[] enBytes = null;
        for (int i = 0; i < data.length; i += MAX_ENCRYPT_BLOCK) {
            // 注意要使用2的倍数，否则会出现加密后的内容再解密时为乱码
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + MAX_ENCRYPT_BLOCK));
            enBytes = ArrayUtils.addAll(enBytes, doFinal);
        }
        String outStr = Base64.encodeBase64String(enBytes);
        return outStr;
    }

    /**
     * 公钥分段解密
     *
     * @param str
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String decrypt(String str, String privateKey) throws Exception {
        // 获取公钥
        byte[] decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(2, priKey);
        byte[] data = Base64.decodeBase64(str.getBytes("UTF-8"));

        // 返回UTF-8编码的解密信息
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * 128;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return URLDecoder.decode(new String(decryptedData, "UTF-8"), "UTF-8");
    }

}
