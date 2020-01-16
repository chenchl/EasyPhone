package cn.chenchl.easyphone.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * created by ccl on 2020/1/17
 **/
object TimeUtils {

    fun getTimeByUnixTime(unixTime: String): String {
        val sdr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        val i = Integer.parseInt(unixTime)
        return sdr.format(Date(i * 1000L))

    }
}