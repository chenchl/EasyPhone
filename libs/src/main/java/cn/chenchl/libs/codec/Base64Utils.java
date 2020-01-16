package cn.chenchl.libs.codec;

import android.util.Base64;

/**
 * created by ccl on 2019/2/20
 **/
public class Base64Utils {

    public static byte[] encode(byte[] plain) {
        return Base64.encode(plain, Base64.NO_WRAP);
    }

    /*
     *Base64.DEFAULT 这个参数是默认，使用默认的方法来加密
     * NO_PADDING 这个参数是略去加密字符串最后的”=”
     * NO_WRAP 这个参数意思是略去所有的换行符（设置后CRLF就没用了）
     * CRLF 这个参数看起来比较眼熟，它就是Win风格的换行符，意思就是使用CR LF这一对作为一行的结尾而不是Unix风格的LF
     * URL_SAFE 这个参数意思是加密时不使用对URL和文件名有特殊意义的字符来作为加密字符，具体就是以-和_取代+和
     * https://www.cnblogs.com/whoislcj/p/5887859.html
     */
    public static String encodeToString(byte[] plain) {
        return Base64.encodeToString(plain, Base64.NO_WRAP);
    }

    public static byte[] decode(String text) {
        return Base64.decode(text, Base64.NO_WRAP);
    }

    public static byte[] decode(byte[] text) {
        return Base64.decode(text, Base64.NO_WRAP);
    }
}
