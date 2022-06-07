package com.zjy.architecture.util.qr

import android.content.ClipData
import android.content.Context
import android.widget.Toast
import com.zjy.architecture.ext.clipboardManager

/**
 * @author zhengjy
 * @since 2022/06/07
 * Description:
 */
interface QRProcessor {

    /**
     * 处理器优先级
     */
    val priority: Int get() = 0

    /**
     * 处理二维码解析结果
     */
    fun process(context: Context, text: String?): Boolean
}

typealias Fallback = (Context, String?) -> Boolean

object QRCodeHelper {

    private val processors = mutableListOf<QRProcessor>()

    private var fallback: Fallback? = { context, result ->
        context.clipboardManager?.setPrimaryClip(ClipData.newPlainText("QRCode", result))
        Toast.makeText(
            context,
            "无法处理结果，已复制到剪贴板",
            Toast.LENGTH_SHORT
        ).show()
        true
    }

    fun registerQRProcessor(processor: QRProcessor) {
        if (!processors.contains(processor)) {
            processors.add(processor)
        }
    }

    fun unregisterQRProcessor(processor: QRProcessor) {
        processors.remove(processor)
    }

    fun process(context: Context, result: String?): Boolean {
        processors.sortedByDescending { it.priority }.forEach {
            try {
                if (it.process(context, result)) return true
            } catch (e: Exception) {
                // 忽略错误，执行下一个处理器
            }
        }
        try {
            return fallback?.invoke(context, result) ?: false
        } catch (e: Exception) {
            context.clipboardManager?.setPrimaryClip(ClipData.newPlainText("QRCode", result))
            Toast.makeText(
                context,
                "无法处理结果，已复制到剪贴板",
                Toast.LENGTH_SHORT
            ).show()
        }
        return false
    }

    fun setFallback(fallback: Fallback) {
        this.fallback = fallback
    }
}