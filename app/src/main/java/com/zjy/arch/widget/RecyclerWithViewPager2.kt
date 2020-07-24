package com.zjy.arch.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * @author zhengjy
 * @since 2020/07/24
 * Description:竖直ViewPager2中嵌套竖直RecyclerView，防止滑动冲突
 */
internal class RecyclerWithViewPager2 : RecyclerView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    private var startY = 0

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startY = ev.y.toInt()
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val endY = ev.y.toInt()
                parent.requestDisallowInterceptTouchEvent(canScrollVertically(startY - endY))
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}