package com.zjy.architecture.di

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.GlobalContext
import org.koin.core.scope.Scope

/**
 * @author zhengjy
 * @since 2020/08/14
 * Description:
 */

/**
 * 获取rootScope
 */
@OptIn(KoinInternalApi::class)
val rootScope: Scope
    get() = GlobalContext.get().scopeRegistry.rootScope