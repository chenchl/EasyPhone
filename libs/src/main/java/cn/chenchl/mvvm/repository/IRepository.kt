package cn.chenchl.mvvm.repository

/**
 * created by ccl on 2020/1/13
 **/
interface IRepository {
    /**
     * viewModel销毁时调用用于停止正在进行的数据请求或写入等处理
     */
    fun onCleared()
}