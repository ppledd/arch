package com.zjy.pluginhook

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * @author zhengjy
 * @since 2022/09/14
 * Description:
 */
class TargetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_target)
    }
}