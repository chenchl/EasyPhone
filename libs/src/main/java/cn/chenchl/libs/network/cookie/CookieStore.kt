package cn.chenchl.libs.network.cookie

import okhttp3.Cookie
import okhttp3.HttpUrl

interface CookieStore {
    fun add(uri: HttpUrl, cookie: List<Cookie>)
    operator fun get(uri: HttpUrl): List<Cookie>
    val cookies: List<Cookie>
    fun remove(uri: HttpUrl, cookie: Cookie): Boolean
    fun removeAll(): Boolean
}