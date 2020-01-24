package cn.chenchl.libs.cache

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * created by ccl on 2020/1/7
 * cache委托
 **/
object LocalCacheDelegates {
    fun <T> localCache(key: String, defValue: T, fileName: String = LocalCache.config.filename) =
        LocalCacheProperty(
            key,
            defValue,
            fileName
        )

    class LocalCacheProperty<T>(
        private val key: String,
        private val defValue: T,
        private val fileName: String
    ) :
        ReadWriteProperty<Any?, T?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T =
            LocalCache.get(fileName, key, defValue)


        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) =
            LocalCache.set(fileName, key, value)
    }
}