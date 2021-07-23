package com.zjy.architecture.ext

import android.annotation.SuppressLint
import com.zjy.architecture.util.logV
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author zhengjy
 * @since 2020/07/16
 * Description:
 */
inline fun <reified T, R> T.tryWith(crossinline block: () -> R): R? {
    return try {
        block()
    } catch (e: Exception) {
        logV(T::class.java.name, e.message)
        null
    }
}

@SuppressLint("SimpleDateFormat")
fun Long.format(format: String, locale: Locale? = null): String {
    return if (locale == null) {
        SimpleDateFormat(format).format(this)
    } else {
        SimpleDateFormat(format, locale).format(this)
    }
}

fun Long.formatDuration(showHours: Boolean = false): String {
    val hourStr = when (val hour = TimeUnit.SECONDS.toHours(this)) {
        0L -> if (showHours) "00:" else ""
        in 1..9 -> "0$hour:"
        else -> "$hour:"
    }

    val minStr = when (val min = TimeUnit.SECONDS.toMinutes(this) % 60) {
        0L -> "00"
        in 1..9 -> "0$min"
        else -> min.toString()
    }

    val secStr = when (val sec = TimeUnit.SECONDS.toSeconds(this) % 60) {
        0L -> "00"
        in 1..9 -> "0$sec"
        else -> sec.toString()
    }
    return "${hourStr}${minStr}:${secStr}"
}

fun Int.formatDuration(showHours: Boolean = false): String {
    return this.toLong().formatDuration(showHours)
}

/**
 * 字符串截取小数点后number位
 * 末位不足，则补全 0
 *
 * @param count
 * @return
 */
fun String.checkString(count: Int): String {
    var result = this
    if (this.contains(".")) {
        val pointLength = length - 1 - indexOf(".")
        when {
            count == 0 -> result = substring(0, indexOf("."))
            pointLength >= count -> result = substring(0, indexOf(".") + count + 1)
            pointLength < count -> {
                val add = count - pointLength
                val builder = StringBuilder(this)
                for (i in 0 until add) {
                    builder.append("0")
                }
                result = builder.toString()
            }
        }
    } else {
        if (count > 0) {
            val builder = StringBuilder(this)
            builder.append(".")
            for (i in 0 until count) {
                builder.append("0")
            }
            result = builder.toString()
        }
    }
    return result
}