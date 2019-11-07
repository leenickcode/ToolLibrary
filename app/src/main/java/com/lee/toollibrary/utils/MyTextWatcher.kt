package com.lee.toollibrary.utils

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText

/**
 * @author nick
 * @date 2019/11/7 0007
 * @Description
 * 限制英文不超过mMaxLength ，中文不超过mMaxLength/2
 */
class MyTextWatcher(private val editText: EditText, private val  mMaxLength:Int) : TextWatcher {

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        var index = 0
        var length = 0
        while (index < s!!.length) {
            val c = s?.get(index++)
            if (c!!.toInt() < 128) {
                length += 1
            } else {
                length += 2
            }
        }
        if (length > mMaxLength) {
            index = 0
            length = 0
            while (index < s.length) {
                val c = s.get(index++)
                if (c.toInt() < 128) {
                    length = length + 1
                } else {
                    length = length + 2
                }
                if (length > mMaxLength) {
                    index -= 1
                    break
                }
            }

            editText.removeTextChangedListener(this)
            editText.setText(s.subSequence(0, index))
            editText.setSelection(index)
            editText.addTextChangedListener(this)
        }
    }

    override fun afterTextChanged(s: Editable) {

    }
}
