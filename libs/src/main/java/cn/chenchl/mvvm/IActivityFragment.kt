package cn.chenchl.mvvm

import android.os.Bundle
import org.jetbrains.annotations.NotNull

/**
 * created by ccl on 2020/1/13
 **/
interface IActivityFragment {
    /**
     * 设置xml页面前初始化处理
     */
    fun initBefore(savedInstanceState: Bundle?)

    /**
     * 设置xml id
     */
    @NotNull
    fun initXml(): Int

    /**
     * 设置xml中定义的viewModel BR.XX
     */
    @NotNull
    fun getDBVariableId(): Int

    /**
     * 初始化数据
     */
    fun initData(savedInstanceState: Bundle?)

    /**
     * 初始化view
     */
    fun initView(savedInstanceState: Bundle?)

    /**
     * 用于设置view层监听viewModel层 liveData等数据变化
     */
    fun initViewObservable()
}