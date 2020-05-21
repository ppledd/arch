package com.zjy.architecture.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.zjy.architecture.Arch
import kotlin.reflect.KProperty

/**
 * @author zhengjy
 * @since 2020/01/13
 * Description:Preference存储代理类
 */
class PreferenceDelegate<T>(
    private val key: String,
    private val value: T,
    private val sp: IStorage = PreferenceSingleton.instance
) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return sp.getValue(key, value)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        sp.putValue(key, value)
    }
}

/**
 * 通用存储接口
 */
interface IStorage {

    fun <T> getValue(name: String, default: T): T

    fun <T> putValue(name: String, value: T)
}

/**
 * SharedPreferences存储
 *
 * @param sp    SharedPreferences
 */
class Preferences(private val sp: SharedPreferences) : IStorage {

    override fun <T> getValue(name: String, default: T): T = with(sp) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default) ?: ""
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw java.lang.IllegalArgumentException()
        }
        @Suppress("UNCHECKED_CAST")
        res as T
    }

    override fun <T> putValue(name: String, value: T) = sp.edit {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type can't be saved into Preferences")
        }
    }
}

/**
 * 应用默认的SharedPreferences
 */
private class PreferenceSingleton private constructor(
    private val preferences: Preferences
) : IStorage by preferences {

    companion object {

        val instance by lazy {
            val shared = Arch.context.getSharedPreferences(
                "${Arch.context.packageName}.sharedpreference",
                Context.MODE_PRIVATE
            )
            PreferenceSingleton(Preferences(shared))
        }
    }
}