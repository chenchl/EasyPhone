package cn.chenchl.libs

import android.os.Build
import android.os.Process
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.multidex.MultiDexApplication
import cn.chenchl.libs.cache.CacheConfig
import cn.chenchl.libs.cache.LocalCache
import cn.chenchl.libs.cache.MMKVCache
import cn.chenchl.libs.log.LogUtil
import cn.chenchl.libs.network.cookie.CookieJarImpl
import cn.chenchl.libs.network.cookie.MemoryCookieStore
import cn.chenchl.libs.network.retrofit.NetError
import cn.chenchl.libs.network.retrofit.NetProvider
import cn.chenchl.libs.network.retrofit.RetrofitUtil
import com.didichuxing.doraemonkit.DoraemonKit
import com.squareup.picasso.Picasso
import okhttp3.CookieJar
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.jetbrains.anko.doBeforeSdk

/**
 * created by ccl on 2020/1/7
 **/
class BaseApp : MultiDexApplication(), ViewModelStoreOwner {

    //实现全局viewModel
    private val mViewModelStore: ViewModelStore by lazy { ViewModelStore() }

    //全局唯一AndroidViewModelFactory
    private val mFactory: ViewModelProvider.Factory by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(
            this
        )
    }

    override fun onCreate() {
        super.onCreate()
        checkAppReplacingState()
        //初始化utils
        Utils.init(this)
        //log初始化
        LogUtil.init(this)
        //本地存储初始化（MMKV）
        MMKVCache.get().config =
            CacheConfig(filename = Config.localCacheFileName)
        LocalCache.init(MMKVCache.get(), this)
        //retrofit初始化
        RetrofitUtil.registerProvider(object : NetProvider {
            override fun configInterceptors(): Array<Interceptor> {
                return arrayOf()

            }

            override fun configLogEnable(): Boolean = true

            override fun configCookie(): CookieJar = CookieJarImpl(MemoryCookieStore())

            override fun configHttps(builder: OkHttpClient.Builder?) {
                builder?.hostnameVerifier { _, _ -> return@hostnameVerifier true }
            }

            override fun configWriteTimeoutMills(): Long = 1500L

            override fun isNeedCache(): Boolean = true

            override fun configConnectTimeoutMills(): Long = 1500L

            override fun configReadTimeoutMills(): Long = 1500L

            override fun handleError(error: NetError?): Boolean = false
        })
        //picasso初始化
        val picasso =
            Picasso.Builder(Utils.getApp())
                .loggingEnabled(true)
                .indicatorsEnabled(true)
                .build()
        Picasso.setSingletonInstance(picasso)
        DoraemonKit.install(this,null,"pId");
    }

    /**
     * Attempt to invoke virtual method 'android.content.res.AssetManager android.content.res.Resources.getAssets()' on a null object reference 问题修复 针对5.1以下机器
     */
    private fun checkAppReplacingState() {
        doBeforeSdk(Build.VERSION_CODES.LOLLIPOP) {
            resources ?: try {
                Process.killProcess(Process.myPid())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getViewModelStore(): ViewModelStore = mViewModelStore

    fun getViewModelProvider(): ViewModelProvider = ViewModelProvider(this, mFactory)
}