package com.zjy.architecture.ext

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

/**
 * @author zhengjy
 * @since 2020/05/18
 * Description:ImageView扩展函数
 */
@Deprecated("使用load")
fun ImageView.load0(
    url: String?,
    placeholder: Int = 0,
    block: RequestBuilder<Drawable>.(Int) -> RequestBuilder<Drawable> = DEFAULT_APPLY
) {
    Glide.with(this).load(url).let {
        block.invoke(it, placeholder)
    }.into(this)
}

@Deprecated("使用load")
fun ImageView.load0(
    uri: Uri?,
    placeholder: Int = 0,
    block: RequestBuilder<Drawable>.(Int) -> RequestBuilder<Drawable> = DEFAULT_APPLY
) {
    Glide.with(this).load(uri).let {
        block.invoke(it, placeholder)
    }.into(this)
}

private val DEFAULT_APPLY: RequestBuilder<Drawable>.(Int) -> RequestBuilder<Drawable> = {
    if (it == 0) {
        this
    } else {
        apply(RequestOptions().placeholder(it).error(it))
    }
}

fun ImageView.load(
    uri: String?,
    placeholder: Int = 0,
    crossFade: Boolean = false,
    block: RequestOptions.() -> RequestOptions
) {
    Glide.with(this).load(uri).let {
        if (crossFade) it.transition(DrawableTransitionOptions.withCrossFade()) else it
    }.apply(RequestOptions().placeholder(placeholder).block()).into(this)
}

fun ImageView.load(
    uri: Uri?,
    placeholder: Int = 0,
    crossFade: Boolean = false,
    block: RequestOptions.() -> RequestOptions
) {
    Glide.with(this).load(uri).let {
        if (crossFade) it.transition(DrawableTransitionOptions.withCrossFade()) else it
    }.apply(RequestOptions().placeholder(placeholder).block()).into(this)
}
