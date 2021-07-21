package com.zjy.architecture.util.rom

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings

/**
 * @author zhengjy
 * @since 2021/07/21
 * Description:
 */
class Oppo : Rom {
    override fun isBackgroundStartAllowed(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context)
        }
        return true
    }

    override fun openPermissionSetting(context: Context) {
        val intent = Intent()
        intent.putExtra("packageName", context.packageName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.component = ComponentName(
            "com.color.safecenter",
            "com.color.safecenter.permission.PermissionManagerActivity"
        )
        context.startActivity(intent)
    }
}