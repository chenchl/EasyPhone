package cn.chenchl.easyphone.weather.data.bean


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CityKind(
    @SerializedName("city") val city: String, // 迪庆
    @SerializedName("district") val district: String, // 迪庆
    @SerializedName("id") val id: String, // 2587
    @SerializedName("province") val province: String // 云南
)