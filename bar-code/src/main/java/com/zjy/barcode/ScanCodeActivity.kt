package com.zjy.barcode

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.barcode.Barcode
import com.zjy.barcode.decoder.MLDecodeThread
import com.zjy.barcode.qrcode.OnScanResultListener
import kotlinx.android.synthetic.main.activity_scan_code.*
import java.io.FileInputStream

/**
 * @author zhengjy
 * @since 2020/11/19
 * Description:二维码扫描界面
 */
class ScanCodeActivity : AppCompatActivity(), OnScanResultListener, View.OnClickListener {

    private var flash = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_code)

        scanView.setOnScanResultListener(this)

        iv_flash.setOnClickListener(this)
        iv_album.setOnClickListener(this)

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
            } else {
                scanView.bindWithLifeCycle(this)
            }
        }
    }

    override fun onSuccess(view: View, result: Barcode) {
        Toast.makeText(this, result.displayValue, Toast.LENGTH_SHORT).show()
        scanView.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        scanView.postDelayed({
            setResult(RESULT_OK, Intent().putExtra("result", result.displayValue))
            finish()
        }, 800)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_flash -> {
                flash = !flash
                scanView.enableFlash(flash)
            }
            R.id.iv_album -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_READ_STORAGE
                    )
                    return
                }
                val intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(intent, SELECT_PIC)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PIC) {
                val selectedImage = data!!.data //获取系统返回的照片的Uri
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val cursor = contentResolver.query(
                    selectedImage!!,
                    filePathColumn, null, null, null
                ) //从系统表中查询指定Uri对应的照片
                cursor!!.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                val path = cursor.getString(columnIndex) //获取照片路径
                cursor.close()
                val picture = decodeBitmapFromPath2(path)
                picture?.also { bitmap ->
                    MLDecodeThread(bitmap) {
                        if (it != null) {
                            setResult(RESULT_OK, Intent().putExtra("result", it.displayValue))
                            finish()
                        } else {
                            Toast.makeText(this, "无法识别图片", Toast.LENGTH_SHORT).show()
                        }
                    }.start()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scanView.bindWithLifeCycle(this)
                } else {
                    Toast.makeText(this, R.string.code_permission_denied, Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_READ_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(intent, SELECT_PIC)
                } else {
                    Toast.makeText(this, R.string.code_permission_denied, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun decodeBitmapFromPath(path: String?): Bitmap? {
        val reqWidth = 800
        val reqHeight = 480
        val newOpts = BitmapFactory.Options()
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true //获取原始图片大小
        BitmapFactory.decodeFile(path, newOpts) // 此时返回bm为空
        val width = newOpts.outWidth.toFloat()
        val height = newOpts.outHeight.toFloat()
        // 缩放比，由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        var wSize = 1 // wSize=1表示不缩放
        if (width > reqWidth) { // 如果宽度大的话根据宽度固定大小缩放
            wSize = (width / reqWidth).toInt()
        }
        var hSize = 1 // wSize=1表示不缩放
        if (height > reqHeight) { // 如果高度高的话根据宽度固定大小缩放
            hSize = (height / reqHeight).toInt()
        }
        var size = Math.max(wSize, hSize)
        if (size <= 0) size = 1
        newOpts.inSampleSize = size // 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        newOpts.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(path, newOpts)
    }

    private fun decodeBitmapFromPath2(path: String?): Bitmap? {
        if (path == null) return null
        val fis = FileInputStream(path)
        val opts = BitmapFactory.Options().also { it.inSampleSize = 1 }
        return BitmapFactory.decodeStream(fis, null, opts)
    }

    companion object {
        const val SELECT_PIC = 100

        const val REQUEST_CAMERA = 200
        const val REQUEST_READ_STORAGE = 201
    }
}