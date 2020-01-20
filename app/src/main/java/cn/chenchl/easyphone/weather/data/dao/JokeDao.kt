package cn.chenchl.easyphone.weather.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cn.chenchl.easyphone.weather.data.bean.JokeInfo

/**
 * created by ccl on 2020/1/20
 **/
@Dao
interface JokeDao {

    @Insert
    fun insertJokeList(vararg jokeInfo: JokeInfo)

    @Query("SELECT * FROM joke_info")
    fun queryAll(): List<JokeInfo>

    @Query("DELETE FROM joke_info")
    fun deleteAll()

}