package cn.chenchl.easyphone.weather.data.dao

import android.text.TextUtils
import cn.chenchl.easyphone.weather.data.bean.WeatherKind
import cn.chenchl.libs.cache.LocalCache
import cn.chenchl.libs.file.FileUtils
import cn.chenchl.mvvm.repository.BaseDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * created by ccl on 2020/1/15
 **/
object WeatherDao : BaseDao() {

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

    private val weatherByWidList: ArrayList<WeatherKind> by lazy { loadWeatherKindFromAsset() }

    fun queryTodayWeather(): String? {
        val today = sdf.format(Date())
        return LocalCache[today, ""]
    }

    fun insertTodayWeather(weatherJson: String) {
        val today = sdf.format(Date())
        LocalCache[today] = weatherJson
    }

    private fun loadWeatherKindFromAsset(): ArrayList<WeatherKind> {
        return try {
            val stringFromAssetFile = FileUtils.getStringFromAssetFile("weather_kind")
            val listType = object :
                TypeToken<ArrayList<WeatherKind>>() {}.type
            Gson().fromJson<ArrayList<WeatherKind>>(stringFromAssetFile, listType)
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

    fun queryCurrentCity(): String = LocalCache["currentCity", "绵阳"]!!
}