package com.zjy.arch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.zjy.arch.bean.User
import com.zjy.arch.databinding.ActivityDataBindBinding

/**
 * @author zhengjy
 * @since 2022/09/15
 * Description:
 */
class DataBindingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDataBindBinding.inflate(layoutInflater)
        binding.user = User("zhengjy", 20)
        Class.forName("")
    }
}