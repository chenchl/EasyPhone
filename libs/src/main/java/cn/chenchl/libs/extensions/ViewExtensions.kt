package cn.chenchl.libs.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import cn.chenchl.libs.FastClickUtils


/**
 * created by ccl on 2020/1/7
 **/
/**
 * 获取状态栏高度
 */
@SuppressLint("PrivateApi")
fun Context.getStatusBarHeight(): Int {
    var statusHeight = -1
    try {
        val clazz = Class.forName("com.android.internal.R\$dimen")
        val `object` = clazz.newInstance()
        val height = clazz.getField("status_bar_height")[`object`].toString().toInt()
        statusHeight = resources.getDimensionPixelSize(height)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return statusHeight
}

/**
 * 任何类均可随时调用runUiThread
 */
inline fun <T> T.runUiThread(crossinline task: T.() -> Unit) {
    val mainHandler = Handler(Looper.getMainLooper())
    mainHandler.post {
        task()
    }
}

/**
 * 对view点击事件设置防抖拦截
 */
inline fun <V : View> V.setOnNoFastClickListener(crossinline task: (V) -> Unit) {
    setOnClickListener {
        if (!FastClickUtils.isFastClick)
            task(this)
    }
}
