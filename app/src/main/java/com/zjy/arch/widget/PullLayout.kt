package com.zjy.arch.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import android.widget.OverScroller
import android.widget.Scroller
import android.widget.TextView
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.core.view.children
import com.zjy.architecture.ext.dp
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @author zhengjy
 * @since 2020/07/02
 * Description:
 */
class PullLayout : FrameLayout, NestedScrollingParent3 {

    companion object {
        val TAG = "PullLayout"
    }

    private var mNestedParent = NestedScrollingParentHelper(this)

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        init()
    }

    private var headerView: View? = null
    private var contentView: View? = null
    private lateinit var mScroller: Scroller
    private lateinit var mOverScroller: OverScroller

    private var mLastY = 0f
    private var mScreenHeightPixels: Int = context.resources.displayMetrics.heightPixels

    private val mTotalOffset
        get() = abs(scrollY)
    private val mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private val mMinimumVelocity = ViewConfiguration.get(context).scaledMinimumFlingVelocity

    private fun init() {
        if (childCount > 1) {
            throw IllegalStateException("PullLayout can host only one direct child")
        }
        if (childCount == 1) {
            contentView = getChildAt(0)
        }
        headerView = TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 60f.dp)
            text = "下拉刷新"
            gravity = Gravity.CENTER
        }
        addView(headerView, 0)
        isNestedScrollingEnabled = true
        mScroller = Scroller(context)
        mOverScroller = OverScroller(context)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, left, top, right, bottom)
        children.forEach {
            if (it == headerView) {
                it.layout(0, -it.measuredHeight, it.measuredWidth, 0)
            } else if (it == contentView) {
                layout(0, 0, it.measuredWidth, it.measuredHeight)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastY = event.y
                stopNestedScroll()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaY = event.y - mLastY
                if (deltaY >= mTouchSlop) {
                    val headerHeight = headerView?.measuredHeight ?: 0
                    scrollY = if (deltaY <= headerHeight) {
                        -deltaY.toInt()
                    } else {
                        -(headerHeight + (deltaY - headerHeight) / 2).toInt()
                    }
                }
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                mScroller.startScroll(0, scrollY, 0, abs(scrollY), 300)
                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX, mScroller.currY)
            postInvalidate()
//        } else if (scrollY < 0) {
//            mScroller.startScroll(0, scrollY, 0, abs(scrollY), 300)
//            invalidate()
        }
    }

    override fun getNestedScrollAxes(): Int {
        return ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        val accepted = isEnabled
                && isNestedScrollingEnabled
                && nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
        return accepted
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        mNestedParent.onNestedScrollAccepted(child, target, axes, type)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (dy > 0 && scrollY < 0) {
            if (dy > mTotalOffset) {
                consumed[1] = mTotalOffset
            } else {
                consumed[1] = dy
            }
            Log.d(TAG, "PreScroll:${consumed[1]}")
            scrollY += consumed[1]
        }
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
                                dyUnconsumed: Int, type: Int, consumed: IntArray) {
        Log.d(TAG, "x:${dxUnconsumed}, y:${dyUnconsumed}, offset:${mTotalOffset}")
        var consumedY = 0
        if (dyUnconsumed < 0) {
            consumedY = if (mTotalOffset <= headerView!!.measuredHeight) {
                dyUnconsumed
            } else {
                (sqrt(abs(dyUnconsumed.toDouble()) / (headerView!!.measuredHeight)) * dyUnconsumed / 2).toInt()
            }
        }

        consumed[1] += consumedY
        scrollY += consumed[1]
        invalidate()
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
                                dyUnconsumed: Int, type: Int) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, IntArray(2))
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
                                dyUnconsumed: Int) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, ViewCompat.TYPE_NON_TOUCH, IntArray(2))
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        mNestedParent.onStopNestedScroll(target, type)
    }

//    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
//        Log.d(TAG, "Vx:${velocityX}, Vy:${velocityY}")
//        if (velocityY < 0 && scrollY <= 0) {
//            mScroller.fling(0, scrollY, velocityX.toInt(), velocityY.toInt(), 0, 0, -Int.MAX_VALUE, Int.MAX_VALUE)
//            invalidate()
//        }
//        return super.onNestedPreFling(target, velocityX, velocityY)
//    }
//
//    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
//        Log.d(TAG, "Vx:${velocityX}, Vy:${velocityY}, consumed:${consumed}")
//        mOverScroller.fling(0, scrollY, velocityX.toInt(), velocityY.toInt(), 0, 0, -Int.MAX_VALUE, Int.MAX_VALUE, 0, headerView!!.measuredHeight)
//        return super.onNestedFling(target, velocityX, velocityY, consumed)
//    }

}