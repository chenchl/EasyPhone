package cn.chenchl.easyphone.weather

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cn.chenchl.easyphone.R
import cn.chenchl.easyphone.weather.data.WeatherRepository
import cn.chenchl.easyphone.weather.data.bean.CityWeather
import cn.chenchl.easyphone.weather.data.dao.WeatherDao
import cn.chenchl.easyphone.weather.data.net.WeatherNetwork
import cn.chenchl.libs.Utils
import cn.chenchl.libs.cache.LocalCache
import cn.chenchl.libs.rxjava.RxLifecycleUtil
import cn.chenchl.mvvm.BaseViewModel
import com.zaaach.citypicker.CityPicker
import com.zaaach.citypicker.adapter.OnPickListener
import com.zaaach.citypicker.model.City
import com.zaaach.citypicker.model.HotCity
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

    val weatherData: MutableLiveData<CityWeather> = MutableLiveData()

    val refreshing: MutableLiveData<Boolean> = MutableLiveData()

    var cityName: String = LocalCache["currentCity", "绵阳"]!!

    fun requestWeather(city: String = cityName, isRefresh: Boolean = true) {
        refreshing.value = true
        repository.getWeather(city, isRefresh, getLifecycle()!!)
            .observe(getLifecycleOwner().get()!!,
                Observer {
                    weatherData.value = it
                    refreshing.value = false
                })
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        //启动背景定时刷新
        refreshBgOnTimer()
    }

    private fun refreshBgOnTimer() {
        Flowable.interval(5, 5, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .`as`(RxLifecycleUtil.bindLifeCycle(getLifecycleOwner().get()))//绑定到onPause停止
            .subscribe { bgUrl.value = bgUrl.value }
    }

    fun onRefresh() {
        requestWeather()
    }

    fun onChangeCity() {
        if (Utils.getTopActivityOrApp() is FragmentActivity) {
            val hotCities: MutableList<HotCity> = ArrayList()
            hotCities.add(HotCity("北京", "北京", "101010100")) //code为城市代码
            hotCities.add(HotCity("西宁", "青海", "101010232")) //code为城市代码
            hotCities.add(HotCity("上海", "上海", "101020100"))
            hotCities.add(HotCity("广州", "广东", "101280101"))
            hotCities.add(HotCity("深圳", "广东", "101280601"))
            hotCities.add(HotCity("杭州", "浙江", "101210101"))
            CityPicker.from(Utils.getTopActivityOrApp() as FragmentActivity) //activity或者fragment
                .enableAnimation(true)    //启用动画效果，默认无
                .setLocatedCity(LocatedCity("绵阳", "四川", "101212059"))  //APP自身已定位的城市，传null会自动定位（默认）
                .setHotCities(hotCities)    //指定热门城市
                .setOnPickListener(object : OnPickListener {
                    override fun onPick(position: Int, data: City?) {
                        cityName = data?.name ?: "绵阳"
                        requestWeather()
                    }

                    override fun onCancel() {

                    }

                    override fun onLocate() {

                    }
                })
                .show();
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.onCleared()
    }
}