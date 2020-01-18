package cn.chenchl.mvvm.repository

import cn.chenchl.libs.log.LogUtil
import com.google.gson.Gson
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/**
 * created by ccl on 2020/1/12
 **/
abstract class BaseRepository<Dao : BaseDao, Net : BaseNet>(
    protected val dao: Dao,
    protected val network: Net
) : IRepository, Consumer<Disposable> {

    private val compositeDisposable by lazy { CompositeDisposable() }

    private val gSon: Gson = Gson()

    override fun onCleared() {
        // dispose()调用后 后续加入CompositeDisposable的所有Disposable对象在加入时就会被dispose掉，
        // 放在onCleared里比较合适 因为onCleared的调用场景是整个Repository对象要被回收的时候
        compositeDisposable.dispose()
    }

    fun disposeAll() {
        //clear()调用只会清除当前在compositeDisposable set集合中的disposable对象 不会影响后续新加入的disposable对象
        compositeDisposable.clear()
    }

    override fun accept(disposable: Disposable?) {
        disposable?.let { compositeDisposable.add(disposable) }
    }

    fun addSubscriber(disposable: Disposable) = compositeDisposable.add(disposable)

    fun <T> toJson(objects: T): String = gSon.toJson(objects)

    fun <T> fromJson(json: String?, clazz: Class<T>): T? {
        return try {
            gSon.fromJson(json, clazz)
        } catch (e: Exception) {
            LogUtil.e(e.message)
            e.printStackTrace()
            null
        }
    }

}