package com.zjy.chat

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author zhengjy
 * @since 2020/07/16
 * Description:
 */
@Parcelize
class PushMessage(
        val cmdId: Int,
        val buffer: ByteArray
) : Parcelable