package cn.chenchl.easyphone.weather.data.net

import cn.chenchl.libs.network.retrofit.RetrofitUtil
import cn.chenchl.mvvm.repository.BaseNet

/**
 * created by ccl on 2020/1/14
 **/
object WeatherNetwork : BaseNet() {
    private const val MAIN_BASE_URL = "https://apis.juhe.cn/simpleWeather/"

    const val MAIN_BG_URL = "https://pic.tsmp4.net/api/fengjing/img.php"

    const val APP_KEY = "12238789aa92a097fdf84ef15e10d1e4"

    private val mainNetService by lazy {
        RetrofitUtil.get(
            MAIN_BASE_URL,
            WeatherNetService::class.java
        )
    }

    fun getWeather(city: String) = mainNetService
        .getWeather(city)


}