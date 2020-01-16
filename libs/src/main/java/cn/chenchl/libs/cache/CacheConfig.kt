package cn.chenchl.libs.cache

/**
 * created by ccl on 2020/1/9
 **/
class CacheConfig(
    var cryptKey: String = MMKV_CRYPTKEY,
    var filename: String = NATIVE_FILENAME,
    var filePath: String = FILE_PATH
) {
    companion object {
        const val NATIVE_FILENAME = "default_cache"
        const val MMKV_CRYPTKEY = "my-encrypt-key"
        const val FILE_PATH = ""
    }
}