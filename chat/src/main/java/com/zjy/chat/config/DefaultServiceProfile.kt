package com.zjy.chat.config

/**
 * @author zhengjy
 * @since 2020/07/16
 * Description:服务端配置空实现
 */
class DefaultServiceProfile : ServiceProfile {

    override fun clientVersion(): Int {
        return 0
    }

    override fun longLinkHost(): String {
        return "mars.cn"
    }

    override fun longLinkPorts(): IntArray {
        return intArrayOf()
    }

    override fun shortLinkPort(): Int {
        return 0
    }
}