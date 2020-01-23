package cn.chenchl.easyphone.weather.data.dao

import android.annotation.SuppressLint
import android.text.TextUtils
import cn.chenchl.easyphone.weather.data.bean.JokeInfo
import cn.chenchl.easyphone.weather.data.bean.WeatherKind
import cn.chenchl.easyphone.weather.data.database.JokeDatabase
import cn.chenchl.libs.cache.LocalCache
import cn.chenchl.libs.file.FileUtils
import cn.chenchl.libs.log.LogUtil
import cn.chenchl.libs.rxjava.RxJavaTransformers
import cn.chenchl.libs.utils.GSonUtil
import cn.chenchl.mvvm.repository.BaseDao
import com.google.gson.reflect.TypeToken
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * created by ccl on 2020/1/15
 **/
object WeatherDao : BaseDao() {

    private val jokeDao = JokeDatabase.instance.jokeDao()

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

    private val weatherByWidList: ArrayList<WeatherKind> by lazy { loadWeatherKindFromAsset() }

    fun queryTodayWeather(city: String): String? {
        val today = sdf.format(Date())
        return LocalCache["$city - $today", ""]
    }

    fun insertTodayWeather(city: String, weatherJson: String) {
        val today = sdf.format(Date())
        LocalCache["$city - $today"] = weatherJson
    }

    @SuppressLint("CheckResult")
    fun queryJokeList(result: (List<JokeInfo>) -> Unit) {
        jokeDao.queryAll()
            .compose(RxJavaTransformers.getSingleScheduler())
            .subscribe({
                LogUtil.i("Room", it.toString())
                result(it)
            }, {
                LogUtil.i("Room", it.message!!)
                result(ArrayList())
            })
    }

    fun insertJokeList(data: List<JokeInfo>?) {
        Flowable.create<Int>(
            { emitter: FlowableEmitter<Int> ->
                jokeDao.deleteAll()
                data?.let { jokeDao.insertJokeList(*data.toTypedArray()) }
                emitter.onNext(1)
                emitter.onComplete()
            },
            BackpressureStrategy.BUFFER
        ).subscribeOn(Schedulers.io())
            .subscribe()
    }

    private fun loadWeatherKindFromAsset(): ArrayList<WeatherKind> {
        return try {
            val stringFromAssetFile = FileUtils.getStringFromAssetFile("weather_kind")
            val listType = object :
                TypeToken<ArrayList<WeatherKind>>() {}.type
            GSonUtil.fromJson(stringFromAssetFile, listType)
        } catch (e: Exception) {
            e.printStackTrace()
            ArrayList()
        }
    }

    fun queryWeatherKindByWid(wid: String): WeatherKind? {
        for (weather in weatherByWidList) {
            if (TextUtils.equals(weather.wid, wid)) {
                return weather
            }
        }
        return null
    }

    fun insertCurrentCity(city: String?) {
        if (city != null)
            LocalCache["currentCity"] = city
    }

}