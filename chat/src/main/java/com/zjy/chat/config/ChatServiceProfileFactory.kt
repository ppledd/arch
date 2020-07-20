package com.zjy.chat.config

import com.tencent.mars.xlog.Log

/**
 * @author zhengjy
 * @since 2020/07/16
 * Description:
 */
class ChatServiceProfileFactory(
        private val provider: () -> ServiceProfile
) : ServiceProfileFactory {

    override fun createServiceProfile(): ServiceProfile {
        return try {
            provider.invoke()
        } catch (e: Exception) {
            Log.e("ServerProfileFactory", "", e)
            DefaultServiceProfile()
        }
    }
}