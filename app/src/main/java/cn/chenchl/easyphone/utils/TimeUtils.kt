package cn.chenchl.easyphone.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * created by ccl on 2020/1/17
 **/
object TimeUtils {
    @JvmStatic
    fun getTimeByUnixTime(unixTime: Long): String {
        val sdr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        return sdr.format(Date(unixTime * 1000L))

    }
}