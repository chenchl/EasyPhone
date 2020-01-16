package cn.chenchl.mvvm

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import cn.chenchl.libs.log.LogUtil
import java.lang.ref.WeakReference

/**
 * created by ccl on 2020/1/12
 **/
open class BaseViewModel : ViewModel(),
    DefaultLifecycleObserver {

    private var lifecycleOwner: WeakReference<LifecycleOwner>? = null

    override fun onCleared() {
        lifecycleOwner?.clear()
    }

    fun injectLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = WeakReference(lifecycleOwner)
    }

    fun getLifecycleOwner() = this.lifecycleOwner!!

    fun getLifecycle() = getLifecycleOwner().get()?.lifecycle

    override fun onCreate(owner: LifecycleOwner) {
        LogUtil.e("onCreate$owner")
    }

    override fun onStart(owner: LifecycleOwner) {
        LogUtil.e("onStart$owner")
    }

    override fun onResume(owner: LifecycleOwner) {
        LogUtil.e("onResume$owner")
    }

    override fun onPause(owner: LifecycleOwner) {
        LogUtil.e("onPause$owner")
    }


    override fun onStop(owner: LifecycleOwner) {
        LogUtil.e("onStop$owner")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        LogUtil.e("onDestroy$owner")
        lifecycleOwner?.clear()
    }


}