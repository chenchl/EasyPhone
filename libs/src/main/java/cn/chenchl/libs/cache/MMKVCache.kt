package cn.chenchl.libs.cache

import android.content.Context
import android.os.Parcelable
import cn.chenchl.libs.log.LogUtil
import com.tencent.mmkv.MMKV

/**
 * created by ccl on 2020/1/7
 **/
class MMKVCache private constructor() : ICache {

    override var config: CacheConfig = CacheConfig()

    private val TAG = MMKVCache::class.simpleName
    private var isInit = false

    companion object {
        private val instance by lazy { MMKVCache() }

        fun get() = instance
    }


    override fun init(context: Context) {
        if (isInit) {
            throw IllegalArgumentException("MMKVUtil has been init")
        }
        if (config.filePath.isEmpty())
            initMMKV(context)
        else
            initMMKV(config.filePath)
        isInit = true
    }

    override operator fun <T> set(fileName: String, key: String, value: T) =
        _put(fileName, key, value)

    override operator fun <T> get(fileName: String, key: String, defValue: T): T? =
        _get(fileName, key, defValue)

    override fun remove(fileName: String, key: String) = _remove(fileName, key)

    override fun contains(fileName: String, key: String): Boolean? = _contains(fileName, key)

    override fun clear(fileName: String) = _clearAll(fileName)

    private fun initMMKV(context: Context) {
        val rootDir = MMKV.initialize(context)
        LogUtil.i(TAG!!, "mmkv init root: $rootDir")
    }

    private fun initMMKV(path: String) {
        val rootDir = MMKV.initialize(path)
        LogUtil.i(TAG!!, "mmkv init root: $rootDir")
    }

    private fun <T> _put(fileName: String, key: String, value: T) {
        val mmkv = MMKV.mmkvWithID(fileName, MMKV.SINGLE_PROCESS_MODE, config.cryptKey)
        var result = false
        when (value) {
            is Int -> {
                result = mmkv.encode(key, value as Int)
            }
            is Long -> {
                result = mmkv.encode(key, value as Long)
            }
            is Float -> {
                result = mmkv.encode(key, value as Float)
            }
            is Double -> {
                result = mmkv.encode(key, value as Double)
            }
            is Boolean -> {
                result = mmkv.encode(key, value as Boolean)
            }
            is String -> {
                result = mmkv.encode(key, value as String)
            }
            is ByteArray -> {
                result = mmkv.encode(key, value as ByteArray)
            }
            is Parcelable -> {
                result = mmkv.encode(key, value as Parcelable)
            }
        }
        LogUtil.i(TAG!!, "mmkv $fileName _put :$key ,value = $value result = $result")
    }

    private fun <T> _get(fileName: String, key: String, defValue: T): T? {
        val mmkv = MMKV.mmkvWithID(fileName, MMKV.SINGLE_PROCESS_MODE, config.cryptKey)
        var result: T? = null
        when (defValue) {
            is Int -> {
                result = mmkv.decodeInt(key, defValue) as T
            }
            is Long -> {
                result = mmkv.decodeLong(key, defValue) as T
            }
            is Float -> {
                result = mmkv.decodeFloat(key, defValue) as T
            }
            is Double -> {
                result = mmkv.decodeDouble(key, defValue) as T
            }
            is Boolean -> {
                result = mmkv.decodeBool(key, defValue) as T
            }
            is String -> {
                result = mmkv.decodeString(key, defValue) as T
            }
            is ByteArray -> {
                result = mmkv.decodeBytes(key, defValue) as T
            }
            is Parcelable -> {
                val objects: Parcelable = defValue
                result = mmkv.decodeParcelable(key, objects.javaClass, objects) as T
            }
        }
        LogUtil.i(TAG!!, "mmkv $fileName _get :$key value = $result")
        return result
    }

    private fun _remove(fileName: String, key: String) {
        val kv = MMKV.mmkvWithID(fileName, MMKV.SINGLE_PROCESS_MODE, config.cryptKey)
        kv.removeValueForKey(key)
        LogUtil.i(TAG!!, "mmkv $fileName remove :$key")
    }

    private fun _contains(fileName: String, key: String): Boolean {
        val kv = MMKV.mmkvWithID(fileName, MMKV.SINGLE_PROCESS_MODE, config.cryptKey)
        val containsKey = kv.containsKey(key)
        LogUtil.i(TAG!!, "mmkv $fileName contains $key:$containsKey")
        return containsKey
    }

    private fun _clearAll(fileName: String) {
        val kv = MMKV.mmkvWithID(fileName, MMKV.SINGLE_PROCESS_MODE, config.cryptKey)
        kv.clearAll()
        LogUtil.i(TAG!!, "mmkv $fileName clearAll")
    }

}