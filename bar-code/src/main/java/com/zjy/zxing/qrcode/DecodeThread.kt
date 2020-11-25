package com.zjy.zxing.qrcode

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.detector.Detector

/**
 * @author zhengjy
 * @since 2020/11/23
 * Description:
 */
class DecodeThread(
    private val origin: Bitmap,
    private val callback: (Result?) -> Unit
) : Thread("DecodeHandler") {

    private val map = mapOf<DecodeHintType, Any>(
        DecodeHintType.POSSIBLE_FORMATS to arrayListOf(BarcodeFormat.QR_CODE),
        DecodeHintType.TRY_HARDER to true

    )
    private val reader: MultiFormatReader = MultiFormatReader().apply { setHints(map) }

    private val handler = Handler(Looper.getMainLooper())

    private val yuvFormats = mutableListOf(ImageFormat.YUV_420_888)

    private var count = 0
    private var doNotZoom = false

    companion object {
        private const val BORDER_OFFSET = 50
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            yuvFormats.addAll(listOf(ImageFormat.YUV_422_888, ImageFormat.YUV_444_888))
        }
    }

    override fun run() {
        detect(origin)
    }

    private fun detect(picture: Bitmap) {
        if (++count > 3 || doNotZoom) {
            handler.post { callback.invoke(null) }
            return
        }
        val width: Int = picture.width
        val height: Int = picture.height

        val size = width * height
        if (size > 100_0000) {
            doNotZoom = true
        }
        val pix = IntArray(size)
        picture.getPixels(pix, 0, width, 0, 0, width, height)
        val source = RGBLuminanceSource(width, height, pix)
        val bitmap = BinaryBitmap(HybridBinarizer(source))

        try {
            val result = reader.decode(bitmap)
            handler.post { callback.invoke(result) }
            picture.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                val detectResult = Detector(bitmap.blackMatrix).detect(map)
                if (detectResult.points.isNotEmpty()) {
                    val startX = (minOf(
                        detectResult.points[0].x,
                        detectResult.points[1].x,
                        detectResult.points[2].x
                    ).toInt() - BORDER_OFFSET).coerceAtLeast(0)
                    val endX = (maxOf(
                        detectResult.points[0].x,
                        detectResult.points[1].x,
                        detectResult.points[2].x
                    ).toInt() + BORDER_OFFSET).coerceAtMost(width)
                    val startY = (minOf(
                        detectResult.points[0].y,
                        detectResult.points[1].y,
                        detectResult.points[2].y
                    ).toInt() - BORDER_OFFSET).coerceAtLeast(0)
                    val endY = (maxOf(
                        detectResult.points[0].y,
                        detectResult.points[1].y,
                        detectResult.points[2].y
                    ).toInt() + BORDER_OFFSET).coerceAtMost(height)
                    val new =
                        Bitmap.createBitmap(picture, startX, startY, endX - startX, endY - startY)
                    picture.recycle()
                    detect(new)
                } else {
                    picture.recycle()
                }
            } catch (e: Exception) {
                val sizeAddMatrix = Matrix()
                sizeAddMatrix.postScale(2.0f, 2.0f)
                val new = Bitmap.createBitmap(picture, 0, 0, width, height, sizeAddMatrix, true)
                picture.recycle()
                detect(new)
            }
        }
    }
}