package com.zjy.architecture

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import com.tencent.mars.xlog.Log
import com.tencent.mars.xlog.Xlog

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

    /**
     * 在Application的onCreate中初始化
     */
    @JvmStatic
    @JvmOverloads
    fun init(context: Context, debug: Boolean = false) {
        this.mContext = context.applicationContext
        openXLog(context, debug)
    }

    /**
     * 通常在Application的onTerminate中调用，用于释放资源，关闭日志
     */
    @JvmStatic
    fun terminate() {
        Log.appenderClose()
    }

    /**
     * 开启日志
     */
    private fun openXLog(context: Context, debug: Boolean) {
        System.loadLibrary("c++_shared")
        System.loadLibrary("marsxlog")
        val pid = Process.myPid()
        var processName: String? = null
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (appProcess in am.runningAppProcesses) {
            if (appProcess.pid == pid) {
                processName = appProcess.processName
                break
            }
        }
        if (processName == null) {
            return
        }

        val root = context.getExternalFilesDir("")
        val logPath = "$root/arch/log"

        val logFileName = if (processName.indexOf(":") == -1)
            "Arch"
        else
            "Arch_${processName.substring(processName.indexOf(":") + 1)}"

        if (debug) {
            Xlog.appenderOpen(
                    Xlog.LEVEL_VERBOSE, Xlog.AppednerModeAsync, "", logPath,
                    "DEBUG_$logFileName", 5, ""
            )
            Xlog.setConsoleLogOpen(true)
        } else {
            Xlog.appenderOpen(
                    Xlog.LEVEL_INFO, Xlog.AppednerModeAsync, "", logPath,
                    logFileName, 3, ""
            )
            Xlog.setConsoleLogOpen(false)
        }
        Log.setLogImp(Xlog())
    }
}