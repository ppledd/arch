package com.zjy.architecture.util.rom

import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import java.lang.reflect.Method

/**
 * @author zhengjy
 * @since 2021/07/21
 * Description:
 */
class Xiaomi : Rom {

    companion object {
        /**
         * 锁屏显示界面权限code
         */
        const val SHOW_WHEN_LOCK = 10020
        /**
         * 后台启动权限code
         */
        const val BACKGROUND_START = 10021
    }

    override fun canShowViewOnLockScreen(context: Context): Boolean {
        val ops = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        try {
            val op = SHOW_WHEN_LOCK
            val method: Method = ops.javaClass.getMethod(
                "checkOpNoThrow",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                String::class.java
            )
            val result = method.invoke(ops, op, android.os.Process.myUid(), context.packageName) as Int
            return result == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun isBackgroundStartAllowed(context: Context): Boolean {
        val ops = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        try {
            val op = BACKGROUND_START
            val method: Method = ops.javaClass.getMethod(
                "checkOpNoThrow",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                String::class.java
            )
            val result = method.invoke(ops, op, android.os.Process.myUid(), context.packageName) as Int
            return result == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun openPermissionSetting(context: Context) {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        val componentName = ComponentName(
            "com.miui.securitycenter",
            "com.miui.permcenter.permissions.AppPermissionsEditorActivity"
        )
        intent.component = componentName
        intent.putExtra("extra_pkgname", context.packageName)
        context.startActivity(intent)
    }
}