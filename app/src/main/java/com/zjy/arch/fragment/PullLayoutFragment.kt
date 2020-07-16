package com.zjy.arch.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder
import kotlinx.android.synthetic.main.fragment_pull_layout.*

/**
 * @author zhengjy
 * @since 2020/07/02
 * Description:
 */
class PullLayoutFragment(layout: Int) : Fragment(layout) {

    private val data = listOf(
            "苹果", "橘子", "香蕉", "草莓", "橙子", "榴莲", "荔枝", "樱桃", "西瓜",
            "苹果", "橘子", "香蕉", "草莓", "橙子", "榴莲", "荔枝", "樱桃", "西瓜",
            "苹果", "橘子", "香蕉", "草莓", "橙子", "榴莲", "荔枝", "樱桃", "西瓜",
            "苹果", "橘子", "香蕉", "草莓", "橙子", "榴莲", "荔枝", "樱桃", "西瓜",
            "苹果", "橘子", "香蕉", "草莓", "橙子", "榴莲", "荔枝", "樱桃", "西瓜",
            "苹果", "橘子", "香蕉", "草莓", "橙子", "榴莲", "荔枝", "樱桃", "西瓜"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rv_text.layoutManager = LinearLayoutManager(context)
        rv_text.adapter = object : CommonAdapter<String>(context, android.R.layout.activity_list_item, data) {
            override fun convert(holder: ViewHolder, t: String, position: Int) {
                holder.setText(android.R.id.text1, t)
            }
        }
    }
}