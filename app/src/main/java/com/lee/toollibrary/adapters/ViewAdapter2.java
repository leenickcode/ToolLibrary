package com.lee.toollibrary.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.lee.mylibrary.BaseRecyclerViewAdapter;
import com.lee.mylibrary.UniversalViewHolder;

/**
 * Created by nick on 2018/8/15.
 *
 * @author nick
 */
public class ViewAdapter2 extends BaseRecyclerViewAdapter<String> {
    public ViewAdapter2(Context mContext, int mDefaultItemLayoutId) {
        super(mContext, mDefaultItemLayoutId);
    }

    @Override
    protected void bindData(UniversalViewHolder holder, int position, String bean) {

    }


}
