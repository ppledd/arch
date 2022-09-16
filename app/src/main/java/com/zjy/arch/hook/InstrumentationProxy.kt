package com.zjy.arch.hook

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import com.zjy.arch.StubActivity

/**
 * @author zhengjy
 * @since 2022/09/14
 * Description:
 */
class InstrumentationProxy(
    private val mInstrumentation: Instrumentation?,
    private val mPackageManager: PackageManager?
) : Instrumentation() {

    private val execStartActivityMethod = Instrumentation::class.java.getDeclaredMethod(
        "execStartActivity",
        Context::class.java,
        IBinder::class.java,
        IBinder::class.java,
        Activity::class.java,
        Intent::class.java,
        Int::class.java,
        Bundle::class.java
    ).apply {
        isAccessible = true
    }

    private val newActivityMethod =  Instrumentation::class.java.getDeclaredMethod(
        "newActivity",
        ClassLoader::class.java,
        String::class.java,
        Intent::class.java
    ).apply {
        isAccessible = true
    }

    override fun newActivity(cl: ClassLoader?, className: String?, intent: Intent?): Activity? {
        val origin = intent?.getParcelableExtra<Intent>("origin")
        if (origin == null) {
            return newActivityMethod.invoke(mInstrumentation, cl, className, intent) as Activity?
        }
        val clsName = origin.component?.className ?: className
        return newActivityMethod.invoke(mInstrumentation, cl, clsName, origin) as Activity?
    }

    fun execStartActivity(
        who: Context, contextThread: IBinder?, token: IBinder?, target: Activity?,
        intent: Intent, requestCode: Int, options: Bundle?
    ): ActivityResult? {
        val infos = mPackageManager?.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        val newIntent = if (infos.isNullOrEmpty() && intent.component != null) {
            Intent().setClassName(who, StubActivity::class.java.canonicalName!!)
                .putExtra("origin", intent)
        } else intent

        return execStartActivityMethod.invoke(
            mInstrumentation,
            who,
            contextThread,
            token,
            target,
            newIntent,
            requestCode,
            options
        ) as ActivityResult?
    }
}