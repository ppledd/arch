package com.zjy.arch

import com.zjy.architecture.data.Result
import com.zjy.architecture.ext.apiCall
import com.zjy.architecture.mvvm.LoadingViewModel
import com.zjy.architecture.mvvm.request
import com.zjy.architecture.net.HttpResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @author zhengjy
 * @since 2020/05/20
 * Description:
 */
class MainViewModel : LoadingViewModel() {

    init {
//        request<Int>(false) {
//            onRequest {
//                repeat(10) {
//                    getInfo()
//                    delay(293)
//                }
//                Result.Success(0)
//            }
//        }
    }

    fun getInfo() {
        request<String> {
            onRequest {
                delay(11835)
                Result.Success("")
            }
        }
    }

    fun getUserInfo() {
        request<String> {
            onFail {

            }
            onComplete {

            }
            onRequest {
                launch {

                }
                withContext(Dispatchers.IO) {

                }
                requestUserInfo()
            }
            onSuccess {

            }
        }
    }

    suspend fun requestUserInfo(): Result<String> {
        return apiCall { HttpResult(0, "", "Any()") }
    }
}