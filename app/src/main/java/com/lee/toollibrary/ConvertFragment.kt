package com.lee.toollibrary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by nick on 2018/8/14.
 * @author nick
 */
class ConvertFragment : BaseFragment() {
    override fun initXml(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
      return  inflater.inflate(R.layout.fragment_convert,null)
    }

    override fun init(view: View, savedInstanceState: Bundle?) {
    }

    override fun businessLogic(savedInstanceState: Bundle?) {
    }
    override fun initListener() {
    }

}