package cn.chenchl.mvvm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * created by ccl on 2020/1/17
 * 防止数据倒灌的liveData
 **/
class UnStickyLiveData<T> : MutableLiveData<T>() {

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        //防止第一次注册监听时数据倒灌
        value = null
        super.observe(owner, observer)
    }

    /*private fun hook(observer: Observer<in T>) {
        val liveDataClass = LiveData::class.java
        try { //获取field private SafeIterableMap<Observer<T>, ObserverWrapper> mObservers
            val mObservers = liveDataClass.getDeclaredField("mObservers")
            mObservers.isAccessible = true
            //获取SafeIterableMap集合mObservers
            val observers = mObservers[this]
            val observersClass: Class<*> = observers.javaClass
            //获取SafeIterableMap的get(Object obj)方法
            val methodGet =
                observersClass.getDeclaredMethod("get", Any::class.java)
            methodGet.isAccessible = true
            //获取到observer在集合中对应的ObserverWrapper对象
            val objectWrapperEntry = methodGet.invoke(observers, observer)
            var objectWrapper: Any? = null
            if (objectWrapperEntry is Map.Entry<*, *>) {
                objectWrapper = objectWrapperEntry.value
            }
            if (objectWrapper == null) {
                throw NullPointerException("ObserverWrapper can not be null")
            }
            //获取ObserverWrapper的Class对象  LifecycleBoundObserver extends ObserverWrapper
            val wrapperClass: Class<*>? = objectWrapper.javaClass.superclass
            //获取ObserverWrapper的field mLastVersion
            val mLastVersion =
                wrapperClass!!.getDeclaredField("mLastVersion")
            mLastVersion.isAccessible = true
            //获取liveData的field mVersion
            val mVersion = liveDataClass.getDeclaredField("mVersion")
            mVersion.isAccessible = true
            val mV = mVersion[this]
            //把当前ListData的mVersion赋值给 ObserverWrapper的field mLastVersion
            mLastVersion[objectWrapper] = mV
            mObservers.isAccessible = false
            methodGet.isAccessible = false
            mLastVersion.isAccessible = false
            mVersion.isAccessible = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/
}

/**
 * 拦截回调 防止数据倒灌
 */
class UnStickyObserver<T>(private val onChange: (T) -> Unit) : Observer<T> {
    override fun onChanged(t: T) {
        if (t != null) {
            onChange(t)
        }
    }
}