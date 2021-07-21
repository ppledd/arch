package com.zjy.architecture.util.rom

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * @author zhengjy
 * @since 2021/07/21
 * Description:
 */
interface Rom {

    /**
     * 是否允许后台弹出界面
     */
    fun isBackgroundStartAllowed(context: Context): Boolean

    /**
     * 跳转到app权限设置页面
     */
    fun openPermissionSetting(context: Context)

    class DefaultRom : Rom {
        override fun isBackgroundStartAllowed(context: Context): Boolean {
            return true
        }

        override fun openPermissionSetting(context: Context) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", context.packageName, null)
            context.startActivity(intent)
        }
    }
}