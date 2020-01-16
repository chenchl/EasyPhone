package cn.chenchl.libs.cache

import android.content.Context

/**
 * created by ccl on 2020/1/7
 **/
object LocalCache : ICache {

    override lateinit var config: CacheConfig

    private var cache: ICache? = null

    fun init(cache: ICache, context: Context) {
        this.cache = cache;
        this.config = cache.config
        this.cache?.init(context)
    }

    /**
     * 默认使用mmkv 默认config
     */
    override fun init(context: Context) {
        cache = MMKVCache.get()
        this.config = cache?.config!!
        cache?.init(context)
    }

    override operator fun <T> set(fileName: String, key: String, value: T) {
        cache?.set(fileName, key, value)
    }

    operator fun <T> set(key: String, value: T) {
        cache?.set(key = key, value = value)
    }

    override operator fun <T> get(fileName: String, key: String, defValue: T): T? =
        cache?.get(fileName, key, defValue)

    operator fun <T> get(key: String, defValue: T): T? =
        cache?.get(key = key, defValue = defValue)

    override fun remove(fileName: String, key: String) = cache?.remove(fileName, key)

    override fun contains(fileName: String, key: String): Boolean? = cache?.contains(fileName, key)

    operator fun contains(key: String): Boolean {
        var result = cache?.contains(key = key)
        return result ?: false
    }

    override fun clear(fileName: String) = cache?.clear(fileName)
}