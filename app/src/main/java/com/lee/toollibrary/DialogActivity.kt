package com.lee.toollibrary

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.lee.toollibrary.dialogs.PikerDialogFragment
import kotlinx.android.synthetic.main.activity_dialog.*

class DialogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)
        tv_show.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val pikerDialogFragment = PikerDialogFragment()
                pikerDialogFragment.show(fragmentManager, "哈哈")
            }
        })

    }
}
