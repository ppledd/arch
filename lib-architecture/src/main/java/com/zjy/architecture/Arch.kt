package com.zjy.architecture

import android.content.Context

/**
 * @author zhengjy
 * @since 2020/05/21
 * Description:
 */
object Arch {

    /**
     * 获取应用全局[Context]
     */
    val context: Context
        get() = checkNotNull(mContext) { "Please init Arch first" }

    private var mContext: Context? = null

    fun init(context: Context) {
        this.mContext = context.applicationContext
    }
}