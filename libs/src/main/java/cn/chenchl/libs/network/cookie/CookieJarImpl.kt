package cn.chenchl.libs.network.cookie

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * Created by zhy on 16/3/10.
 */
class CookieJarImpl(private val cookieStore: CookieStore) :
    CookieJar {
    @Synchronized
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) = cookieStore.add(url, cookies)

    @Synchronized
    override fun loadForRequest(url: HttpUrl): List<Cookie> = cookieStore[url]
}