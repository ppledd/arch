package com.zjy.barcode.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import com.zjy.barcode.R
import com.zjy.barcode.qrcode.CameraXModule
import com.zjy.barcode.qrcode.OnScanResultListener
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

    fun freeze(bitmap: Bitmap?, detectPoints: List<Point>) {
        iv_preview.post {
            iv_preview.setImageBitmap(bitmap?.drawPoint(detectPoints, 20f))
            iv_preview.visibility = View.VISIBLE
            preView.visibility = View.GONE
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (gestureDetector.onTouchEvent(event)) {
            return true
        }
        return super.onTouchEvent(event)
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

    fun enableFlash(enable: Boolean) {
        module.enableFlash(enable)
    }

    private fun Bitmap?.drawPoint(center: List<Point>, radius: Float): Bitmap? {
        if (this == null) return null
        val paint = Paint().also { it.isAntiAlias = true }
        val origin = rotateBitmap(this, 90f)
        val bitmap = Bitmap.createBitmap(origin.width, origin.height, origin.config)
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(origin, 0f, 0f, paint)
        center.forEach {
            paint.color = Color.parseColor("#ffffff")
            canvas.drawCircle(it.x.toFloat(), it.y.toFloat(), radius + 10, paint)
            paint.color = Color.parseColor("#0066ff")
            canvas.drawCircle(it.x.toFloat(), it.y.toFloat(), radius, paint)
        }
        return bitmap
    }

    private fun rotateBitmap(origin: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { setRotate(degrees) }
        val newBM = Bitmap.createBitmap(origin, 0, 0, origin.width, origin.height, matrix, false)
        if (newBM == origin) {
            return newBM
        }
        origin.recycle()
        return newBM
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