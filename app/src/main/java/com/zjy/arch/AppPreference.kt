package com.zjy.arch

import com.tencent.mmkv.MMKV
import com.zjy.architecture.util.preference.IStorage
import com.zjy.architecture.util.preference.Preference
import com.zjy.architecture.util.preference.PreferenceDelegate
import com.zjy.architecture.util.preference.PreferenceStorage

/**
 * @author zhengjy
 * @since 2020/07/15
 * Description:
 */
object AppPreference : Preference {
    override val sp: IStorage = PreferenceStorage(MMKV.defaultMMKV())

    val isLogin by PreferenceDelegate("IS_LOGIN", false, sp)

}