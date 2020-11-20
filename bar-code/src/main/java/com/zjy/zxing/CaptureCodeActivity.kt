package com.zjy.zxing

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.Result
import com.zjy.zxing.qrcode.OnScanResultListener
import kotlinx.android.synthetic.main.activity_capture_code.*

/**
 * @author zhengjy
 * @since 2020/11/19
 * Description:二维码扫描界面
 */
class CaptureCodeActivity : AppCompatActivity(), OnScanResultListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_code)

        scanView.bindWithLifeCycle(this)
        scanView.setOnScanResultListener(this)
    }

    override fun onSuccess(view: View, result: Result) {
        Toast.makeText(this, result.text, Toast.LENGTH_SHORT).show()
        scanView.postDelayed({
            setResult(RESULT_OK, Intent().putExtra("result", result.text))
            finish()
        }, 500)
    }
}