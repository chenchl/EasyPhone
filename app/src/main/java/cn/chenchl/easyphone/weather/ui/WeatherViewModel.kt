package cn.chenchl.easyphone.weather.ui

import android.graphics.Color
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import cn.chenchl.easyphone.R
import cn.chenchl.easyphone.weather.data.WeatherRepository
import cn.chenchl.easyphone.weather.data.bean.CityWeather
import cn.chenchl.easyphone.weather.data.bean.JokeInfo
import cn.chenchl.easyphone.weather.data.dao.WeatherDao
import cn.chenchl.easyphone.weather.data.net.WeatherNetwork
import cn.chenchl.libs.cache.LocalCache
import cn.chenchl.libs.rxjava.RxLifecycleUtil
import cn.chenchl.mvvm.BaseViewModel
import cn.chenchl.mvvm.RepoMediatorLiveData
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit


/**
 * created by ccl on 2020/1/12
 **/
class WeatherViewModel : BaseViewModel() {

    private val repository by lazy { WeatherRepository(WeatherDao, WeatherNetwork) }

    val bgUrl: MutableLiveData<String> = MutableLiveData(WeatherNetwork.MAIN_BG_URL)//随机加载背景图片

    val srlColor: MutableLiveData<Int> = MutableLiveData(R.color.colorAccent)

    val weatherData = RepoMediatorLiveData<CityWeather>(repository.weatherData)
    //map写法：val weatherData = Transformations.map(repository.weatherData) { it }  效果是一样的

    val jokeData = RepoMediatorLiveData<List<JokeInfo>>(repository.jokeList)

    val refreshing: MutableLiveData<Boolean> = MutableLiveData()

    val titleColor: MutableLiveData<Int> = MutableLiveData(Color.WHITE)

    var cityName: String = LocalCache["currentCity", "绵阳"]!!

    fun requestWeather(city: String = cityName, isRefresh: Boolean = true) {
        refreshing.value = true
        repository.getWeather(city, isRefresh)
    }

    fun requestJokeList(isRefresh: Boolean = true) {
        repository.getJokeList(isRefresh)
    }

    fun recordCurrentCity() {
        repository.insertCurrentCity(cityName)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        //启动背景定时刷新
        refreshBgOnTimer(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        repository.disposeAll()
    }

    private fun refreshBgOnTimer(owner: LifecycleOwner) {
        Flowable.interval(5, 5, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .`as`(RxLifecycleUtil.bindLifeCycle(owner))//绑定到onPause停止
            .subscribe { bgUrl.value = bgUrl.value }
    }

    fun onRefresh(isNeedJoke: Boolean = true) {
        requestWeather()
        if (isNeedJoke)
            requestJokeList()
    }

    override fun onCleared() {
        super.onCleared()
        repository.onCleared()
    }
}