package com.lee.toollibrary

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_tab.*

class TabActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_tab
    }

    override fun init() {

        val titles = arrayOf("tab1", "tab2")
        nl_tab.setNavWidth(this, 20);
//        nl_tab.setMargin(80);//如果xml设置了margin 左右各40，这边就设置80
        nl_tab.setViewPager(this, titles, vp_page, R.color.test, R.color.test, 16,
                16, 0, 0, true)
        nl_tab.setBgLine(this, 1, R.color.white);
        nl_tab.setNavLine(this, 5, R.color.test, 0)


        val viewList: MutableList<View> = mutableListOf()
        viewList.add( layoutInflater.inflate(R.layout.view_page,null))
        viewList.add( layoutInflater.inflate(R.layout.veiw_pager2,null))
        vp_page.adapter = object : PagerAdapter() {
            override fun getCount(): Int {
                return viewList.size
            }

            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view ==`object`
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                container.addView(viewList[position])
                return viewList[position]
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(viewList[position])
            }
        }
    }

    override fun setListener() {
    }


}
