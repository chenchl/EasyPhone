package cn.chenchl.easyphone.weather.data.model

import androidx.annotation.Keep
import cn.chenchl.easyphone.weather.data.bean.Joke
import cn.chenchl.libs.network.bean.IModel

/**
 * created by ccl on 2020/1/14
 **/
@Keep
class JokeListModel(
    private val result: List<Joke>,
    private val reason: String,
    private val error_code: Int
) : IModel<List<Joke>> {

    override fun isSuccess(): Boolean = error_code == 0

    override fun responseCode(): Int = error_code

    override fun responseData(): List<Joke> = result

    override fun responseMsg(): String = reason

}