package cn.chenchl.libs.network.bean

import java.io.Serializable

/**
 * created by hasee on 2019/11/30
 */
open class BaseModel<T>(var data: T, var msg: String, var code: Int) : IModel<T>,
    Serializable {

    override fun isSuccess(): Boolean = code == 200

    override fun responseCode(): Int = code

    override fun responseMsg(): String = msg

    override fun responseData(): T = data

}