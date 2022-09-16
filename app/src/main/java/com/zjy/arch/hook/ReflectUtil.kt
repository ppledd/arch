package com.zjy.arch.hook


@Throws(SecurityException::class, IllegalArgumentException::class, IllegalAccessException::class)
fun Any?.setField(clazz: Class<*>, name: String, value: Any?) {
    val field = clazz.getDeclaredField(name)
    field.isAccessible = true
    field.set(this, value)
}

@Throws(SecurityException::class, IllegalArgumentException::class, IllegalAccessException::class)
fun Any?.getField(clazz: Class<*>, name: String): Any? {
    val field = clazz.getDeclaredField(name)
    field.isAccessible = true
    return field.get(this)
}

@Throws(SecurityException::class, IllegalArgumentException::class, IllegalAccessException::class)
fun <T> Any?.getFieldT(clazz: Class<*>, name: String): T? {
    val field = clazz.getDeclaredField(name)
    field.isAccessible = true
    return field.get(this) as T?
}