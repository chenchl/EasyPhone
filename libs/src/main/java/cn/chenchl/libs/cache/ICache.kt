package cn.chenchl.libs.cache

import android.content.Context

/**
 * created by ccl on 2020/1/7
 **/
interface ICache {

    var config: CacheConfig

    /**
     * 初始化
     */
    fun init(context: Context)

    /**
     * 存储数据
     */
    fun <T> set(fileName: String = config.filename, key: String, value: T)

    /**
     * 获取数据 (带默认值 必定不为空)
     */
    fun <T> get(fileName: String = config.filename, key: String, defValue: T): T

    /**
     * 删除key
     */
    fun remove(fileName: String = config.filename, key: String): Unit?

    /**
     * 是否包含key
     */
    fun contains(fileName: String = config.filename, key: String): Boolean?

    /**
     * 删除key
     */
    fun clear(fileName: String = config.filename): Unit?
}