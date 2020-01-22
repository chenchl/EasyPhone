package cn.chenchl.libs.rxjava

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * created by ccl on 2019/11/29
 * RxJava常用compose Transformer
 */
object RxJavaTransformers {
    /**
     * 线程切换
     *
     * @return
     */
    fun <T> getDefaultScheduler(): FlowableTransformer<T, T> {
        return FlowableTransformer { upstream: Flowable<T> ->
            upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    fun <T> getObservableScheduler(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream: Observable<T> ->
            upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    fun <T> getSingleScheduler(): SingleTransformer<T, T> {
        return SingleTransformer { upstream: Single<T> ->
            upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }
}