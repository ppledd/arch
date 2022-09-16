package com.zjy.arch.bean

import java.io.Serializable

/**
 * @author zhengjy
 * @since 2022/09/15
 * Description:
 */
data class User(
    val name: String,
    val age: Int
) : Serializable
