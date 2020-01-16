package cn.chenchl.libs.rxjava

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.AutoDisposeConverter
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider

/**
 * created by ccl on 2019/11/29
 * AutoDispose封装
 */
object RxLifecycleUtil {
    fun <T> bindLifeCycle(lifecycleOwner: LifecycleOwner?): AutoDisposeConverter<T> =
        AutoDispose.autoDisposable(
            AndroidLifecycleScopeProvider.from(lifecycleOwner)
        )

    fun <T> bindLifeCycle(lifecycle: Lifecycle): AutoDisposeConverter<T> =
        AutoDispose.autoDisposable(
            AndroidLifecycleScopeProvider.from(lifecycle)
        )

    fun <T> bindLifeCycle(
        lifecycleOwner: LifecycleOwner,
        event: Lifecycle.Event
    ): AutoDisposeConverter<T> =
        AutoDispose.autoDisposable(
            AndroidLifecycleScopeProvider.from(lifecycleOwner, event)
        )

    fun <T> bindLifeCycle(lifecycle: Lifecycle, event: Lifecycle.Event): AutoDisposeConverter<T> =
        AutoDispose.autoDisposable(
            AndroidLifecycleScopeProvider.from(lifecycle, event)
        )
}