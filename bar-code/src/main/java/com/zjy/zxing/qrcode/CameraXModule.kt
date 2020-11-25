package com.zjy.zxing.qrcode

import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.zxing.Result
import kotlinx.android.synthetic.main.view_scan_auto_zoom.view.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * @author zhengjy
 * @since 2020/11/19
 * Description:CameraX相机和用例初始化
 */
class CameraXModule(val view: AutoZoomScanView) {

    private lateinit var mLifecycleOwner: LifecycleOwner
    private var camera: Camera? = null
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private lateinit var preview: Preview
    private lateinit var qrCodeAnalyzer: QRCodeAnalyzer
    private lateinit var qrAnalysis: ImageAnalysis

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private val mainExecutor = ContextCompat.getMainExecutor(view.context)

    private var resolutionHeight = 0

    fun bindWithCameraX(lifecycleOwner: LifecycleOwner, callback: (Result) -> Unit) {
        mLifecycleOwner = lifecycleOwner
        val metrics = DisplayMetrics().also { view.display.getRealMetrics(it) }
        //Log.d(TAG, "Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")
        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        //Log.i(TAG, "Preview aspect ratio: $screenAspectRatio")
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(view.context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val width = view.measuredWidth
            val height = (width * screenAspectRatio).toInt()
            resolutionHeight = height
            preview = Preview.Builder()
                .setTargetResolution(Size(width, height))
                .build()
            preview.setSurfaceProvider(view.preView.surfaceProvider)

            qrCodeAnalyzer = QRCodeAnalyzer(this) {
                mainExecutor.execute {
                    callback(it)
                }
            }
            // ImageAnalysis
            qrAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(width, height))
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, qrCodeAnalyzer)
                }

            // Must unbind the use-cases before rebinding them
            cameraProvider.unbindAll()

            try {
                // A variable number of use-cases can be passed here -
                // camera provides access to CameraControl & CameraInfo
                camera = cameraProvider.bindToLifecycle(
                    mLifecycleOwner, cameraSelector, preview, qrAnalysis
                )
                setFocus(view.width.toFloat() / 2, view.height.toFloat() / 2)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, mainExecutor)
    }

    fun enableFlash(enable: Boolean) {
        camera?.cameraControl?.enableTorch(enable)
    }

    fun setFocus(x: Float, y: Float) {
        val factory = SurfaceOrientedMeteringPointFactory(
            view.width.toFloat(), view.height.toFloat()
        )
        //create a point on the center of the view
        val autoFocusPoint = factory.createPoint(x, y)

        camera?.cameraControl?.startFocusAndMetering(
            FocusMeteringAction.Builder(
                autoFocusPoint,
                FocusMeteringAction.FLAG_AF
            ).apply {
                //auto-focus every 1 seconds
                setAutoCancelDuration(1, TimeUnit.SECONDS)
            }.build()
        )
    }

    private fun aspectRatio(width: Int, height: Int): Double {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return RATIO_4_3_VALUE
        }
        return RATIO_16_9_VALUE
    }

    fun setZoomRatio(zoomRatio: Float) {
        if (zoomRatio > getMaxZoomRatio()) {
            return
        }
        camera?.cameraControl?.setZoomRatio(zoomRatio)
    }

    fun getZoomRatio(): Float {
        return camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 0F
    }

    fun getMaxZoomRatio(): Float {
        return camera?.cameraInfo?.zoomState?.value?.maxZoomRatio ?: 0F
    }

    companion object {
        private const val TAG = "CameraXImp"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }
}