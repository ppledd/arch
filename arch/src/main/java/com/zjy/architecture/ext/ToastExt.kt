package com.zjy.architecture.ext

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference

/**
 * @author zhengjy
 * @since 2020/05/21
 * Description:
 */
private var sToast: WeakReference<Toast>? = null

fun Context.toast(@StringRes res: Int, duration: Int = Toast.LENGTH_SHORT, config: (Toast.() -> Unit)? = null) {
    sToast?.get()?.cancel()
    Toast.makeText(this, res, duration).apply {
        config?.invoke(this)
        show()
    }.also {
        sToast = WeakReference(it)
    }
}

fun Context.toast(content: CharSequence, duration: Int = Toast.LENGTH_SHORT, config: (Toast.() -> Unit)? = null) {
    sToast?.get()?.cancel()
    Toast.makeText(this, content, duration).apply {
        config?.invoke(this)
        show()
    }.also {
        sToast = WeakReference(it)
    }
}

fun Fragment.toast(content: CharSequence, duration: Int = Toast.LENGTH_SHORT, config: (Toast.() -> Unit)? = null) {
    val context = context ?: return
    sToast?.get()?.cancel()
    Toast.makeText(context, content, duration).apply {
        config?.invoke(this)
        show()
    }.also {
        sToast = WeakReference(it)
    }
}

fun Fragment.toast(@StringRes res: Int, duration: Int = Toast.LENGTH_SHORT, config: (Toast.() -> Unit)? = null) {
    val context = context ?: return
    sToast?.get()?.cancel()
    Toast.makeText(context, res, duration).apply {
        config?.invoke(this)
        show()
    }.also {
        sToast = WeakReference(it)
    }
}

fun Fragment.cancelToast() {
    sToast?.get()?.cancel()
    sToast = null
}

fun Context.cancelToast() {
    sToast?.get()?.cancel()
    sToast = null
}