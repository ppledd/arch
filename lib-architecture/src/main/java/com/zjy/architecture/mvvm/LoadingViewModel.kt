package com.zjy.architecture.mvvm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import com.zjy.architecture.data.Result
import com.zjy.architecture.ext.handleException
import kotlinx.coroutines.CoroutineScope
import java.io.Serializable

/**
 * @author zhengjy
 * @since 2019/08/15
 * Description:带加载框状态的ViewModel
 */
open class LoadingViewModel : LifecycleViewModel() {

    private val _loading: MutableLiveData<Loading> by lazy { MutableLiveData<Loading>() }
    val loading: LiveData<Loading> = _loading

    /**
     * 加载框引用计数
     */
    private var count = 0

    private fun loading(cancelable: Boolean) {
        if (count++ <= 0) {
            _loading.value = Loading(true, cancelable)
        }
    }

    private fun dismiss() {
        if (--count <= 0) {
            count = 0
            _loading.value = Loading(false)
        }
    }

    fun <T> CoroutineScope.request(
        loading: Boolean = true,
        cancelable: Boolean = true,
        block: RequestDSL<T>.() -> Unit
    ) {
        object : RequestDSL<T>() {
            override fun build() {
                launch {
                    try {
                        if (loading) {
                            loading(cancelable)
                        }
                        onStart?.invoke()
                        onRequest?.invoke(this)?.apply {
                            if (isSucceed()) {
                                onSuccess?.invoke(data())
                            } else {
                                processError(onFail, error())
                            }
                        }
                    } catch (e: Exception) {
                        if (e is CancellationException) {
                            Log.e("LoadingViewModel", "Exception: ${e.message}")
                        } else {
                            processError(onFail, handleException(e))
                        }
                    } finally {
                        onComplete?.invoke()
                        if (loading) {
                            dismiss()
                        }
                    }
                }
            }
        }.apply(block).build()
    }

    private fun processError(onError: ((Throwable) -> Unit)? = null, e: Throwable) {
        onError?.invoke(e)
    }
}

abstract class RequestDSL<T> {

    var onStart: (() -> Unit)? = null
    var onRequest: (suspend CoroutineScope.() -> Result<T>)? = null
    var onSuccess: ((T) -> Unit)? = null
    var onFail: ((Throwable) -> Unit)? = null
    var onComplete: (() -> Unit)? = null

    fun onStart(block: () -> Unit) {
        this.onStart = block
    }

    fun onRequest(block: suspend CoroutineScope.() -> Result<T>) {
        this.onRequest = block
    }

    fun onSuccess(block: (T) -> Unit) {
        this.onSuccess = block
    }

    fun onFail(block: (Throwable) -> Unit) {
        this.onFail = block
    }

    fun onComplete(block: () -> Unit) {
        this.onComplete = block
    }

    abstract fun build()
}

data class Loading(
    /**
     * 是否正在显示
     */
    var loading: Boolean = true,
    /**
     * 加载框是否可取消
     */
    var cancelable: Boolean = true
) : Serializable