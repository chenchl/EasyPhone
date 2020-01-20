package cn.chenchl.libs.file

import cn.chenchl.libs.Utils
import java.io.InputStreamReader

/**
 * created by ccl on 2020/1/15
 **/
object FileUtils {

    fun getStringFromAssetFile(fileName: String): String {
        var str = ""
        try {
            str = InputStreamReader(Utils.getApp().assets.open(fileName)).readText()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            return str
        }
    }
}