package cn.chenchl.easyphone.weather.data.bean

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Keep
data class WeatherKind(
    @SerializedName("weather")
    val weather: String, // 霾
    @SerializedName("wid")
    val wid: String // 53
) : Serializable