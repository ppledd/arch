package com.zjy.arch

import android.app.Application
import com.tencent.mmkv.MMKV
import com.zjy.architecture.Arch

/**
 * @author zhengjy
 * @since 2020/07/17
 * Description:
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Arch.init(this, true)
        MMKV.initialize(this)
    }
}