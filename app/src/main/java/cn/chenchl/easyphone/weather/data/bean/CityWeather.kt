package cn.chenchl.easyphone.weather.data.bean

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class CityWeather(
    @SerializedName("city")
    val city: String, // 西宁
    @SerializedName("future")
    val future: List<Future>,
    @SerializedName("realtime")
    val realtime: Realtime
) : Serializable {
    @Keep
    data class Future(
        @SerializedName("date")
        val date: String, // 2020-01-18
        @SerializedName("direct")
        val direct: String, // 南风转东风
        @SerializedName("temperature")
        val temperature: String, // -11/1℃
        @SerializedName("weather")
        val weather: String, // 多云
        @SerializedName("wid")
        val wid: Wid
    ) : Serializable {
        @Keep
        data class Wid(
            @SerializedName("day")
            var day: String, // 01
            @SerializedName("night")
            var night: String // 01
        ) : Serializable
    }

    @Keep
    data class Realtime(
        @SerializedName("aqi")
        val aqi: String, // 204
        @SerializedName("direct")
        val direct: String, // 东北风
        @SerializedName("humidity")
        val humidity: String, // 39
        @SerializedName("info")
        val info: String, // 晴
        @SerializedName("power")
        val power: String, // 2级
        @SerializedName("temperature")
        val temperature: String, // 1
        @SerializedName("wid")
        var wid: String // 00
    ) : Serializable
}