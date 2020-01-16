package cn.chenchl.easyphone.weather.data

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import cn.chenchl.easyphone.weather.data.bean.CityWeather
import cn.chenchl.easyphone.weather.data.dao.WeatherDao
import cn.chenchl.easyphone.weather.data.model.CityWeatherModel
import cn.chenchl.easyphone.weather.data.net.WeatherNetwork
import cn.chenchl.libs.Utils
import cn.chenchl.libs.network.retrofit.DefaultResponseSubscriber
import cn.chenchl.libs.network.retrofit.NetError
import cn.chenchl.libs.rxjava.RxJavaTransformers
import cn.chenchl.libs.rxjava.RxLifecycleUtil
import cn.chenchl.mvvm.repository.BaseRepository
import org.jetbrains.anko.toast

/**
 * created by ccl on 2020/1/12
 **/
class WeatherRepository(
    dao: WeatherDao,
    net: WeatherNetwork
) : BaseRepository<WeatherDao, WeatherNetwork>(dao, net) {

    @SuppressLint("CheckResult")
    fun getWeather(
        city: String,
        isRefresh: Boolean,
        lifecycle: Lifecycle
    ): MutableLiveData<CityWeather> {
        val weatherData: MutableLiveData<CityWeather> = MutableLiveData()
        val weatherJson = dao.queryTodayWeather()
        if (TextUtils.isEmpty(weatherJson) || isRefresh) {
            //方案1 使用autoDispose自动管理
            network.getWeather(city)
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
                })
            //方案2 加入compositeDisposable中 在Repository触发onCleared时取消
            /*addSubscriber(network.getWeather(city)
                .compose(RxJavaTransformers.getDefaultScheduler())
                .`as`(RxLifecycleUtil.bindLifeCycle(lifecycleOwner, Lifecycle.Event.ON_DESTROY))
                .subscribeWith(object : DefaultResponseSubscriber<CityWeatherModel, CityWeather>() {
                    override fun onSuccess(data: CityWeather?) {
                        Utils.getApp().toast(data.toString())
                    }

                    override fun onFail(error: NetError) {
                        Utils.getApp().toast(error.message!!)
                    }
                })
            )*/
        } else {
            weatherData.value = fromJson(weatherJson, CityWeather::class.java)
        }
        return weatherData
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