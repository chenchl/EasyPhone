package cn.chenchl.easyphone.weather.data.net

import cn.chenchl.libs.network.retrofit.RetrofitUtil
import cn.chenchl.mvvm.repository.BaseNet

/**
 * created by ccl on 2020/1/14
 **/
object WeatherNetwork : BaseNet() {
    private const val WEATHER_BASE_URL = "https://apis.juhe.cn/simpleWeather/"

    const val MAIN_BG_URL = "https://pic.tsmp4.net/api/fengjing/img.php"

    const val WEATHER_KEY = "12238789aa92a097fdf84ef15e10d1e4"

    const val JOKE_KEY = "3f27b3ee95c9e665ed6a3a37d7532db3"

    private val WeatherNetService by lazy {
        RetrofitUtil.get(
            WEATHER_BASE_URL,
            WeatherNetService::class.java
        )
    }

    fun getWeather(city: String) = WeatherNetService
        .getWeather(city)

    fun getJokeList() = WeatherNetService
        .getJokeList()


}