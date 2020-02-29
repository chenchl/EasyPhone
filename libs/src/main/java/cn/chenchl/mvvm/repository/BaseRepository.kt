package cn.chenchl.mvvm.repository

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * created by ccl on 2020/1/12
 **/
abstract class BaseRepository : IRepository {

    private val compositeDisposable by lazy { CompositeDisposable() }

    override fun onCleared() {
        // dispose()调用后 后续加入CompositeDisposable的所有Disposable对象在加入时就会被dispose掉，
        // 放在onCleared里比较合适 因为onCleared的调用场景是整个Repository对象要被回收的时候
        compositeDisposable.dispose()
    }

    fun disposeAll() {
        //clear()调用只会清除当前在compositeDisposable set集合中的disposable对象 不会影响后续新加入的disposable对象
        compositeDisposable.clear()
    }

    fun addSubscriber(disposable: Disposable) = compositeDisposable.add(disposable)

}