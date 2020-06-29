package com.zjy.arch

/**
 * @author zhengjy
 * @since 2020/05/29
 * Description:
 */
data class DepInfo(
    val avatar: String,
    val code: String,
    val departments: List<Department>,
    val entryTime: Long,
    val phone: String,
    val position: String,
    val realName: String,
    val role: Int,
    val userId: String
)

data class Department(
    val containSub: Boolean,
    val depId: String,
    val master: String,
    val name: String,
    val parent: String,
    val type: Int
)