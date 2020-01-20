package cn.chenchl.easyphone.weather.data.bean

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * created by ccl on 2020/1/20
 **/
@Keep
@Entity(tableName = "joke_info")
data class JokeInfo(
    @PrimaryKey
    @ColumnInfo(name = "hash_id")
    val hashId: String,
    @ColumnInfo(name = "content")
    val content: String?,
    @ColumnInfo(name = "unixtime")
    val unixtime: Long?
) {

}