package cn.chenchl.easyphone.weather.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import cn.chenchl.easyphone.BR
import cn.chenchl.easyphone.R
import cn.chenchl.easyphone.databinding.ActivityWeatherBinding
import cn.chenchl.easyphone.weather.WeatherViewModel
import cn.chenchl.libs.extensions.getStatusBarHeight
import cn.chenchl.mvvm.BaseMVVMActivity
import kotlinx.android.synthetic.main.activity_weather.*
import org.jetbrains.anko.doFromSdk

class WeatherActivity : BaseMVVMActivity<ActivityWeatherBinding, WeatherViewModel>() {

    override fun initXml(): Int = R.layout.activity_weather

    override fun getDBVariableId(): Int = BR.vm

    override fun initBefore(savedInstanceState: Bundle?) {
        //沉浸式
        doFromSdk(Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        //设置topMargin防止被状态栏挡住
        val layoutParams = binding.swiperefreshlayout.layoutParams as FrameLayout.LayoutParams
        layoutParams.topMargin = getStatusBarHeight()
        statusbar_top.layoutParams.height = getStatusBarHeight()
    }

    override fun initData(savedInstanceState: Bundle?) {
        viewModel.requestWeather(isRefresh = false)
    }

}
