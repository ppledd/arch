package com.zjy.arch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.liveData
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zjy.architecture.ext.load
import com.zjy.architecture.net.HttpResult
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers

class MainActivity : AppCompatActivity() {

    private val imageUrl = "https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOPb5hszoy7LYno" +
            "N5FKueEq4oT4VxcqjELLtBaqkMMdh6Fkiaya1uLfD0clTbjocU5pvZo7VK2ak3A/640?wx_fmt=png&" +
            "tp=webp&wxfrom=5&wx_lazy=1&wx_co=1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView?.apply {
            load(imageUrl) {
                apply(RequestOptions().placeholder(R.mipmap.ic_launcher))
            }
            setOnClickListener {

            }
        }
        liveData(Dispatchers.IO) {
            emit("")
        }
        gson()

    }

    private fun gson() {
        val depInfo = Gson().fromJson<HttpResult<DepInfo>>(JSON, object : TypeToken<HttpResult<DepInfo>>() {}.type)
        textView.text = depInfo.data.realName
    }

    private fun moshi() {
//        val moshi = Moshi.Builder()
//            .add(KotlinJsonAdapterFactory())
//            .build()
//
//        val adapter = HttpResultJsonAdapter<DepInfo>(moshi, arrayOf(DepInfo::class.java))
//        val depInfo = adapter.fromJson(JSON) as HttpResult<DepInfo>

//        val depInfo = moshi.adapter<HttpResult<DepInfo>>(
//            Types.newParameterizedType(HttpResult::class.java, DepInfo::class.java)
//        ).fromJson(JSON) as HttpResult<DepInfo>

//        val depInfo = moshi.adapter<HttpResult<DepInfo>>(HttpResult::class.java).fromJson(JSON)

//        textView.text = depInfo.data.position
    }
}
