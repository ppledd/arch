package com.zjy.chat.task

import android.os.Bundle
import com.zjy.chat.remote.MarsTaskWrapper

/**
 * @author zhengjy
 * @since 2020/07/16
 * Description:抽象任务包装类
 */
abstract class AbstractTaskWrapper : MarsTaskWrapper.Stub() {

    private val properties = Bundle()

    init {
        // Reflects task properties
        val taskProperty = this.javaClass.getAnnotation(TaskConfig::class.java)
        if (taskProperty != null) {
            setHttpRequest(taskProperty.host, taskProperty.path)
            setShortChannelSupport(taskProperty.shortChannelSupport)
            setLongChannelSupport(taskProperty.longChannelSupport)
            setCmdID(taskProperty.cmdID)
        }
    }

    override fun getProperties(): Bundle {
        return properties
    }

    abstract override fun onTaskEnd(errType: Int, errCode: Int)

    fun setHttpRequest(host: String, path: String?): AbstractTaskWrapper {
        properties.putString(TaskProperty.OPTIONS_HOST, if ("" == host) null else host)
        properties.putString(TaskProperty.OPTIONS_CGI_PATH, path)
        return this
    }

    fun setShortChannelSupport(support: Boolean): AbstractTaskWrapper {
        properties.putBoolean(TaskProperty.OPTIONS_CHANNEL_SHORT_SUPPORT, support)
        return this
    }

    fun setLongChannelSupport(support: Boolean): AbstractTaskWrapper {
        properties.putBoolean(TaskProperty.OPTIONS_CHANNEL_LONG_SUPPORT, support)
        return this
    }

    fun setCmdID(cmdID: Int): AbstractTaskWrapper {
        properties.putInt(TaskProperty.OPTIONS_CMD_ID, cmdID)
        return this
    }

    override fun toString(): String {
        return "AbsMarsTask: " + format(properties)
    }

    private fun format(bundle: Bundle): String {
        val sb = StringBuilder("{ ")
        val keys = bundle.keySet()
        for (k in keys) {
            val obj = bundle[k]
            if (obj is Bundle) {
                sb.append(format(obj))
            } else {
                sb.append(k).append("=").append(obj).append("; ")
            }
        }
        return sb.append("}").toString()
    }
}