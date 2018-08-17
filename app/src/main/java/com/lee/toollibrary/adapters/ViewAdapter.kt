package com.lee.toollibrary.adapters

import android.content.Context
import android.view.ViewGroup
import com.lee.mylibrary.BaseRecyclerViewAdapter
import com.lee.mylibrary.UniversalViewHolder
import com.lee.toollibrary.R
import kotlinx.android.synthetic.main.item_views.view.*

/**
 * Created by nick on 2018/8/15.
 * 自定义view列表适配器
 * @author nick
 */
class ViewAdapter(mContext: Context?, mDefaultItemLayoutId: Int) : BaseRecyclerViewAdapter<String>(mContext, mDefaultItemLayoutId) {
    override fun bindData(holder: UniversalViewHolder?, position: Int, bean: String?) {
        print("哈哈")
        holder!!.getTextView(R.id.textView).text = bean
    }

    override fun setItemChildListener(helper: UniversalViewHolder?, viewType: Int) {
        helper?.setItemChildOnClickListener(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UniversalViewHolder {
        print("恩航")
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun setData(data: MutableList<String>?) {
        super.setData(data)

    }
}
