package cn.chenchl.libs.log

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Process
import android.text.TextUtils
import cn.chenchl.libs.Config.isLogOpen
import cn.chenchl.libs.Utils
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import java.io.BufferedReader
import java.io.FileReader

/**
 * created by ccl on 2019/2/14
 */
object LogUtil {
    var isDebug = isLogOpen // LOG开关
    private fun getIsLogEnable(context: Context): Boolean {
        val appInfo: ApplicationInfo?
        return try {
            appInfo = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
            val environment = appInfo.metaData.getInt("LOG_ENABLE")
            environment == 1
        } catch (e: Exception) {
            true
        }
    }

    //初始化logger
    fun init(context: Context) {
        try { // 获取当前包名
            val packageName = context.applicationContext.packageName
            // 获取当前进程名
            val processName =
                getProcessName(Process.myPid())
            if (processName == null || processName == packageName) { //保证只在主进程初始化一次
//打印到logcat
                val logcatFormatStrategy = PrettyFormatStrategy.newBuilder()
                    .tag(applicationName)
                    .build()
                Logger.addLogAdapter(object :
                    AndroidLogAdapter(logcatFormatStrategy) {
                    override fun isLoggable(
                        priority: Int,
                        tag: String?
                    ): Boolean {
                        return isDebug
                    }
                })
                e("logger", "init success test")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val applicationName: String
        get() {
            val packageManager: PackageManager
            val applicationInfo: ApplicationInfo
            try {
                packageManager = Utils.getApp().packageManager
                applicationInfo = packageManager.getApplicationInfo(
                    Utils.getApp().packageName,
                    0
                )
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return "test"
            }
            return packageManager.getApplicationLabel(applicationInfo) as String
        }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private fun getProcessName(pid: Int): String? {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
            var processName = reader.readLine()
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim { it <= ' ' }
            }
            return processName
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        } finally {
            try {
                reader?.close()
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
        return null
    }

    fun d(tag: String?, msg: String) {
        if (isDebug) {
            if (!TextUtils.isEmpty(msg)) {
                Logger.d("$tag---$msg")
            }
        }
    }

    fun e(tag: String?, msg: String) {
        if (isDebug) {
            if (!TextUtils.isEmpty(msg)) {
                Logger.e("$tag---$msg")
            }
        }
    }

    fun d(msg: String?) {
        if (isDebug) {
            if (!TextUtils.isEmpty(msg)) {
                Logger.d(msg)
            }
        }
    }

    fun e(msg: String?) {
        if (isDebug) {
            if (!TextUtils.isEmpty(msg)) {
                Logger.e(msg!!)
            }
        }
    }

    fun i(tag: String?, msg: String) {
        if (isDebug) {
            if (!TextUtils.isEmpty(msg)) {
                Logger.i("$tag---$msg")
            }
        }
    }

    fun i(msg: String?) {
        if (isDebug) {
            if (!TextUtils.isEmpty(msg)) {
                Logger.i(msg!!)
            }
        }
    }

    fun json(msg: String?) {
        if (isDebug) {
            if (!TextUtils.isEmpty(msg)) {
                Logger.json(msg)
            }
        }
    }
}