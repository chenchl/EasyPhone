package cn.chenchl.libs.extensions

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import cn.chenchl.libs.Utils
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * created by ccl on 2020/1/11
 **/

/**
 * @description 权限检查 有权限直接运行 没有权限则去申请权限后执行
 * @date: 2020/1/11 16:26
 * @author: ccl
 * @param
 * @return
 */
@SuppressLint("CheckResult")
inline fun <T : FragmentActivity> T.checkPermissions(
    vararg permissionNames: String,
    crossinline noPermission: T.(List<String>) -> Unit = { permissionsDialog(it) },
    crossinline task: T.() -> Unit
) {
    RxPermissions(this)
        .request(*permissionNames)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe {
            if (it) {
                task()
            } else {
                val noPermissionNames: ArrayList<String> = ArrayList()
                //查询哪些权限尚未申请到
                for (name in permissionNames) {

                    if (ContextCompat.checkSelfPermission(
                            Utils.getApp(),
                            name
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        noPermissionNames.add(name)
                    }
                }
                noPermission(noPermissionNames)
            }
        }
}

/**
 * @description 弹窗提示前往设置权限
 * @date: 2020/1/11 17:41
 * @author: ccl
 * @param
 * @return
 */
fun <T : FragmentActivity> T.permissionsDialog(
    it: List<String>
) {
    if (!this.isFinishing) {
        val permissionNames = it.toString()
        AlertDialog.Builder(this)
            .setTitle("权限缺失")
            .setMessage("缺失以下权限:\n$permissionNames\n是否前往设置开启？")
            .setCancelable(false)
            .setPositiveButton("前往设置") { dialog, _ ->
                dialog.dismiss()
                toAppSetting()
            }
            .setNegativeButton("下次再说") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .create()
            .show()
    }
}

/**
 * @description 权限检查 有权限直接运行 没有权限则去申请权限后执行
 * @date: 2020/1/11 16:26
 * @author: ccl
 * @param
 * @return
 */
@SuppressLint("CheckResult")
inline fun <T : Fragment> T.checkPermissions(
    vararg permissionNames: String,
    crossinline noPermission: T.(List<String>) -> Unit = { permissionsDialog(it) },
    crossinline task: T.() -> Unit
) {
    RxPermissions(this)
        .request(*permissionNames)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe {
            if (it) {
                task()
            } else {
                val noPermissionNames: ArrayList<String> = ArrayList()
                //查询哪些权限尚未申请到
                for (name in permissionNames) {

                    if (ContextCompat.checkSelfPermission(
                            Utils.getApp(),
                            name
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        noPermissionNames.add(name)
                    }
                }
                noPermission(noPermissionNames)
            }
        }
}

/**
 * @description 弹窗提示前往设置权限
 * @date: 2020/1/11 17:41
 * @author: ccl
 * @param
 * @return
 */
fun <T : Fragment> T.permissionsDialog(
    it: List<String>
) {
    if (this.activity != null) {
        val permissionNames = it.toString()
        AlertDialog.Builder(this.activity!!)
            .setTitle("权限缺失")
            .setMessage("缺失以下权限:\n$permissionNames\n是否前往设置开启？")
            .setCancelable(false)
            .setPositiveButton("前往设置") { dialog, _ ->
                dialog.dismiss()
                toAppSetting()
            }
            .setNegativeButton("下次再说") { dialog, _ ->
                dialog.dismiss()
                this.activity?.finish()
            }
            .create()
            .show()
    }
}

private fun toAppSetting() {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = Uri.parse("package:" + Utils.getApp().packageName)
        val intentAvailable = Utils.getApp()
            .packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size
        if (intentAvailable > 0) {
            Utils.getApp().startActivity(intent)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}