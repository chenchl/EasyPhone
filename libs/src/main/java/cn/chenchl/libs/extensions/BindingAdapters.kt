package cn.chenchl.libs.extensions

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
