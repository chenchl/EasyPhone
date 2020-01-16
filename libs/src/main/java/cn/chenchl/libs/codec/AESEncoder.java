package cn.chenchl.libs.codec;

import android.text.TextUtils;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * created by ccl on 2019/2/28
 **/
public class AESEncoder {
    /*
     * 加密
     */
    public static String AESEncode(String encodeRules, String content) {
        if (TextUtils.isEmpty(encodeRules) || encodeRules.length() != 16) {//秘钥判断
            return null;
        }
        try {
            //5.根据字节数组生成AES密钥
            SecretKeySpec key = new SecretKeySpec(encodeRules.getBytes(), "AES");
            IvParameterSpec iv = new IvParameterSpec(encodeRules.getBytes("UTF-8"));
            //6.根据指定算法AES自成密码器
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            //8.获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
            byte[] byte_encode = content.getBytes("utf-8");
            //9.根据密码器的初始化方式--加密：将数据加密
            byte[] byte_AES = cipher.doFinal(byte_encode);
            //10.将加密后的数据转换为字符串
            String AES_encode = Base64.encodeToString(byte_AES, Base64.NO_WRAP);
            //11.将字符串返回
            return AES_encode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果有错就返加nulll
        return null;
    }

    /*
     * 解密
     */
    public static String AESDecode(String encodeRules, String content) {
        if (TextUtils.isEmpty(encodeRules) || encodeRules.length() != 16) {//秘钥判断
            return null;
        }
        try {
            //5.根据字节数组生成AES密钥
            SecretKeySpec key = new SecretKeySpec(encodeRules.getBytes(), "AES");
            IvParameterSpec iv = new IvParameterSpec(encodeRules.getBytes("UTF-8"));
            //6.根据指定算法AES自成密码器
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            //8.将加密并编码后的内容解码成字节数组
            byte[] byte_content = Base64.decode(content, Base64.NO_WRAP);
            /*
             * 解密
             */
            byte[] byte_decode = cipher.doFinal(byte_content);
            String AES_decode = new String(byte_decode, "utf-8");
            return AES_decode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果有错就返加nulll
        return null;
    }

}
