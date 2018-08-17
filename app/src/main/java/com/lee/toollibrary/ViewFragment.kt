package com.lee.toollibrary

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lee.mylibrary.ItemClickListener
import com.lee.toollibrary.adapters.ViewAdapter
import kotlinx.android.synthetic.main.faragment_view.*

/**
 * Created by nick on 2018/8/13.
 * @author nick
 */
class ViewFragment : BaseFragment() {

    val viewList: MutableList<String> = mutableListOf()

    var mAdapter: ViewAdapter? = null
    override fun initXml(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.faragment_view, null)

    }

    override fun init(view: View, savedInstanceState: Bundle?) {
        mAdapter = ViewAdapter(activity, R.layout.item_views)
        recyclerView.layoutManager = GridLayoutManager(activity, 2)
        recyclerView.adapter = mAdapter
        initData()
    }

    private fun initData() {
        viewList.add("密码输入框")
        viewList.add("流式布局")
        viewList.add("Dialog")
        viewList.add("wheelView")
        viewList.add("圆形imageView")
        viewList.add("九宫格imageView")
        viewList.add("进度条imageView")
        viewList.add("自定义短线Tab")
    }

    override fun businessLogic(savedInstanceState: Bundle?) {
        mAdapter?.data = viewList

    }

    override fun initListener() {
        mAdapter?.setItemClickListener(object : ItemClickListener {
            override fun onClick(position: Int, view: View?, data: Any?) {

                when (position) {
                    7 -> {
                        val intent = Intent(activity, TabActivity::class.java)
                        intent.putExtra("type", position)
                        startActivity(intent)
                    }

                    else -> {
                        val intent = Intent(activity, ViewActivity::class.java)
                        intent.putExtra("type", position)
                        startActivity(intent)
                    }

                }

            }

        })
    }

}