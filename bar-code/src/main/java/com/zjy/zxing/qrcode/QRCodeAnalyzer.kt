package com.zjy.zxing.qrcode

import android.annotation.SuppressLint
import android.graphics.*
import android.graphics.ImageFormat.*
import android.media.Image
import android.os.Build
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.detector.Detector
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

/**
 * @author zhengjy
 * @since 2020/11/19
 * Description:QR Code分析器
 */
class QRCodeAnalyzer(
    private val module: CameraXModule,
    private val callback: (Result) -> Unit
) : ImageAnalysis.Analyzer {

    private val map = mapOf<DecodeHintType, Collection<BarcodeFormat>>(
        DecodeHintType.POSSIBLE_FORMATS to arrayListOf(BarcodeFormat.QR_CODE)
    )
    private val reader: MultiFormatReader = MultiFormatReader().apply { setHints(map) }

    private val yuvFormats = mutableListOf(YUV_420_888)

    /**
     * 是否已经识别
     */
    private var recognized: Boolean = false

    /**
     * 上一次放大的时间
     */
    private var lastZoomTime = 0L

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            yuvFormats.addAll(listOf(YUV_422_888, YUV_444_888))
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(image: ImageProxy) {
        if (recognized) {
            // 如果已经识别，则不再分析图像
            image.close()
            return
        }
        if (image.format !in yuvFormats) {
            image.close()
            return
        }
        val startTime = System.currentTimeMillis()
        val data = image.planes[0].buffer.toByteArray()
        val height = image.height
        val width = image.width

        val source = PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        try {
            val detectResult = Detector(bitmap.blackMatrix).detect(map)
            if (zoomCamera(detectResult.points, height/*这里高度为1080，宽度为1920，与预期相反，因此传入height*/)) {
                image.close()
                return
            }
            val result = reader.decode(bitmap)
            Log.i(
                "QRCodeAnalyzer",
                "result=$result  timeUsage:${System.currentTimeMillis() - startTime}"
            )
            // 识别到的二维码中心点
            val centerX = (detectResult.points[0].x + detectResult.points[2].x) / 2f
            val centerY = (detectResult.points[0].y + detectResult.points[2].y) / 2f
            module.view.freeze(
                image.image?.toBitmap()?.drawPoint(Point(centerX.toInt(), centerY.toInt()), 20f)
            )
            recognized = true
            callback.invoke(result)
        } catch (e: Exception) {
            // e.printStackTrace()
        } finally {
            image.close()
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

        val yuvImage = YuvImage(nv21, NV21, this.width, this.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private fun Bitmap?.drawPoint(center: Point, radius: Float): Bitmap? {
        if (this == null) return null
        val paint = Paint().also {
            it.color = Color.parseColor("#ffffff")
            it.isAntiAlias = true
        }
        val bitmap = Bitmap.createBitmap(this.width, this.height, this.config)
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(this, 0f, 0f, paint)
        canvas.drawCircle(center.x.toFloat(), center.y.toFloat(), radius + 10, paint)
        paint.color = Color.parseColor("#0066ff")
        canvas.drawCircle(center.x.toFloat(), center.y.toFloat(), radius, paint)
        return rotateBitmap(bitmap, 90f)
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

    private fun zoomCamera(points: Array<ResultPoint>, width: Int): Boolean {
        val qrWidth = calculateDistance(points[0], points[1]) * 2
        val imageWidth = width.toFloat()
        if (qrWidth < imageWidth / 8 && System.currentTimeMillis() - lastZoomTime > 800L) {
            lastZoomTime = System.currentTimeMillis()
            module.setZoomRatio(module.getZoomRatio() + 1)
            return true
        }
        return false
    }

    private fun calculateDistance(point1: ResultPoint, point2: ResultPoint): Float {
        return ResultPoint.distance(point1, point2)
    }
}

private fun ByteBuffer.toByteArray(): ByteArray {
    val data = ByteArray(rewind().remaining())
    get(data)
    return data
}