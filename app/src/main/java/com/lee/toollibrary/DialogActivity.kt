package com.lee.toollibrary

import android.content.DialogInterface
import android.support.v7.widget.LinearLayoutManager
import com.lee.toollibrary.adapters.DialogAdapter
import com.lee.toollibrary.dialogs.*
import kotlinx.android.synthetic.main.activity_dialog.*

class DialogActivity : BaseActivity() {
    override fun business() {
    }

    val mAdapter = DialogAdapter(this, R.layout.item_views)
    var mData = mutableListOf<String>()
    override fun getLayoutId(): Int {
        return R.layout.activity_dialog
    }

    override fun init(): Unit {

        rv_dialogs.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_dialogs.adapter = mAdapter
        mData.add("单项选择")

        mData.add("通用")
        mData.add("年月日时分选择器")
        mData.add("日期选择器")
        mData.add("加载框")
        mAdapter.data = mData
    }

    override fun setListener() {
        mAdapter.setItemClickListener { position, view, data ->
            var item=data
            when (item) {
                "单项选择" -> {
                   var pickerDialog =PickerDialog(this)
                    pickerDialog.show()
                }
                "通用" -> {
                    var dialog = IosDefaultDialog(this)
                    dialog.setTitle("IOS风格对话框")
                    dialog.show()
                }
                "年月日时分选择器"->{
                    var fiveDatePickerDialog =FiveDatePickerDialog(this)
                    fiveDatePickerDialog.show()
                }
                "日期选择器"->{
                    var datePickerDialog =DatePickerDialog(this)
                    datePickerDialog.show()
                }
                "加载框"->{
                    var  dialog= ProgressLoadingDialog.show(this, getResources().getString(R.string.loading),
                            true, if (true)
                        DialogInterface.OnCancelListener {
                            //                            HttpUtils.cancelPost();

                        }
                    else
                        null);
                    dialog.show()
                }
            }
        }
    }

}
