package cn.chenchl.easyphone.weather.ui

import android.Manifest
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cn.chenchl.easyphone.BR
import cn.chenchl.easyphone.R
import cn.chenchl.easyphone.databinding.ActivityWeatherBinding
import cn.chenchl.easyphone.utils.location.LocationManager
import cn.chenchl.libs.Utils
import cn.chenchl.libs.extensions.checkPermissions
import cn.chenchl.libs.extensions.getStatusBarHeight
import cn.chenchl.libs.extensions.runUiThread
import cn.chenchl.mvvm.BaseMVVMActivity
import kotlinx.android.synthetic.main.activity_weather.*
import org.jetbrains.anko.doFromSdk
import org.jetbrains.anko.toast

class WeatherActivity : BaseMVVMActivity<ActivityWeatherBinding, WeatherViewModel>() {

    private val jokeListAdapter: JokeListAdapter = JokeListAdapter()

    private var titleScrollHeight = 0

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
        val layoutParams = swiperefreshlayout.layoutParams as FrameLayout.LayoutParams
        layoutParams.topMargin = getStatusBarHeight()
        statusbar_top.layoutParams.height = getStatusBarHeight()
        initTitleBar()

    }

    private fun initTitleBar() {
        //修改title高度
        with(ll_title) {
            setPadding(
                paddingLeft,
                paddingTop + getStatusBarHeight(),
                paddingRight,
                paddingBottom
            )
        }
        //title背景色渐变
        titleScrollHeight = resources.displayMetrics.heightPixels / 3
        ll_title.background.alpha = 0
        scrollView.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, _: Int ->
            var alpha = scrollY.toFloat() / titleScrollHeight.toFloat()
            if (alpha > 1) alpha = 1.0f
            ll_title.background.alpha = (alpha * 255).toInt()
            if (alpha > 0.5) {
                viewModel.titleColor.value = Color.BLACK
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            } else {
                viewModel.titleColor.value = Color.WHITE
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        //检查权限
        checkPermissions(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
        ) {
            LocationManager.getCurrentLocationCity({ city, _, _ ->
                if (!TextUtils.equals(viewModel.cityName, city)) {
                    viewModel.cityName = city
                    viewModel.requestWeather()
                }
            }, {
                runUiThread {
                    Utils.getApp().toast(it)
                }
            })
        }
        viewModel.requestWeather(isRefresh = false)
        viewModel.requestJokeList(isRefresh = false)
        //jokeList
        jokeListAdapter.setHasStableIds(true)
        rv_story.layoutManager = LinearLayoutManager(this)
        rv_story.adapter = jokeListAdapter
    }

    override fun initViewObservable() {
        viewModel.refreshing.observe(this, Observer {
            if (it) {
                scrollView.smoothScrollTo(0, 0)
            }
        })
        viewModel.jokeData.observe(this, Observer {
            jokeListAdapter.clear()
            jokeListAdapter.addDataList(it)
        })
    }

}
