package cn.chenchl.easyphone.weather.data.net

import cn.chenchl.easyphone.weather.data.model.CityWeatherModel
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * created by ccl on 2020/1/14
 **/
interface WeatherNetService {

    @GET("query")
    fun getWeather(@Query("city") city: String, @Query("key") appKey: String = WeatherNetwork.APP_KEY): Flowable<CityWeatherModel>
}