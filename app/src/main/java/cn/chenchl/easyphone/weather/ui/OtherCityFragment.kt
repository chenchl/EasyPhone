package cn.chenchl.easyphone.weather.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import cn.chenchl.easyphone.BR
import cn.chenchl.easyphone.R
import cn.chenchl.easyphone.databinding.FragmentOtherCityBinding
import cn.chenchl.mvvm.BaseMVVMFragment
import cn.chenchl.mvvm.annotation.MvvMAutoWired

/**
 * created by ccl on 2020/1/23
 **/
@MvvMAutoWired(R.layout.fragment_other_city)
class OtherCityFragment(private val cityName: String) :
    BaseMVVMFragment<FragmentOtherCityBinding, WeatherViewModel>() {

    override fun getDBVariableId(): Int = BR.vm

    override fun initView(savedInstanceState: Bundle?) {
        binding.view = this
    }

    override fun initData(savedInstanceState: Bundle?) {
        viewModel.cityName = cityName
        viewModel.requestWeather(isRefresh = false)
    }

    override fun initViewObservable() {
        viewModel.weatherData.observe(this, Observer {
            viewModel.refreshing.value = false
        })
    }

    fun onCancel() {
        activity?.let {
            val otherCityWeatherActivity = activity as OtherCityWeatherActivity
            otherCityWeatherActivity.onCancelCity(cityName)
        }
    }
}