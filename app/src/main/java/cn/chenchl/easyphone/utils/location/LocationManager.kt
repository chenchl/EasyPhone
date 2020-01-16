package cn.chenchl.easyphone.utils.location

import cn.chenchl.libs.Utils
import cn.chenchl.libs.log.LogUtil
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener

/**
 * created by ccl on 2020/1/16
 **/
object LocationManager {

    private val aMapLocationClient: AMapLocationClient = AMapLocationClient(Utils.getApp())

    init {
        val aMapLocationClientOption = AMapLocationClientOption()
        aMapLocationClientOption.locationMode =
            AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        aMapLocationClientOption.isOnceLocation = true
        //aMapLocationClientOption.isOnceLocationLatest = true
        aMapLocationClient.setLocationOption(aMapLocationClientOption)
    }

    private var listener: AMapLocationListener? = null

    fun getCurrentLocationCity(
        response: (city: String, province: String, cityCode: String) -> Unit,
        error: (errorInfo: String) -> Unit = {}
    ) {
        //清除原有回调防止重复触发多次
        if (listener != null)
            aMapLocationClient.unRegisterLocationListener(listener)
        listener = AMapLocationListener {
            LogUtil.i(javaClass.simpleName, it.toString())
            if (it != null && it.errorCode == 0) {
                response(it.city.replace("市", ""), it.province, it.cityCode)
            } else {
                error(it.errorInfo)
            }
            aMapLocationClient.stopLocation()
        }
        aMapLocationClient.stopLocation()
        aMapLocationClient.setLocationListener(listener)
        aMapLocationClient.startLocation()
    }
}