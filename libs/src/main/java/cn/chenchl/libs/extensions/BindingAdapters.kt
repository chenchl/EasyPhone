package cn.chenchl.libs.extensions

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.chenchl.libs.FastClickUtils
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso

@BindingAdapter("loadPicasso", "placeholder", requireAll = false)
fun ImageView.loadPicasso(url: String?, resId: Int) {
    if (url != null) {
        if (resId == 0) {
            Picasso.get()
                .load(url)
                .fit()
                .centerCrop()
                .noPlaceholder()
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(this)
        } else {
            Picasso.get()
                .load(url)
                .fit()
                .centerCrop()
                .placeholder(resId)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(this)
        }

    }
}

@BindingAdapter("colorSchemeResources")
fun SwipeRefreshLayout.colorSchemeResources(resId: Int) {
    setColorSchemeResources(resId)
}

//kapt暂时有bug 不支持kotlin 高阶函数这种写法
/*@BindingAdapter("NoFastOnClick")
inline fun View.noFastOnClick(crossinline onClick: () -> Unit) {
    setOnClickListener {
        if (!FastClickUtils.isFastClick)
            onClick()
    }
}*/

@BindingAdapter("NoFastOnClick")
fun View.noFastOnClick(onClick: View.OnClickListener) {
    setOnClickListener {
        if (!FastClickUtils.isFastClick)
            onClick.onClick(it)
    }
}
