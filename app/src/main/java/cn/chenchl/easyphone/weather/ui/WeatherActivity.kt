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
import cn.chenchl.mvvm.UnStickyObserver
import cn.chenchl.statelayoutlibrary.StateLayout
import com.amap.api.location.AMapLocation
import com.zaaach.citypicker.CityPicker
import com.zaaach.citypicker.adapter.OnPickListener
import com.zaaach.citypicker.model.City
import com.zaaach.citypicker.model.LocateState
import com.zaaach.citypicker.model.LocatedCity
import kotlinx.android.synthetic.main.activity_weather.*
import org.jetbrains.anko.doFromSdk
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class WeatherActivity : BaseMVVMActivity<ActivityWeatherBinding, WeatherViewModel>() {

    private val jokeListAdapter: JokeListAdapter = JokeListAdapter()

    private var titleScrollHeight = 0

    private lateinit var stateLayout: StateLayout

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
        //绑定activity到dataBinding
        binding.view = this
        //设置topMargin防止被状态栏挡住
        val layoutParams = swiperefreshlayout.layoutParams as FrameLayout.LayoutParams
        layoutParams.topMargin = getStatusBarHeight()
        statusbar_top.layoutParams.height = getStatusBarHeight()
        initTitleBar()
        stateLayout = StateLayout(this)
            .setOnRetryClickListener {
                viewModel.refreshing.value = true
                viewModel.onRefresh()
            }
            .setAnimDuration(500)
            .setEnableLoadingAlpha(false)
            .with(this)
            .showLoading("数据加载中")
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

    //写法1 类似java的匿名类形式
    /*private val locationObserver1: Observer<AMapLocation> = object : Observer<AMapLocation> {
       override fun onChanged(t: AMapLocation) {
           if (t.errorCode == 0) {
               val city = t.city.replace("市", "")
               if (!TextUtils.equals(viewModel.cityName, city)) {
                   viewModel.cityName = city
                   viewModel.requestWeather()
               }
           } else {
               runUiThread {
                   Utils.getApp().toast(t.errorInfo)
               }
           }
           //只监听一次 防止后续重复收到
           LocationManager.aMapLocationData.removeObserver(this)
       }
   }*/

    //写法2 使用run
    private val locationObserver: UnStickyObserver<AMapLocation> = run {
        UnStickyObserver {
            //只监听一次 防止后续重复收到
            LocationManager.aMapLocationData.removeObserver(locationObserver)
            with(it) {
                if (errorCode == 0) {
                    val city = city.replace("市", "")
                    if (!TextUtils.equals(viewModel.cityName, city)) {
                        viewModel.cityName = city
                        viewModel.recordCurrentCity()
                        viewModel.requestWeather()
                    }
                } else {
                    runUiThread {
                        Utils.getApp().toast(errorInfo)
                    }
                }
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
            LocationManager.getCurrentLocationCity()
        }
        viewModel.requestWeather(isRefresh = false)
        viewModel.requestJokeList(isRefresh = false)
        //jokeList
        jokeListAdapter.setHasStableIds(true)
        rv_story.layoutManager = LinearLayoutManager(this)
        rv_story.adapter = jokeListAdapter
    }

    override fun initViewObservable() {
        LocationManager.aMapLocationData.observe(this, locationObserver)
        viewModel.refreshing.observe(this, Observer {
            if (it) {
                scrollView.smoothScrollTo(0, 0)
            }
        })
        viewModel.jokeData.observe(this, Observer {
            jokeListAdapter.clear()
            jokeListAdapter.addDataList(it)
        })
        viewModel.weatherData.observe(this, Observer {
            if (it == null) {
                stateLayout.showError("数据加载异常请点击重试")
            } else {
                //模拟效果
                stateLayout.postDelayed({
                    stateLayout.showContent()
                },3000)
            }
            //viewModel.refreshing.value = false
        })
    }

    //写法2 使用run
    private val locationObserverF: UnStickyObserver<AMapLocation> = run {
        UnStickyObserver {
            LocationManager.aMapLocationData.removeObserver(locationObserverF)
            with(it) {
                if (errorCode == 0) {
                    cityPicker.locateComplete(
                        LocatedCity(
                            city,
                            province,
                            cityCode
                        ), LocateState.SUCCESS
                    )
                } else {
                    cityPicker.locateComplete(
                        LocatedCity(
                            "北京",
                            "北京",
                            "101010100"
                        ), LocateState.FAILURE
                    )
                }
            }
        }
    }

    private val cityPicker: CityPicker by lazy(LazyThreadSafetyMode.NONE) {
        CityPicker.from(this) //activity或者fragment
            .enableAnimation(true)    //启用动画效果，默认无
            .setOnPickListener(object : OnPickListener {
                override fun onPick(position: Int, data: City?) {
                    viewModel.cityName = data?.name ?: "绵阳"
                    viewModel.recordCurrentCity()
                    viewModel.requestWeather()
                }

                override fun onCancel() {

                }

                override fun onLocate() {
                    LocationManager.aMapLocationData.removeObserver(locationObserverF)
                    LocationManager.aMapLocationData.observe(
                        this@WeatherActivity,
                        locationObserverF
                    )
                    LocationManager.getCurrentLocationCity()
                }
            })
    }

    fun onChangeCity() {
        cityPicker.show()
    }

    fun toOtherCity(view: View) {
        startActivity<OtherCityWeatherActivity>()
    }

}
