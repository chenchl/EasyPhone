package cn.chenchl.libs.network.retrofit

import androidx.lifecycle.viewModelScope
import cn.chenchl.libs.network.bean.IModel
import cn.chenchl.mvvm.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * created by ccl on 2020/2/29
 **/
//协程请求1:不处理异常code全部抛给上层自行处理
inline fun BaseViewModel.request(
    crossinline complete: suspend CoroutineScope.() -> Unit = {},//请求结束
    crossinline error: suspend CoroutineScope.(Throwable) -> Unit = {},//异常处理
    crossinline block: suspend CoroutineScope.() -> Unit//请求体
) = viewModelScope.launch {
    try {
        block()
    } catch (e: Throwable) {
        error(e)
    } finally {
        complete()
    }
}

//协程请求2:处理异常code后只将code码==200的数据抛给上层自行处理
inline fun <T> BaseViewModel.requestOnlySuccess(
    crossinline block: suspend CoroutineScope.() -> IModel<T>,
    crossinline success: (T) -> Unit,
    crossinline error: (NetError) -> Unit = { },
    crossinline complete: () -> Unit = {}
) {
    viewModelScope.launch {
        handleException(
            { block() },
            { response ->
                handleResponse(response) {
                    success(
                        it
                    )
                }
            },
            {
                error(it)
            },
            {
                complete()
            }
        )
    }
}

/**
 * 异常结果过滤
 */
suspend inline fun <T> handleException(
    crossinline block: suspend CoroutineScope.() -> IModel<T>,
    crossinline success: suspend CoroutineScope.(IModel<T>) -> Unit,
    crossinline error: (NetError) -> Unit,
    crossinline complete: () -> Unit
) {
    coroutineScope {
        try {
            success(block())
        } catch (e: Throwable) {
            error(NetError.handleException(e))
        } finally {
            complete()
        }
    }
}

/**
 * 请求结果过滤
 */
inline fun <T> handleResponse(
    response: IModel<T>,
    crossinline success: (T) -> Unit
) {
    if (response.isSuccess())
        success(response.responseData())
    else
        throw NetError(response.responseMsg(), response.responseCode())
}

