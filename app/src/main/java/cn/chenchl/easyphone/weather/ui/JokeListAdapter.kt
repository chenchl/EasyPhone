package cn.chenchl.easyphone.weather.ui

import androidx.recyclerview.widget.RecyclerView
import cn.chenchl.easyphone.R
import cn.chenchl.easyphone.databinding.ItemJokeDetailBinding
import cn.chenchl.easyphone.weather.data.bean.JokeInfo
import cn.chenchl.mvvm.adapter.BaseDataBindingAdapter

/**
 * created by ccl on 2020/1/17
 **/
class JokeListAdapter : BaseDataBindingAdapter<JokeInfo, ItemJokeDetailBinding>() {

    override fun getItemLayout(vieType: Int): Int = R.layout.item_joke_detail

    override fun onBindItem(binding: ItemJokeDetailBinding?, item: JokeInfo, holder: RecyclerView.ViewHolder?) {
        binding?.joke=item
    }
}