package com.lee.toollibrary

import android.support.v4.app.Fragment
import android.view.View
import com.lee.toollibrary.views.BottomItem
import com.lee.toollibrary.views.BottomMenuView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

/**
 * @date 创建时间: 2018/8/13
 * @author nick.li
 */


class MainActivity : BaseActivity() {
    override fun business() {
    }

    /**
     * 当前下标
     */
    private var currentIndex = 0
    private val fragments: MutableList<Fragment> = mutableListOf()

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun init() {
        fragments.add(ViewFragment())
        fragments.add(ConvertFragment())
        supportFragmentManager.beginTransaction().add(frameLayout.id,fragments[0]).commit()
    }

    override fun setListener() {
        bottomMenuView.setBottomItem(initBottomItem())
        bottomMenuView.bottomItemOnClickListener =object  : BottomMenuView.BottomItemOnClickListener{
            override fun bottomItemOnClick(view: View, i: Int, item: BottomItem) {
                        switchFragment(i)
                print(i)
            }
        }
        bottomMenuView.setShowIndex(currentIndex)
    }



    private fun initBottomItem(): List<BottomItem> {
        val items = ArrayList<BottomItem>()
        items.add(BottomItem("Views", R.drawable.bottom_view_def, R.drawable.bottom_view_pre))
        items.add(BottomItem("转换", R.drawable.bottom_convert_def, R.drawable.bottom_convert_pre))
        print("aaaa")
        return items
    }

    /**
     * 切换fragment
     *
     * @param newIndex 新的
     */
    private fun switchFragment(newIndex: Int) {
        if (newIndex == currentIndex) {
            return
        }
        val transaction = supportFragmentManager.beginTransaction()
        if (fragments[newIndex].isAdded) {
            transaction.hide(fragments[currentIndex]).show(fragments[newIndex]).commit()
        } else {
            transaction.hide(fragments[currentIndex]).add(frameLayout.id, fragments[newIndex]).show(fragments[newIndex]).commit()
        }
        currentIndex = newIndex
    }
}
