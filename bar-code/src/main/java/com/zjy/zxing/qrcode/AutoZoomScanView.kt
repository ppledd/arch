package com.zjy.zxing.qrcode

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import com.zjy.zxing.R
import kotlinx.android.synthetic.main.view_scan_auto_zoom.view.*

/**
 * @author zhengjy
 * @since 2020/11/19
 * Description:
 */
class AutoZoomScanView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var module: CameraXModule
    private var listener: OnScanResultListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_scan_auto_zoom, this)
        module = CameraXModule(this)
    }

    fun freeze(bitmap: Bitmap?) {
        iv_preview.post {
            iv_preview.setImageBitmap(bitmap)
            iv_preview.visibility = View.VISIBLE
            preView.visibility = View.GONE
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }

    fun bindWithLifeCycle(lifecycleOwner: LifecycleOwner) {
        preView.post {
            module.bindWithCameraX(lifecycleOwner) {
                listener?.onSuccess(this, it)
            }
        }
    }

    fun setOnScanResultListener(listener: OnScanResultListener) {
        this.listener = listener
    }

    private val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent?): Boolean {
                module.setZoomRatio(module.getZoomRatio() + 1)
                return super.onDoubleTap(e)
            }

            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                e?.apply {
                    module.setFocus(x, y)
                }
                return super.onSingleTapUp(e)
            }
        })

}