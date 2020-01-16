package cn.chenchl.easyphone

import android.Manifest
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import cn.chenchl.easyphone.weather.ui.WeatherActivity
import cn.chenchl.libs.cache.LocalCache
import cn.chenchl.libs.cache.LocalCacheDelegates
import cn.chenchl.libs.extensions.checkPermissions
import cn.chenchl.libs.extensions.getStatusBarHeight
import cn.chenchl.libs.log.LogUtil
import cn.chenchl.libs.net.NetWatchDog
import cn.chenchl.libs.phone.PhoneUtil
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    private var x: Int? by LocalCacheDelegates.localCache("5", 6)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAppBar()
        LocalCache.set(key = "1", value = 1)
        LocalCache.set(key = "2", value = "sdad")
        LocalCache.set(key = "3", value = true)
        doAsync {
            LogUtil.e(MainActivity::class.simpleName, Thread.currentThread().name)
            val get = LocalCache.get(key = "111", defValue = 11)
            val get1 = LocalCache.get(key = "1", defValue = 2)
            val get2 = LocalCache.get(key = "23", defValue = " sad")
            val get3 = LocalCache.get(key = "2", defValue = " 1")
            val get4 = LocalCache.get(key = "3", defValue = false)
            val get5 = LocalCache.get(key = "4", defValue = false)
        }
        doAsync {
            val b = "23" in LocalCache
            LogUtil.e("" + b)
            LogUtil.e(LocalCache["2", "1"])
            LocalCache["2"] = 3
            LogUtil.e(LocalCache["2", "3"])
        }

        println("" + x)
        x = 4
        println("" + LocalCache.get(key = "5", defValue = 6))
        /* RxPermissions(this).requestEachCombined(
             Manifest.permission.READ_CONTACTS,
             Manifest.permission.READ_CALL_LOG,
             Manifest.permission.CALL_PHONE,
             Manifest.permission.WRITE_CALL_LOG
         ).subscribeOn(AndroidSchedulers.mainThread())
             .subscribe { permission ->
                 if (permission.granted) {
                     val phone = PhoneUtil.connectList
                     toast(phone.size.toString())
                 } else if (permission.shouldShowRequestPermissionRationale) {
                     toast("345" + permission.name)
                 } else {
                     toast("456" + permission.name)
                 }
             }*/
        val permissions = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_CALL_LOG
        )
        NetWatchDog.getInstance().observe(this, Observer<Int> {
            toast("netState:$it")
        })
        btn_loading.setOnClickListener { startActivity<WeatherActivity>() }
        btn_content.setOnClickListener {
            checkPermissions(*permissions)
            {
                val phone = PhoneUtil.connectList
                toast(phone.size.toString())
                btn_empty.setOnClickListener { toast(PhoneUtil.contentCallLogList.size.toString()) }
                fab.setOnClickListener {
                    PhoneUtil.callPhone("123213123123213123")
                }
            }
        }
    }

    private fun initAppBar() {
        setSupportActionBar(toolbar)
        statusbar_top.layoutParams.height = getStatusBarHeight()
        appbarlayout.layoutParams.height =
            getStatusBarHeight() + resources.getDimensionPixelOffset(R.dimen.x600)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> ActivityCompat.finishAfterTransition(this)
        }
        return true
        //return super.onOptionsItemSelected(item)
    }
}
