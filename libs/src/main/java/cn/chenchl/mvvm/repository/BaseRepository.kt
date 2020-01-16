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

    private val gson: Gson = Gson()

    override fun onCleared() {
        compositeDisposable.dispose()
    }

    override fun accept(disposable: Disposable?) {
        disposable?.let { compositeDisposable.add(disposable) }
    }

    fun addSubscriber(disposable: Disposable) = compositeDisposable.add(disposable)

    fun <T> toJson(objects: T): String = gson.toJson(objects)

    fun <T> fromJson(json: String?, clazz: Class<T>): T? {
        return try {
            gson.fromJson(json, clazz)
        } catch (e: Exception) {
            LogUtil.e(e.message)
            e.printStackTrace()
            null
        }
    }

}