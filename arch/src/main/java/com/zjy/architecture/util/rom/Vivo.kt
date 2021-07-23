package com.zjy.architecture.util.rom

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build

/**
 * @author zhengjy
 * @since 2021/07/21
 * Description:
 */
class Vivo : Rom.DefaultRom() {

    override fun canShowViewOnLockScreen(context: Context): Boolean {
        val uri = Uri.parse("content://com.vivo.permissionmanager.provider.permission/control_locked_screen_action")
        val selection = "pkgname = ?"
        val selectionArgs = arrayOf(context.packageName)
        return context.contentResolver.query(uri, null, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val currentMode = cursor.getInt(cursor.getColumnIndex("currentstate"));
                currentMode == 1
            } else {
                true
            }
        } ?: true
    }

    /**
     * 判断Vivo后台弹出界面状态， 1无权限，0有权限
     * @param context context
     */
    override fun isBackgroundStartAllowed(context: Context): Boolean {
        val uri = Uri.parse("content://com.vivo.permissionmanager.provider.permission/start_bg_activity")
        val selection = "pkgname = ?"
        val selectionArgs = arrayOf(context.packageName)
        var state = 1
        try {
            context.contentResolver.query(uri, null, selection, selectionArgs, null)?.use {
                if (it.moveToFirst()) {
                    state = it.getInt(it.getColumnIndex("currentstate"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return state == 0
    }

    override fun openPermissionSetting(context: Context) {
        val localIntent = Intent()
        if (((Build.MODEL.contains("Y85")) && (!Build.MODEL.contains("Y85A"))) || (Build.MODEL.contains("vivo Y53L"))) {
            localIntent.component = ComponentName(
                "com.vivo.permissionmanager",
                "com.vivo.permissionmanager.activity.PurviewTabActivity"
            )
            localIntent.putExtra("packagename", context.packageName)
            localIntent.putExtra("tabId", "1")
        } else {
            localIntent.component = ComponentName(
                "com.vivo.permissionmanager",
                "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity"
            )
            localIntent.action = "secure.intent.action.softPermissionDetail"
            localIntent.putExtra("packagename", context.packageName)
        }
        context.startActivity(localIntent)
    }
}