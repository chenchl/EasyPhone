package cn.chenchl.libs.codec;

import android.util.Base64;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DESCryptoUtil {

    /*public static final String DEFAULT_SECRET_KEY1 = "?:P)(OL><KI*&UJMNHY^%TGBVFR$#EDCXSWIENG@!QAZ";
    public static final String KEY = "iENG365@20190326175726!365";*/
    public static final String DESKEY = "?:P)(OL><KI*&UJMNHY^%TGBVFR$#EDCXSWIENG@!QAZ";

    public static final String DES = "DES";
    public static final String DESA = "DES/CBC/PKCS5Padding";

    public static String encodeData(String data) {
        String m = encryptDES(data, DESKEY);
        return m;
    }

    public static String decodeData(String data) {
        try {
            String n = decryptDES(data, DESKEY);
            return n;
        } catch (Exception e) {
            return null;
        }
    }

    //初始化向量，随意填写
    private static final byte[] iv = {1, 2, 3, 4, 5, 6, 7, 8};


    /**
     * 加密
     *
     * @param encryptString 明文
     * @param encryptKey    密钥
     * @return 加密后的密文
     */
    public static String encryptDES(String encryptString, String encryptKey) {
        try {
            DESKeySpec dks = new DESKeySpec(encryptKey.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            Key secretKey = keyFactory.generateSecret(dks);
            //实例化IvParameterSpec对象，使用指定的初始化向量
            IvParameterSpec zeroIv = new IvParameterSpec(iv);
            //创建密码器
            Cipher cipher = Cipher.getInstance(DESA);
            //用密钥初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, zeroIv);
            //执行加密操作
            byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
            return Base64.encodeToString(encryptedData, 0);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 解密
     *
     * @param decryptString 密文
     * @param decryptKey    密钥
     * @return 返回明文
     */

    public static String decryptDES(String decryptString, String decryptKey) {

        try {
            //先使用Base64解密
            byte[] byteMi = Base64.decode(decryptString, 0);
            // key的长度不能够小于8位字节
            DESKeySpec dks = new DESKeySpec(decryptKey.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            Key secretKey = keyFactory.generateSecret(dks);
            //实例化IvParameterSpec对象使用指定的初始化向量
            IvParameterSpec zeroIv = new IvParameterSpec(iv);
            //实例化SecretKeySpec，根据传入的密钥获得字节数组来构造SecretKeySpec,
            //创建密码器
            Cipher cipher = Cipher.getInstance(DESA);
            //用密钥初始化Cipher对象,上面是加密，这是解密模式
            cipher.init(Cipher.DECRYPT_MODE, secretKey, zeroIv);
            //获取解密后的数据
            byte[] decryptedData = cipher.doFinal(byteMi);
            return new String(decryptedData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

}