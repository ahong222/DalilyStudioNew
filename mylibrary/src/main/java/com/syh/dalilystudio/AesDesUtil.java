package com.syh.dalilystudio;

import java.security.MessageDigest;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密解密工具类
 * 
 * @author syh
 * 
 */
public class AesDesUtil {

    public final static String MD5(String s) {
//        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
//                'A', 'B', 'C', 'D', 'E', 'F' };
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            StringBuilder sBuilder = bytesToHexString(md);
            return sBuilder.toString();
//            int j = md.length;
//            char str[] = new char[j * 2];
//            int k = 0;
//            for (int i = 0; i < j; i++) {
//                byte byte0 = md[i];
//                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
//                str[k++] = hexDigits[byte0 & 0xf];
//            }
//            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 转换成Hex
     * 
     * @param bytesArray
     */
    public static StringBuilder bytesToHexString(byte[] bytesArray) {
        if (bytesArray == null) {
            return null;
        }
        StringBuilder sBuilder = new StringBuilder();
        for (byte b : bytesArray) {
            String hv = String.format("%02x", b);
            sBuilder.append(hv);
        }
        return sBuilder;
    }
    

    public static class HmacSHA1Encryption {
        private static final String MAC_NAME = "HmacSHA1";
        private static final String ENCODING = "UTF-8";

        /**
         * 使用 HMAC-SHA1 签名方法对对 encryptText 进行签名
         * 
         * @param encryptText
         *            被签名的字符串
         * @param encryptKey
         *            密钥
         * @return 返回被加密后的字符串
         * @throws Exception
         */
        public static String HmacSHA1Encrypt(String encryptText,
                String encryptKey) throws Exception {
            byte[] data = encryptKey.getBytes(ENCODING);
            SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
            Mac mac = Mac.getInstance(MAC_NAME);
            mac.init(secretKey);
            byte[] text = encryptText.getBytes(ENCODING);
            byte[] digest = mac.doFinal(text);
            StringBuilder sBuilder = bytesToHexString(digest);
            return sBuilder.toString();
        }

        

        /**
         * 使用 HMAC-SHA1 签名方法对对 encryptText 进行签名
         * 
         * @param encryptData
         *            被签名的字符串
         * @param encryptKey
         *            密钥
         * @return 返回被加密后的字符串
         * @throws Exception
         */
        public static String HmacSHA1Encrypt(byte[] encryptData,
                String encryptKey) throws Exception {
            byte[] data = encryptKey.getBytes(ENCODING);
            SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
            Mac mac = Mac.getInstance(MAC_NAME);
            mac.init(secretKey);
            byte[] digest = mac.doFinal(encryptData);
            StringBuilder sBuilder = bytesToHexString(digest);
            return sBuilder.toString();
        }
    }
}
