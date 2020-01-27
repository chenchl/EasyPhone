package cn.chenchl.libs.cache

import android.text.TextUtils
import android.util.LruCache

/**
 * Created by ccl on 2019/02/20.
 * 内存缓存类 lrucache实现
 */
class MemoryCache private constructor() {
    private val cache: LruCache<String?, Any?>

    init {
        val maxMemory = Runtime.getRuntime().maxMemory().toInt()
        val cacheSize = maxMemory / 8
        cache = LruCache(cacheSize)
    }

    private object Holder {
        val INSTANCE =
            MemoryCache()
    }

    companion object {
        val instance: MemoryCache
            get() = Holder.INSTANCE
    }

    @Synchronized
    fun put(key: String?, value: Any?) {
        if (TextUtils.isEmpty(key)) return
        if (cache[key] != null) {
            cache.remove(key)
        }
        cache.put(key, value)
    }

    operator fun get(key: String?): Any? {
        return cache[key]
    }

    @Suppress("UNCHECKED_CAST")
    @Synchronized
    operator fun <T> get(key: String?, clazz: Class<T>?): T? {
        try {
            return cache[key] as T?
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun remove(key: String?) {
        if (cache[key] != null) {
            cache.remove(key)
        }
    }

    operator fun contains(key: String?) = cache[key] != null

    fun clear() {
        cache.evictAll()
    }
}