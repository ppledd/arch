package com.zjy.chat.config

/**
 * @author zhengjy
 * @since 2020/07/16
 * Description:连接配置类
 */
interface ServiceProfile {

    /**
     * 客户端版本 放入长连私有协议头部
     */
    fun clientVersion(): Int

    /**
     * 长链接域名
     */
    fun longLinkHost(): String

    /**
     * 长链接端口列表
     */
    fun longLinkPorts(): IntArray

    /**
     * 长链接调试IP.如果有值,则忽略 host设置, 并使用该IP.
     */
    fun longLinkDebugIP(): String? {
        return null
    }

    /**
     * 短链接(HTTP)端口
     */
    fun shortLinkPort(): Int

    /**
     * 短链接调试IP.如果有值,则所有TASK走短链接时,使用该IP代替TASK中的HOST
     */
    fun shortLinkDebugIP(): String? {
        return null
    }
}