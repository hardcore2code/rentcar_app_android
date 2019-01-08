package com.static4u.netcar.utils;

import com.static4u.netcar.constant.URLConstant;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * DES加密
 *
 * @author LWang 2016.01.05
 */
public class DESUtils {
    private static final String KEY_ALGORITHM = "DES";
    private static final String CIPHER_ALGORITHM = "DES/ECB/PKCS5Padding";

    private static SecretKey keyGenerator(String key) throws Exception {
        DESKeySpec desKey = new DESKeySpec(key.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        SecretKey securekey = keyFactory.generateSecret(desKey);
        return securekey;
    }

    public static String encrypt(byte[] data, String key) {
        try {
            if (data == null || data.length <= 0) {//返回的数据为空或者null返回""
                return "";
            }
            Key deskey = keyGenerator(key);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            SecureRandom random = new SecureRandom();
            cipher.init(Cipher.ENCRYPT_MODE, deskey, random);
            byte[] results = cipher.doFinal(data);
            return Base64Utils.encode(results);
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
            return "";
        }
    }

    public static byte[] decrypt(String data, String key) {
        try {
            if (data == null || data.length() <= 0) {//返回的数据为空或者null返回null
                return null;
            }
            Key deskey = keyGenerator(key);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, deskey);
            return cipher.doFinal(Base64Utils.decode(data));
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
            return "".getBytes();
        }
    }

    public static String encrypt(String data) {
        if (CommonUtil.isEmptyOrNull(data)) {
            return "";
        }
        return encrypt(data.getBytes(), "tax_code");
    }

    public static String decrypt(String data) {
        if (CommonUtil.isEmptyOrNull(data)) {
            return "";
        }
        return new String(decrypt(data, "tax_code"));
    }
}
