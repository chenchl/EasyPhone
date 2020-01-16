package cn.chenchl.libs.phone

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import cn.chenchl.libs.Utils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


object PhoneUtil {
    //联系人提供者的uri
    private val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
    //通话记录uri
    private val callUri = CallLog.Calls.CONTENT_URI
    // 号码
    private const val NUM = ContactsContract.CommonDataKinds.Phone.NUMBER
    // 联系人姓名
    private const val NAME = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME

    //获取所有联系人
    val connectList: List<ConnectPerson>
        get() {
            val connectPersons: MutableList<ConnectPerson> = ArrayList()
            val cr = Utils.getApp().contentResolver
            val cursor = cr.query(
                phoneUri,
                arrayOf(NUM, NAME),
                null,
                null,
                null
            )
            while (cursor!!.moveToNext()) {
                val phoneDto = ConnectPerson(
                    cursor.getString(cursor.getColumnIndex(NAME)),
                    cursor.getString(cursor.getColumnIndex(NUM))
                )
                connectPersons.add(phoneDto)
            }
            cursor.close()
            return connectPersons
        }

    //获取通话记录
    val contentCallLogList: List<ContentCallLog>
        @SuppressLint("MissingPermission", "SimpleDateFormat")
        get() {
            val contentCallLogs: MutableList<ContentCallLog> = ArrayList()
            val columns = arrayOf(
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION,
                CallLog.Calls.TYPE
            )
            var cursor: Cursor? = null
            try {
                val contentResolver = Utils.getApp().contentResolver
                cursor = contentResolver.query(
                    callUri, // 查询通话记录的URI
                    columns
                    , null, null, CallLog.Calls.DEFAULT_SORT_ORDER// 按照时间逆序排列，最近打的最先显示
                )
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val simpleDateFormat1 = SimpleDateFormat("HH:mm:ss")
                while (cursor!!.moveToNext()) {
                    val name =
                        cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME))  //姓名
                    val number =
                        cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))  //号码
                    val dateLong =
                        cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)) //获取通话日期
                    val date: String = simpleDateFormat.format(Date(dateLong))
                    val duration =
                        cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION))//通话时长
                    val durationString = simpleDateFormat1.format(Date((duration * 1000L)))
                    val type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE))//类型 0来电 1去电
                    val contentCallLog = ContentCallLog(name, number, date, durationString, type)
                    contentCallLogs.add(contentCallLog)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor?.close()
                return contentCallLogs
            }
        }


    @SuppressLint("MissingPermission")
    fun insertPhone(name: String, number: String) {
        try {
            val values = ContentValues()
            values.clear()
            values.put(CallLog.Calls.CACHED_NAME, name)
            values.put(CallLog.Calls.NUMBER, number)
            values.put(CallLog.Calls.TYPE, "1")
            values.put(CallLog.Calls.NEW, "0") // 0已看1未看 ,由于没有获取默认全为已读
            Utils.getApp().contentResolver.insert(CallLog.Calls.CONTENT_URI, values)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun callPhone(phoneNum: String): Boolean {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNum")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return if (ContextCompat.checkSelfPermission(
                Utils.getApp(),
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Utils.getApp().startActivity(intent)
            true
        } else {
            false
        }
    }

}