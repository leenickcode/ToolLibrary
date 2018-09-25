package com.lee.toollibrary.adapters

import android.content.Context
import com.lee.mylibrary.BaseRecyclerViewAdapter
import com.lee.mylibrary.UniversalViewHolder
import com.lee.toollibrary.R

/**
 * Created by nick on 2018/9/25.
 * @author nick
 */
class DialogAdapter(context: Context,layoutid:Int) :BaseRecyclerViewAdapter<String>(context,layoutid){
    override fun bindData(holder: UniversalViewHolder?, position: Int, bean: String?) {
        holder!!.getTextView(R.id.textView).text = bean
    }

    override fun setItemChildListener(helper: UniversalViewHolder?, viewType: Int) {
        helper!!.setItemChildOnClickListener(R.id.textView)
    }
}