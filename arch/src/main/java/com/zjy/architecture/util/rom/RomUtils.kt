package com.zjy.architecture.util.rom

import android.content.Context
import android.os.Build

/**
 * @author zhengjy
 * @since 2021/07/21
 * Description:适配不同手机系统的工具类
 *
 * 华为——Huawei
 * 魅族——Meizu
 * 小米——Xiaomi
 * 索尼——Sony
 * oppo——OPPO
 * LG——LG
 * vivo——vivo
 * 三星——samsung
 * 乐视——Letv
 * 中兴——ZTE
 * 酷派——YuLong
 * 联想——LENOVO
 */
object RomUtils : Rom {

    private val map = mutableMapOf<String, Rom>(
        "xiaomi" to Xiaomi(),
        "huawei" to Huawei(),
        "oppo" to Oppo(),
        "vivo" to Vivo(),
    )

    private val rom by lazy { findRom() }
    private val default by lazy { Rom.DefaultRom() }

    private fun findRom(): Rom {
        for ((manufacturer, rom) in map) {
            if (manufacturer.equals(Build.MANUFACTURER, true)) {
                return rom
            }
        }
        return default
    }

    /**
     * 需要在使用RomUtils之前调用
     */
    fun registerRom(manufacturer: String, rom: Rom) {
        map[manufacturer] = rom
    }

    override fun isBackgroundStartAllowed(context: Context): Boolean {
        return try {
            rom.isBackgroundStartAllowed(context)
        } catch (e: Exception) {
            default.isBackgroundStartAllowed(context)
        }
    }

    override fun openPermissionSetting(context: Context) {
        try {
            rom.openPermissionSetting(context)
        } catch (e: Exception) {
            default.openPermissionSetting(context)
        }
    }
}