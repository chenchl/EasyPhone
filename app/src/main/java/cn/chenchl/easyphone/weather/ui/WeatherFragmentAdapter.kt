package cn.chenchl.easyphone.weather.ui

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class WeatherFragmentAdapter(
    activity: OtherCityWeatherActivity,
    private val cityList: ArrayList<String>
) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = cityList.size

    override fun createFragment(position: Int): Fragment = OtherCityFragment(cityList[position])

}