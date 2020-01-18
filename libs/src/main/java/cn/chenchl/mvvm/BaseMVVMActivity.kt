package cn.chenchl.mvvm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import cn.chenchl.libs.BaseApp
import cn.chenchl.libs.Utils
import java.lang.reflect.ParameterizedType

/**
 * created by ccl on 2020/1/12
 **/
abstract class BaseMVVMActivity<V : ViewDataBinding, VM : BaseViewModel> :
    AppCompatActivity(), IActivityFragment {

    val binding: V by lazy { DataBindingUtil.setContentView<V>(this, initXml()) }

    val viewModel: VM by lazy { initVM() }

    //全局共享viewModel 可用于消息传递 变量共享
    val globalShareViewModel: GlobalShareViewModel by lazy {
        (Utils.getApp() as BaseApp).getViewModelProvider().get(GlobalShareViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBefore(savedInstanceState)
        initVM2DB()
        initView(savedInstanceState)
        initData(savedInstanceState)
        initViewObservable()
    }

    override fun initBefore(savedInstanceState: Bundle?) {

    }

    override fun initViewObservable() {

    }

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
        lifecycle.removeObserver(viewModel)
    }

    private fun initVM(): VM {
        //获取超类（即BaseMVVMActivity）以及泛型参数类型
        val type = javaClass.genericSuperclass
        return if (type != null && type is ParameterizedType) {
            //获取第二个泛型即VM的泛型具体实现类class<VM>,这里强转能保证不会因为异常因为在继承基类时泛型已经限定了类型
            val vmClass = type.actualTypeArguments[1] as Class<VM>
            ViewModelProviders.of(this).get(vmClass)
        } else {
            //兜底处理如果没有泛型类型 则使用默认的BaseViewModel绑定（简单页面相当于没有viewModel）
            val vmClass = BaseViewModel::class.java
            ViewModelProviders.of(this).get(vmClass) as VM
        }
    }

    /**
     * 初始化viewModel和dataBinding并绑定
     */
    private fun initVM2DB() {
        binding.setVariable(getDBVariableId(), viewModel)
        //使用liveData替换ObservableField
        binding.lifecycleOwner = this
        //让ViewModel拥有View的生命周期感应
        lifecycle.addObserver(viewModel)
    }
}