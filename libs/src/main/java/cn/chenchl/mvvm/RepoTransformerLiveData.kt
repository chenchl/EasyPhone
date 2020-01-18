package cn.chenchl.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

/**
 * created by ccl on 2020/1/18
 * 用于repo层直接对view层提供liveData数据传递手段 无需在viewModel中多定义一个MutableLiveData进行二次传递
 *  update:
 *  根据追加transformer函数 类似提供数据map转换功能，默认实现直接强转
 **/
@Suppress("UNCHECKED_CAST")
class RepoTransformerLiveData<R, T>(
    source: LiveData<T>,
    transformer: (T) -> R = { it as R }
) :
    MediatorLiveData<R>() {

    init {
        addSource(source) { this.value = transformer(it) }
    }
}