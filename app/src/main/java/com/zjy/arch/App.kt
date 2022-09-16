package com.zjy.arch

import android.app.Application
import android.content.Context
import com.zjy.arch.hook.HookHelper
import com.zjy.architecture.Arch

/**
 * @author zhengjy
 * @since 2020/07/17
 * Description:
 */
class App : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        HookHelper.hookInstrumentation(base)
    }

    override fun onCreate() {
        super.onCreate()
        Arch.init(this, true, "", arrayOf())
    }
}