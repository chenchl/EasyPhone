package cn.chenchl.libs

import android.os.SystemClock

/**
 * 防连点工具类
 */
object FastClickUtils {

    private var lastClickTime: Long = 0 //上次点击的时间

    private const val spaceTime = Config.clickIntervalTime //时间间隔//是否允许点击

    val isFastClick: Boolean //是否是连续点击
        get() {
            val currentTime = SystemClock.uptimeMillis() //当前系统时间
            val isAllowClick = currentTime - lastClickTime <= spaceTime //是否允许点击
            if (!isAllowClick) lastClickTime = currentTime //当距离第一次点击大于spaceTime时 重置时间
            return isAllowClick
        }
}