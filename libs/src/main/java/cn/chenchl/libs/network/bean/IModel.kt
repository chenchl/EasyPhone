package cn.chenchl.libs.network.bean

/**
 * created by hasee on 2019/11/30
 */
interface IModel<T> {

    fun isSuccess(): Boolean

    fun responseCode(): Int

    fun responseMsg(): String

    fun responseData(): T
}