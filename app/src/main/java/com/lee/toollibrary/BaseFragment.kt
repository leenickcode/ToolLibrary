package com.lee.toollibrary

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lee.toollibrary.R

/**
 * Created by nick on 2018/8/14.
 * @author nick
 */
abstract class BaseFragment : Fragment() {
    private var mContext: Context? = null
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext=context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return initXml(inflater,container,savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view,savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        businessLogic(savedInstanceState)
        initListener()
    }

    /**
     * 初始化布局;
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    protected abstract fun initXml(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View

    /**
     * 初始化控件;
     * @param view
     * @param savedInstanceState
     */
    protected abstract fun init(view: View, savedInstanceState: Bundle?)

    /**
     * 业务逻辑
     */
    abstract fun  businessLogic( savedInstanceState :Bundle?)

    /**
     * 初始化事件;
     */
    protected abstract fun initListener()

}