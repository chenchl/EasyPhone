package cn.chenchl.mvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import java.lang.reflect.ParameterizedType


/**
 * created by ccl on 2020/1/13
 **/
abstract class BaseMVVMFragment<V : ViewDataBinding, VM : BaseViewModel> : Fragment(),
    IActivityFragment {

    lateinit var binding: V

    val viewModel: VM by lazy { initVM() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBefore(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, initXml(), container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initVM2DB()
        initData(savedInstanceState)
        initViewObservable()
    }

    override fun initBefore(savedInstanceState: Bundle?) {

    }

    override fun initViewObservable() {

    }

    override fun initData(savedInstanceState: Bundle?) {

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
        //使用autoDispose
        viewModel.injectLifecycleOwner(this)
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


}