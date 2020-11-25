package com.zjy.arch

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zjy.architecture.Arch
import com.zjy.architecture.ext.load
import com.zjy.architecture.net.HttpResult
import com.zjy.filepicker.FileBrowserActivity
import com.zjy.zxing.ScanCodeActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers

class MainActivity : AppCompatActivity() {

    private val imageUrl = "https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOPb5hszoy7LYno" +
            "N5FKueEq4oT4VxcqjELLtBaqkMMdh6Fkiaya1uLfD0clTbjocU5pvZo7VK2ak3A/640?wx_fmt=png&" +
            "tp=webp&wxfrom=5&wx_lazy=1&wx_co=1"

    private val viewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView?.apply {
            load(imageUrl) {
                apply(RequestOptions().placeholder(R.mipmap.ic_launcher))
            }
            setOnClickListener {
                startActivity(Intent(this@MainActivity, WebViewActivity::class.java))
            }
        }
        textView.setOnClickListener {
            startActivity(Intent(this@MainActivity, WidgetActivity::class.java))
        }
        liveData(Dispatchers.IO) {
            emit("")
        }
        gson()
//        MessageServiceProxy.init(this) {
//            object : ServiceProfile {
//                override fun clientVersion(): Int {
//                    return 100
//                }
//
//                override fun longLinkDebugIP(): String? {
//                    return "192.168.21.155"
//                }
//
//                override fun longLinkHost(): String {
//                    return "192.168.21.155"
//                }
//
//                override fun longLinkPorts(): IntArray {
//                    return intArrayOf(8080, 8081, 9090)
//                }
//
//                override fun shortLinkPort(): Int {
//                    return 8080
//                }
//            }
//        }
//        MessageServiceProxy.accountInfo = AppLogic.AccountInfo(20L, "郑家烨")
//        button.setOnClickListener {
//            content.text.toString().trim().apply {
//                if (isNotEmpty()) {
//                    MessageServiceProxy.send(SimpleTextTaskWrapper(this))
//                }
//            }
//        }
        button.setOnClickListener {

        }
        button2.setOnClickListener {
            startActivity(Intent(this, FileBrowserActivity::class.java))
        }
        button3.setOnClickListener {
            startActivity(Intent(this, ViewPager2Activity::class.java))
        }
        button4.setOnClickListener {
//            startActivity(Intent(this, RecyclerActivity::class.java))
            startActivityForResult(Intent(this, ScanCodeActivity::class.java), 888)
        }
    }

    private fun gson() {
        val depInfo = Gson().fromJson<HttpResult<DepInfo>>(JSON, object : TypeToken<HttpResult<DepInfo>>() {}.type)
        textView.text = depInfo.data.realName
    }

    override fun onPause() {
        super.onPause()
//        MessageServiceProxy.setForeground(false)
    }

    override fun onResume() {
        super.onResume()
//        MessageServiceProxy.setForeground(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        Arch.release()
//        Mars.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 888) {
                val str = data?.getStringExtra("result")
                content.setText(str)
                content.setSelection(0, str?.length ?: 0)
                content.requestFocus()
            }
        }
    }
}
