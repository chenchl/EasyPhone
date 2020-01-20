/**
 *
 */
package cn.chenchl.libs.network.cookie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;


/**
 * @author
 */
public class WebViewCookieUtils {
    private final String KCookieDel = "deleteMe";
    public static final String KJsessionidCookie = "JSESSIONID_COOKIE";
    private Context mContext;
    private String mCookies = "";
    @SuppressLint("StaticFieldLeak")
    private static WebViewCookieUtils mCookieUtils = null;

    public WebViewCookieUtils(Context context) {
        this.mContext = context;
    }

    public static synchronized WebViewCookieUtils getInstance(Context context) {
        if (mCookieUtils == null) {
            mCookieUtils = new WebViewCookieUtils(context);
        }
        return mCookieUtils;
    }

    /**
     * 保存cookie
     * * @author
     */
    public void saveCookie(String cookie) {
        if (TextUtils.isEmpty(cookie) || KCookieDel.equals(cookie)) {
            return;
        }
        String cookieStr = KJsessionidCookie + "=" + cookie;
        if (cookieStr.equals(mCookies)) {
            return;
        }
        mCookies = cookieStr;
    }

    /**
     * 同步一下webview cookie
     *
     * @author
     */
    public void synCookies(String url) {
        if (TextUtils.isEmpty(mCookies) || KCookieDel.equals(mCookies)) {
            return;
        }
        CookieSyncManager.createInstance(mContext);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
//        cookieManager.removeSessionCookie();//移除
//        cookieManager.removeAllCookie();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cookieManager.setCookie(url, mCookies);
        CookieSyncManager.getInstance().sync();
//        if (Build.VERSION.SDK_INT < 21) {
//            CookieSyncManager.getInstance().sync();
//        } else {
//            CookieManager.getInstance().flush();
//        }
    }

    public String getCookie() {
        return mCookies;
    }
}
