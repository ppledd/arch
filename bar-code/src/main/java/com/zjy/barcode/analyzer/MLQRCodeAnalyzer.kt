package com.zjy.barcode.analyzer

import android.annotation.SuppressLint
import android.graphics.*
import android.media.Image
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.zjy.barcode.qrcode.CameraXModule
import java.io.ByteArrayOutputStream

/**
 * @author zhengjy
 * @since 2021/04/28
 * Description:
 */
class MLQRCodeAnalyzer(
    private val module: CameraXModule,
    private val callback: (Barcode) -> Unit
) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    /**
     * 是否已经识别
     */
    private var recognized: Boolean = false

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        if (recognized) {
            // 如果已经识别，则不再分析图像
            imageProxy.close()
            return
        }
        val mediaImage = imageProxy.image ?: kotlin.run {
            imageProxy.close()
            return
        }
        try {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { result ->
                    if (!result.isNullOrEmpty()) {
                        recognized = true
                        val points = mutableListOf<Point>()
                        val single = result.size == 1
                        result.forEach {
                            it.boundingBox?.apply {
                                points.add(Point(centerX(), centerY()))
                            }
                            if (single) {
                                callback.invoke(it)
                            }
                        }
                        module.view.freeze(mediaImage.toBitmap(), points)
                    }
                }
                .addOnFailureListener {

                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } catch (e: Exception) {
            // e.printStackTrace()
        }
    }

    private fun Image.toBitmap(): Bitmap {
        val yBuffer = planes[0].buffer // Y
        val uBuffer = planes[1].buffer // U
        val vBuffer = planes[2].buffer // V

        val ySize = yBuffer.rewind().remaining()
        val uSize = uBuffer.rewind().remaining()
        val vSize = vBuffer.rewind().remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        //U and V are swapped
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}