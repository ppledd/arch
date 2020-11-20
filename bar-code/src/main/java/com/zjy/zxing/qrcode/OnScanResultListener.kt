package com.zjy.zxing.qrcode

import android.view.View
import com.google.zxing.Result

interface OnScanResultListener {
    fun onSuccess(view: View, result: Result)
}