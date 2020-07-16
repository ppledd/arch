package com.zjy.chat

/**
 * @author zhengjy
 * @since 2020/07/16
 * Description:
 */
interface PushMessageHandler {

    fun process(message: PushMessage)
}