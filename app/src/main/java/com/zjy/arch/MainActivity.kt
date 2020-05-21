package com.zjy.arch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.request.RequestOptions
import com.zjy.architecture.ext.load
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val imageUrl = "https://mmbiz.qpic.cn/mmbiz_png/MOu2ZNAwZwOPb5hszoy7LYnoN5FKueEq4oT4VxcqjELLtBaqkMMdh6Fkiaya1uLfD0clTbjocU5pvZo7VK2ak3A/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView.load(imageUrl) {
            apply(RequestOptions().placeholder(R.mipmap.ic_launcher))
        }
        MutableLiveData<String>().observe({ this.lifecycle }) {

        }
    }
}
