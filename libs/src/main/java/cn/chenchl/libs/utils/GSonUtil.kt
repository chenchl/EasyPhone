package cn.chenchl.libs.utils

import cn.chenchl.libs.log.LogUtil
import com.google.gson.Gson
import java.lang.reflect.Type

/**
 * created by ccl on 2020/1/19
 **/
object GSonUtil {

    val gSon: Gson = Gson()

    fun <T> toJson(objects: T): String = gSon.toJson(objects)

    inline fun <reified T> fromJson(json: String?, clazz: Class<T>): T {
        return try {
            gSon.fromJson(json, clazz)
        } catch (e: Exception) {
            LogUtil.e(e.message)
            T::class.java.newInstance()
        }
    }

    inline fun <reified T> fromJson(json: String?, typeOfT: Type): T {
        return try {
            gSon.fromJson(json, typeOfT)
        } catch (e: Exception) {
            LogUtil.e(e.message)
            T::class.java.newInstance()
        }
    }
}