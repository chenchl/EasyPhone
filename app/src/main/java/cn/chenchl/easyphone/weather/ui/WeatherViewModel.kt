package cn.chenchl.easyphone.weather.ui

import android.graphics.Color
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import cn.chenchl.easyphone.R
import cn.chenchl.easyphone.utils.location.LocationManager
import cn.chenchl.easyphone.weather.data.WeatherRepository
import cn.chenchl.easyphone.weather.data.bean.CityWeather
import cn.chenchl.easyphone.weather.data.bean.Joke
import cn.chenchl.easyphone.weather.data.dao.WeatherDao
import cn.chenchl.easyphone.weather.data.net.WeatherNetwork
import cn.chenchl.libs.Utils
import cn.chenchl.libs.cache.LocalCache
import cn.chenchl.libs.rxjava.RxLifecycleUtil
import cn.chenchl.mvvm.BaseViewModel
import cn.chenchl.mvvm.RepoMediatorLiveData
import com.zaaach.citypicker.CityPicker
import com.zaaach.citypicker.adapter.OnPickListener
import com.zaaach.citypicker.model.City
import com.zaaach.citypicker.model.HotCity
import com.zaaach.citypicker.model.LocateState
import com.zaaach.citypicker.model.LocatedCity
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

    val jokeData = RepoMediatorLiveData<List<Joke>>(repository.jokeList)

    val refreshing: MutableLiveData<Boolean> = MutableLiveData()

    val titleColor: MutableLiveData<Int> = MutableLiveData(Color.WHITE)

    var cityName: String = LocalCache["currentCity", "绵阳"]!!

    fun requestWeather(city: String = cityName, isRefresh: Boolean = true) {
        refreshing.value = true
        repository.getWeather(city, isRefresh, getLifecycle()!!)
    }

    fun requestJokeList(isRefresh: Boolean = true) {
        repository.getJokeList(isRefresh, getLifecycle()!!)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        //启动背景定时刷新
        refreshBgOnTimer()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        cityPicker = null
    }

    private fun refreshBgOnTimer() {
        Flowable.interval(5, 5, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .`as`(RxLifecycleUtil.bindLifeCycle(getLifecycleOwner().get()))//绑定到onPause停止
            .subscribe { bgUrl.value = bgUrl.value }
    }

    fun onRefresh() {
        requestWeather()
        requestJokeList()
    }

    var cityPicker: CityPicker? = null

    fun onChangeCity() {
        if (Utils.getTopActivityOrApp() is FragmentActivity) {
            val hotCities: MutableList<HotCity> = ArrayList()
            hotCities.add(HotCity("北京", "北京", "101010100")) //code为城市代码
            hotCities.add(HotCity("西宁", "青海", "101010232")) //code为城市代码
            hotCities.add(HotCity("上海", "上海", "101020100"))
            hotCities.add(HotCity("广州", "广东", "101280101"))
            hotCities.add(HotCity("深圳", "广东", "101280601"))
            hotCities.add(HotCity("杭州", "浙江", "101210101"))
            if (cityPicker == null) {
                cityPicker =
                    CityPicker.from(Utils.getTopActivityOrApp() as FragmentActivity) //activity或者fragment
                        .enableAnimation(true)    //启用动画效果，默认无
                        //.setLocatedCity(LocatedCity("绵阳", "四川", "101212059"))  //APP自身已定位的城市，传null会自动定位（默认）
                        .setHotCities(hotCities)    //指定热门城市
                        .setOnPickListener(object : OnPickListener {
                            override fun onPick(position: Int, data: City?) {
                                cityName = data?.name ?: "绵阳"
                                requestWeather()
                            }

                            override fun onCancel() {

                            }

                            override fun onLocate() {
                                LocationManager.getCurrentLocationCity({ city, province, cityCode ->
                                    cityPicker?.locateComplete(
                                        LocatedCity(
                                            city,
                                            province,
                                            cityCode
                                        ), LocateState.SUCCESS
                                    )
                                }, {
                                    cityPicker?.locateComplete(
                                        LocatedCity(
                                            "北京",
                                            "北京",
                                            "101010100"
                                        ), LocateState.FAILURE
                                    )
                                })
                            }
                        })
            }
            cityPicker?.show()

        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.onCleared()
    }
}