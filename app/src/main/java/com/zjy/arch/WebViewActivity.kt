package com.zjy.arch

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.qmuiteam.qmui.nestedScroll.QMUIContinuousNestedBottomAreaBehavior
import com.qmuiteam.qmui.nestedScroll.QMUIContinuousNestedBottomRecyclerView
import com.qmuiteam.qmui.nestedScroll.QMUIContinuousNestedTopAreaBehavior
import com.qmuiteam.qmui.nestedScroll.QMUIContinuousNestedTopWebView
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder
import kotlinx.android.synthetic.main.activity_web_view.*


class WebViewActivity : AppCompatActivity() {

    private var mNestedWebView: QMUIContinuousNestedTopWebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        mNestedWebView = QMUIContinuousNestedTopWebView(this)
        val matchParent = ViewGroup.LayoutParams.MATCH_PARENT
        val webViewLp = CoordinatorLayout.LayoutParams(
            matchParent, matchParent
        )
        webViewLp.behavior = QMUIContinuousNestedTopAreaBehavior(this)
        mCoordinatorLayout.setTopAreaView(mNestedWebView, webViewLp)

        val mRecyclerView = QMUIContinuousNestedBottomRecyclerView(this)
        val recyclerViewLp = CoordinatorLayout.LayoutParams(
            matchParent, matchParent
        )
        recyclerViewLp.behavior = QMUIContinuousNestedBottomAreaBehavior()
        mCoordinatorLayout.setBottomAreaView(mRecyclerView, recyclerViewLp)

        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = object : CommonAdapter<String>(this, android.R.layout.activity_list_item, listOf("a", "b", "c", "d", "e")) {
            override fun convert(holder: ViewHolder, t: String, position: Int) {
                holder.setText(android.R.id.text1, t)
            }
        }

        mNestedWebView?.settings?.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            blockNetworkImage = false
        }
        mNestedWebView?.loadUrl(
//            "https://api.xiaoheihe.cn/v3/bbs/app/api/web/share?link_id=42234008"
//            "http://fd.33.cn:1230/#/detail?id=40"
//            "https://www.baidu.com"
        "https://server.chain199.com/#/detail?id=23"
        )
    }

    override fun onDestroy() {
        super.onDestroy();
        if (mNestedWebView != null) {
            mCoordinatorLayout.removeView(mNestedWebView);
            mNestedWebView?.destroy()
            mNestedWebView = null
        }
    }
}