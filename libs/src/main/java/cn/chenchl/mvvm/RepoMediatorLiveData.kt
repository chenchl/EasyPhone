package cn.chenchl.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

/**
 * created by ccl on 2020/1/18
 * 用于repo层直接对view层提供liveData数据传递手段 无需在viewModel中多定义一个MutableLiveData进行二次传递
 *
 * 后续学习发现实际就是Transformations.map变换
 **/
class RepoMediatorLiveData<T>(source: LiveData<T>) : MediatorLiveData<T>() {

    init {
        addSource(source) { this.value = it }
    }
}