package com.zjy.chat

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.tencent.mars.Mars
import com.tencent.mars.app.AppLogic
import com.tencent.mars.sdt.SdtLogic
import com.tencent.mars.stn.StnLogic
import com.tencent.mars.xlog.Log
import com.zjy.chat.config.ChatServiceProfileFactory
import com.zjy.chat.config.DefaultServiceProfile
import com.zjy.chat.remote.MarsPushMessageFilter
import com.zjy.chat.remote.MarsService
import com.zjy.chat.remote.MarsTaskWrapper

/**
 * @author zhengjy
 * @since 2020/07/16
 * Description:
 */
class MessageService : Service(), MarsService {

    companion object {
        const val TAG = "Mars.MessageServiceNative"

        var factory = ChatServiceProfileFactory {
            DefaultServiceProfile()
        }
    }

    private lateinit var stub: MarsServiceStub

    override fun onCreate() {
        val profile = factory.createServiceProfile()
        stub = MarsServiceStub(applicationContext, profile)
        // set callback
        AppLogic.setCallBack(stub)
        StnLogic.setCallBack(stub)
        SdtLogic.setCallBack(stub)

        // Initialize the Mars PlatformComm
        Mars.init(applicationContext, Handler(Looper.getMainLooper()))

        // Initialize the Mars
        StnLogic.setLonglinkSvrAddr(profile.longLinkHost(), profile.longLinkPorts())
        StnLogic.setShortlinkSvrAddr(profile.shortLinkPort())
        StnLogic.setClientVersion(profile.clientVersion())
        Mars.onCreate(true)

        StnLogic.makesureLongLinkConnected()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return stub
    }

    override fun asBinder(): IBinder {
        return stub
    }

    override fun unregisterPushMessageFilter(filter: MarsPushMessageFilter?) {
        stub.unregisterPushMessageFilter(filter)
    }

    override fun registerPushMessageFilter(filter: MarsPushMessageFilter?) {
        stub.registerPushMessageFilter(filter)
    }

    override fun setAccountInfo(uin: Long, userName: String?) {
        stub.setAccountInfo(uin, userName)
    }

    override fun setForeground(isForeground: Int) {
        stub.setForeground(isForeground)
    }

    override fun send(taskWrapper: MarsTaskWrapper): Int {
        return stub.send(taskWrapper)
    }

    override fun cancel(taskID: Int) {
        stub.cancel(taskID)
    }

    override fun onDestroy() {
        Log.d(TAG, "message service native destroying")
        Mars.onDestroy()
        Log.d(TAG, "message service native destroyed")
        super.onDestroy()
    }
}