package cn.chenchl.easyphone.weather.data.database

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import cn.chenchl.easyphone.weather.data.bean.JokeInfo
import cn.chenchl.libs.rxjava.RxJavaTransformers
import io.reactivex.disposables.Disposable

/**
 * created by ccl on 2020/1/22
 **/
class JokeDataLiveData : LiveData<List<JokeInfo>>() {
    private val mJokeDao: JokeDataBaseDao = JokeDatabase.instance.jokeDao()
    private var disposable: Disposable? = null

    @SuppressLint("CheckResult")
    override fun onActive() {
        super.onActive()
        disposable = mJokeDao.queryAll()
            .compose(RxJavaTransformers.getDefaultScheduler())
            .subscribe { value = it }
    }

    override fun onInactive() {
        super.onInactive()
        disposable?.dispose()
    }

}