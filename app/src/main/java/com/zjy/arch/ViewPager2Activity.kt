package com.zjy.arch

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
//import com.zjy.arch.fragment.RecyclerViewFragment
import com.zjy.architecture.base.BaseActivity
import kotlinx.android.synthetic.main.activity_vertical.*

/**
 * @author zhengjy
 * @since 2020/07/24
 * Description:
 */
class ViewPager2Activity : BaseActivity() {

    private val data = arrayListOf(
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9
    )

    override val layoutId: Int
        get() = R.layout.activity_vertical

    override fun initView() {
//        vp_vertical.adapter = object : FragmentStateAdapter(this) {
//            override fun getItemCount(): Int {
//                return data.size
//            }
//
//            override fun createFragment(position: Int): Fragment {
//                return RecyclerViewFragment()
//            }
//        }
//        vp_vertical.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageScrollStateChanged(state: Int) {
//                when (state) {
//                    ViewPager2.SCROLL_STATE_IDLE -> vp_vertical.isUserInputEnabled = true
//                    ViewPager2.SCROLL_STATE_DRAGGING -> vp_vertical.isUserInputEnabled = true
//                    ViewPager2.SCROLL_STATE_SETTLING -> vp_vertical.isUserInputEnabled = false
//                }
//            }
//        })
    }

    override fun initData() {

    }

    override fun setEvent() {

    }
}