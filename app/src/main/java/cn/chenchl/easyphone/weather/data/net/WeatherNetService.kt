package cn.chenchl.easyphone.weather.data.net

import cn.chenchl.easyphone.weather.data.model.CityWeatherModel
import cn.chenchl.easyphone.weather.data.model.JokeListModel
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * created by ccl on 2020/1/14
 **/
interface WeatherNetService {

    @GET("query")
    fun getWeather(@Query("city") city: String, @Query("key") appKey: String = WeatherNetwork.WEATHER_KEY): Flowable<CityWeatherModel>

    @GET
    fun getJokeList(@Url url: String = "http://v.juhe.cn/joke/randJoke.php?key=" + WeatherNetwork.JOKE_KEY): Flowable<JokeListModel>
}