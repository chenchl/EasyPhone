package cn.chenchl.easyphone.utils

import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import cn.chenchl.easyphone.R
import cn.chenchl.easyphone.databinding.ItemFutureWeatherDetailBinding
import cn.chenchl.easyphone.weather.data.bean.CityWeather

/**
 * created by ccl on 2020/1/15
 **/
@BindingAdapter("showFuture")
fun LinearLayout.showFuture(futures: List<CityWeather.Future>?) {
    removeAllViews()
    futures?.let {
        for (future in futures) {
            val view =
                LayoutInflater.from(context)
                    .inflate(R.layout.item_future_weather_detail, this, false)
            DataBindingUtil.bind<ItemFutureWeatherDetailBinding>(view)?.future = future
            addView(view)
        }
    }
}
