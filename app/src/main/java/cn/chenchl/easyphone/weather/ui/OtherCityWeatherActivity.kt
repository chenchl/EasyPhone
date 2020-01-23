package cn.chenchl.easyphone.weather.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.chenchl.easyphone.BR
import cn.chenchl.easyphone.R
import cn.chenchl.easyphone.databinding.ActivityOtherCityWeatherBinding
import cn.chenchl.libs.extensions.getStatusBarHeight
import cn.chenchl.libs.widget.DepthPageTransformer
import cn.chenchl.libs.widget.ZoomOutPageTransformer
import cn.chenchl.mvvm.BaseMVVMActivity
import com.zaaach.citypicker.CityPicker
import com.zaaach.citypicker.adapter.OnPickListener
import com.zaaach.citypicker.model.City
import com.zaaach.citypicker.model.LocatedCity
import kotlinx.android.synthetic.main.activity_other_city_weather.*
import kotlinx.android.synthetic.main.activity_weather.ll_title
import org.jetbrains.anko.doFromSdk
import org.jetbrains.anko.toast

class OtherCityWeatherActivity :
    BaseMVVMActivity<ActivityOtherCityWeatherBinding, WeatherViewModel>() {

    val cityList: ArrayList<String> = ArrayList()

    private val cityFragmentAdapter = MyFragmentStateAdapter(this, cityList)

    override fun initXml(): Int = R.layout.activity_other_city_weather

    override fun getDBVariableId(): Int = BR.vm

    override fun initBefore(savedInstanceState: Bundle?) {
        //沉浸式
        doFromSdk(Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.view = this
        //修改title高度
        with(ll_title) {
            setPadding(
                paddingLeft,
                paddingTop + getStatusBarHeight(),
                paddingRight,
                paddingBottom
            )
        }
        //初始化viewpager
        viewpager2.setPageTransformer(ZoomOutPageTransformer())
        viewpager2.setPageTransformer(DepthPageTransformer())
        viewpager2.adapter = cityFragmentAdapter
    }

    private val cityPicker: CityPicker by lazy(LazyThreadSafetyMode.NONE) {
        CityPicker.from(this) //activity或者fragment
            .enableAnimation(true)    //启用动画效果，默认无
            .setLocatedCity(LocatedCity("绵阳", "四川", "123213"))
            .setOnPickListener(object : OnPickListener {
                override fun onPick(position: Int, data: City?) {
                    if (data?.name !in cityList) {
                        if (cityList.size >= 8) {
                            toast("同时观察的其他城市数不能大于8个")
                            return
                        }
                        cityList.add(data?.name ?: "绵阳")
                        cityFragmentAdapter.notifyItemInserted(cityFragmentAdapter.itemCount - 1)
                        //滚动到最后一个
                        viewpager2.currentItem = cityFragmentAdapter.itemCount - 1
                    } else {//已存在切过去
                        viewpager2.currentItem = cityList.indexOf(data?.name)
                    }
                }

                override fun onCancel() {

                }

                override fun onLocate() {

                }
            })
    }

    fun onAddCity() {
        cityPicker.show()
    }

    private class MyFragmentStateAdapter(
        activity: OtherCityWeatherActivity,
        private val cityList: ArrayList<String>
    ) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = cityList.size

        override fun createFragment(position: Int): Fragment = OtherCityFragment(cityList[position])

    }
}
