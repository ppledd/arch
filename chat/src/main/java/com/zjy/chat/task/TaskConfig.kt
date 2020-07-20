package com.zjy.chat.task

import java.lang.annotation.Inherited

/**
 * @author zhengjy
 * @since 2020/07/16
 * Description:
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Inherited
annotation class TaskConfig(
        val host: String = "",
        val path: String = "",
        val shortChannelSupport: Boolean = true,
        val longChannelSupport: Boolean = false,
        val cmdID: Int = -1
)