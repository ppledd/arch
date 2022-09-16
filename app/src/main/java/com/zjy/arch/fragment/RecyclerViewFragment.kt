//package com.zjy.arch.fragment
//
//import android.graphics.Color
//import android.os.Bundle
//import android.view.View
//import android.widget.TextView
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.chad.library.adapter.base.BaseQuickAdapter
//import com.chad.library.adapter.base.viewholder.BaseViewHolder
//import com.zjy.arch.R
//import com.zjy.architecture.base.BaseFragment
//import kotlinx.android.synthetic.main.fragment_recyclerview.*
//import java.util.*
//
///**
// * @author zhengjy
// * @since 2020/07/24
// * Description:
// */
//class RecyclerViewFragment : BaseFragment() {
//
//    private val data = arrayListOf(
//        "START", "dfasdfas", "wrel;l;", "l;qmelqfwefwe",
//        "lklasdf", "dfasdfas", "wrel;l;", "l;qmelqfwefwe",
//        "lklasdf", "dfasdfas", "wrel;l;", "l;qmelqfwefwe",
//        "lklasdf", "dfasdfas", "wrel;l;", "l;qmelqfwefwe",
//        "lklasdf", "dfasdfas", "wrel;l;", "END"
//    )
//
//    override val layoutId: Int
//        get() = R.layout.fragment_recyclerview
//
//    override fun initView(view: View, savedInstanceState: Bundle?) {
//        rv_data.layoutManager = LinearLayoutManager(requireContext())
//        rv_data.adapter = object : BaseQuickAdapter<String, MyViewHolder>(
//            R.layout.item_my_recyclerview, data
//        ) {
//            override fun convert(holder: MyViewHolder, item: String) {
//                holder.text.text = item
//            }
//        }
//        rv_data.setBackgroundColor(Color.parseColor(getRandomColor()))
//    }
//
//    private fun getRandomColor(): String {
//        val random = Random()
//        var red = Integer.toHexString(random.nextInt(256)).toUpperCase()
//        var green = Integer.toHexString(random.nextInt(256)).toUpperCase()
//        var blue = Integer.toHexString(random.nextInt(256)).toUpperCase()
//
//        red = if (red.length == 1) "0$red" else red
//        green = if (green.length == 1) "0$green" else green
//        blue = if (blue.length == 1) "0$blue" else blue
//        return "#$red$green$blue"
//    }
//
//    override fun initData() {
//
//    }
//
//    override fun setEvent() {
//
//    }
//
//    class MyViewHolder(
//        private val item: View
//    ) : BaseViewHolder(item) {
//        val text: TextView = item.findViewById(R.id.text)
//    }
//}