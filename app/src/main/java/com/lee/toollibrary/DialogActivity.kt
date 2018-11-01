package com.lee.toollibrary

import android.support.v7.widget.LinearLayoutManager
import com.lee.toollibrary.adapters.DialogAdapter
import com.lee.toollibrary.dialogs.IosDefaultDialog
import com.lee.toollibrary.dialogs.PikerDialogFragment
import kotlinx.android.synthetic.main.activity_dialog.*

class DialogActivity : BaseActivity() {
    override fun business() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val mAdapter = DialogAdapter(this, R.layout.item_views)
    var mData = mutableListOf<String>()
    override fun getLayoutId(): Int {
        return R.layout.activity_dialog
    }

    override fun init(): Unit {

        rv_dialogs.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_dialogs.adapter = mAdapter
        mData.add("PickDialogFragment")
        mData.add("通用")
        mAdapter.data = mData
    }

    override fun setListener() {
        mAdapter.setItemClickListener { position, view, data ->
            when (position) {
                0 -> {
                    val pikerDialogFragment = PikerDialogFragment()
                    pikerDialogFragment.show(fragmentManager, "哈哈")
                }
                1 -> {
                    var  dialog = IosDefaultDialog(this)
                    dialog.setTitle("IOS风格对话框")
                    dialog.show()
                }
            }
        }
    }

}
