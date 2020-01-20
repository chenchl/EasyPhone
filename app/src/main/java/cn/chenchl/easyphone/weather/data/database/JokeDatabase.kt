package cn.chenchl.easyphone.weather.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import cn.chenchl.easyphone.weather.data.bean.JokeInfo
import cn.chenchl.easyphone.weather.data.dao.JokeDao
import cn.chenchl.libs.Utils
import cn.chenchl.libs.log.LogUtil

/**
 * created by ccl on 2020/1/20
 **/
@Database(entities = [JokeInfo::class], version = 1)
abstract class JokeDatabase : RoomDatabase() {

    abstract fun jokeDao(): JokeDao

    companion object {

        private val TAG = JokeDatabase::class.java.simpleName

        val instance: JokeDatabase by lazy {
            Room.databaseBuilder(Utils.getApp(), JokeDatabase::class.java, "joke-database")
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        LogUtil.i(TAG, "dataBase onCreate")
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        LogUtil.i(TAG, "dataBase onOpen")
                    }
                }).build()
        }
    }
}