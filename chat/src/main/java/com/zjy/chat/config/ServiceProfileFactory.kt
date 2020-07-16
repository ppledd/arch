package com.zjy.chat.config

/**
 * @author zhengjy
 * @since 2020/07/16
 * Description:
 */
interface ServiceProfileFactory {

    fun createServiceProfile(): ServiceProfile
}