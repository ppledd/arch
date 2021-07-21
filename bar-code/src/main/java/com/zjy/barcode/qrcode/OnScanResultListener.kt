package com.zjy.barcode.qrcode

import android.view.View
import com.google.mlkit.vision.barcode.Barcode

interface OnScanResultListener {
    fun onSuccess(view: View, result: Barcode)
}