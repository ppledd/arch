package com.zjy.arch.hook

import android.app.Instrumentation
import android.content.Context
import android.util.Log

/**
 * @author zhengjy
 * @since 2022/09/14
 * Description:
 */
object HookHelper {

    fun hookInstrumentation(context: Context?) {
        try {
            val contextImplClass = Class.forName("android.app.ContextImpl")
            val activityThread = context.getField(contextImplClass, "mMainThread")
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val proxy = InstrumentationProxy(
                activityThread.getFieldT<Instrumentation>(activityThreadClass, "mInstrumentation"),
                context?.packageManager
            )
            activityThread.setField(activityThreadClass, "mInstrumentation", proxy)
            println(activityThreadClass.toString())
        } catch (e: Exception) {
            // hook failed
            Log.e("HookHelper", e.toString())
        }
    }
}