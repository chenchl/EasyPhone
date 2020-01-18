package cn.chenchl.mvvm.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import cn.chenchl.libs.Utils
import java.util.*

/**
 * created by ccl on 2019/12/20
 */
abstract class BaseDataBindingAdapter<D, B : ViewDataBinding?> :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    protected var mList: MutableList<D> = ArrayList()
    private var listener: OnItemClickListener<D>? = null
    private var longListener: OnItemLongClickListener<D>? = null
    val list: List<D> = mList

    fun addDataList(list: List<D>) {
        val start = mList.size
        mList.addAll(list)
        notifyItemRangeInserted(start, mList.size)
    }

    fun addData(data: D) {
        val start = mList.size
        mList.add(data)
        notifyItemRangeInserted(start, mList.size)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    /**
     * 清除某一数据 并刷新
     */
    fun removeData(position: Int) {
        if (mList.isNotEmpty()) {
            mList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    /**
     * 清除所有数据
     */
    fun clear() {
        mList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: B = DataBindingUtil.inflate<B>(
            LayoutInflater.from(Utils.getApp()),
            getItemLayout(viewType),
            parent,
            false
        )
        return BaseDataBindingViewHolder(binding!!.root)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding: B? = DataBindingUtil.getBinding<B>(holder.itemView)
        onBindItem(binding, mList[position], holder)
        binding?.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    /**
     * 设置点击监听
     *
     * @param listener
     */
    fun setOnItemClickListener(listener: OnItemClickListener<D>?) {
        this.listener = listener
    }

    /**
     * 设置长按点击监听
     *
     * @param listener
     */
    fun setOnItemLongClickListener(listener: OnItemLongClickListener<D>?) {
        longListener = listener
    }

    /**
     * 点击监听
     */
    interface OnItemClickListener<D> {
        fun onItemClick(v: View?, position: Int, data: D)
    }

    /**
     * 长按点击监听
     */
    interface OnItemLongClickListener<D> {
        fun onItemLongClick(v: View?, position: Int, data: D)
    }

    @LayoutRes
    abstract fun getItemLayout(vieType: Int): Int

    protected abstract fun onBindItem(binding: B?, item: D, holder: RecyclerView.ViewHolder?)

    inner class BaseDataBindingViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

        override fun onClick(v: View) {
            listener?.onItemClick(v, layoutPosition, mList[layoutPosition])
        }

        override fun onLongClick(v: View): Boolean {
            return if (longListener == null) {
                false
            } else {
                //getLayoutPosition获取最新变化后的position
                longListener!!.onItemLongClick(v, layoutPosition, mList[layoutPosition])
                true
            }
        }

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }
    }
}