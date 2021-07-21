package com.zjy.architecture.util.rom

import android.content.ComponentName
import android.content.Context
import android.content.Intent

/**
 * @author zhengjy
 * @since 2021/07/21
 * Description:
 */
class Huawei : Rom {

    override fun isBackgroundStartAllowed(context: Context): Boolean {
        TODO("Not yet implemented")
    }

    override fun openPermissionSetting(context: Context) {
        val intent = Intent()
        intent.putExtra("packageName", context.packageName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.component = ComponentName(
            "com.huawei.systemmanager",
            "com.huawei.permissionmanager.ui.MainActivity"
        )
        context.startActivity(intent)
    }
}