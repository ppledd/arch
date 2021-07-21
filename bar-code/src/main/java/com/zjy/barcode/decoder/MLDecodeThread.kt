package com.zjy.barcode.decoder

import android.graphics.Bitmap
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

/**
 * @author zhengjy
 * @since 2020/11/23
 * Description:
 */
class MLDecodeThread(
    private val origin: Bitmap,
    private val callback: (Barcode?) -> Unit
) : Thread("MLDecodeThread") {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    override fun run() {
        detect(origin)
    }

    private fun detect(picture: Bitmap) {
        val image = InputImage.fromBitmap(picture, 0)
        scanner.process(image)
            .addOnSuccessListener { result ->
                callback.invoke(result.firstOrNull())
            }
            .addOnCompleteListener {
                picture.recycle()
            }
    }
}