package cn.chenchl.easyphone.weather.data

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import cn.chenchl.easyphone.weather.data.bean.CityWeather
import cn.chenchl.easyphone.weather.data.bean.JokeInfo
import cn.chenchl.easyphone.weather.data.dao.WeatherDao
import cn.chenchl.easyphone.weather.data.model.CityWeatherModel
import cn.chenchl.easyphone.weather.data.model.JokeListModel
import cn.chenchl.easyphone.weather.data.net.WeatherNetwork
import cn.chenchl.libs.Utils
import cn.chenchl.libs.network.retrofit.DefaultResponseSubscriber
import cn.chenchl.libs.network.retrofit.NetError
import cn.chenchl.libs.rxjava.RxJavaTransformers
import cn.chenchl.libs.utils.GSonUtil
import cn.chenchl.mvvm.repository.BaseRepository
import org.jetbrains.anko.toast

/**
 * created by ccl on 2020/1/12
 **/
class WeatherRepository(
    private val dao: WeatherDao,
    private val network: WeatherNetwork
) : BaseRepository() {

    val jokeList: MutableLiveData<List<JokeInfo>> = MutableLiveData()

    val weatherData: MutableLiveData<CityWeather> = MutableLiveData()

    @SuppressLint("CheckResult")
    fun getWeather(city: String, isRefresh: Boolean) {
        val weatherJson = dao.queryTodayWeather(city)
        if (TextUtils.isEmpty(weatherJson) || isRefresh) {
            //方案1（不推荐） 使用autoDispose自动管理 需传递lifecycle对象过来 容易造成内存泄漏
            /*network.getWeather(city)
                .compose(RxJavaTransformers.getDefaultScheduler())
                .`as`(RxLifecycleUtil.bindLifeCycle(lifecycle, Lifecycle.Event.ON_DESTROY))
                .subscribeWith(object : DefaultResponseSubscriber<CityWeatherModel, CityWeather>() {
                    override fun onSuccess(data: CityWeather?) {
                        weatherDataConversion(data)
                        dao.insertTodayWeather(toJson(data))
                        dao.insertCurrentCity(data?.city)
                        weatherData.value = data
                    }

                    override fun onFail(error: NetError) {
                        weatherData.value = null
                        Utils.getApp().toast(error.message!!)
                    }
                })*/
            //方案2（推荐） 加入compositeDisposable中 在Repository触发onCleared时取消 或在viewModel中使用disposeAll方法在任意生命周期自主取消 较灵活
            val dispose = network.getWeather(city)
                .compose(RxJavaTransformers.getDefaultScheduler())
                .subscribeWith(object : DefaultResponseSubscriber<CityWeatherModel, CityWeather>() {
                    override fun onSuccess(data: CityWeather?) {
                        weatherDataConversion(data)
                        dao.insertTodayWeather(city, GSonUtil.toJson(data))
                        weatherData.value = data
                    }

                    override fun onFail(error: NetError) {
                        weatherData.value = null
                        Utils.getApp().toast(error.message!!)
                    }
                })
            addSubscriber(dispose)
        } else {
            weatherData.value = GSonUtil.fromJson(weatherJson, CityWeather::class.java)
        }
    }

    suspend fun getWeatherByCoroutine(city: String, isRefresh: Boolean): CityWeatherModel {
        val weatherJson = dao.queryTodayWeather(city)
        return if (TextUtils.isEmpty(weatherJson) || isRefresh) {
            val responseData = network.getWeather1(city)
            //weatherDataConversion(responseData)
            dao.insertTodayWeather(city, GSonUtil.toJson(responseData))
            responseData
        } else {
            GSonUtil.fromJson(weatherJson, CityWeatherModel::class.java)
        }
    }


    @SuppressLint("CheckResult")
    fun getJokeList(isRefresh: Boolean) {
        dao.queryJokeList {
            if (it.isEmpty() || isRefresh) {
                val dispose = network.getJokeList()
                    .compose(RxJavaTransformers.getDefaultScheduler())
                    .subscribeWith(object :
                        DefaultResponseSubscriber<JokeListModel, List<JokeInfo>>() {
                        override fun onSuccess(data: List<JokeInfo>?) {
                            dao.insertJokeList(data)
                            jokeList.value = data
                        }

                        override fun onFail(error: NetError) {
                            jokeList.value = null
                            Utils.getApp().toast(error.message!!)
                        }
                    })
                addSubscriber(dispose)
            } else {
                jokeList.value = it
            }
        }
    }


    fun insertCurrentCity(city: String) {
        dao.insertCurrentCity(city)
    }

    /**
     * 根据wid查询后替换成真正的具体内容
     */
    private fun weatherDataConversion(data: CityWeather?) {
        if (data != null) {
            data.realtime.wid = dao.queryWeatherKindByWid(data.realtime.wid)?.weather ?: ""
            for (future: CityWeather.Future in data.future) {
                future.wid.day = dao.queryWeatherKindByWid(future.wid.day)?.weather ?: ""
                future.wid.night = dao.queryWeatherKindByWid(future.wid.night)?.weather ?: ""
            }
        }

    }
}