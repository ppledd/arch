package com.zjy.arch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.zjy.arch.fragment.ChartFragment
import com.zjy.arch.fragment.PullLayoutFragment

class WidgetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget)

        supportFragmentManager.commit {
//            add(R.id.fcv_container, PullLayoutFragment(R.layout.fragment_pull_layout))
            add(R.id.fcv_container, ChartFragment(R.layout.fragment_chart))
        }
    }
}