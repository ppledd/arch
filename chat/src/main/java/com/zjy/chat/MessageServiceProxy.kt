package com.zjy.chat

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import com.tencent.mars.app.AppLogic.AccountInfo
import com.tencent.mars.xlog.Log
import com.zjy.architecture.ext.tryWith
import com.zjy.chat.config.ChatServiceProfileFactory
import com.zjy.chat.config.ServiceProfile
import com.zjy.chat.remote.MarsPushMessageFilter
import com.zjy.chat.remote.MarsService
import com.zjy.chat.remote.MarsTaskWrapper
import com.zjy.chat.task.TaskProperty
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue

/**
 * @author zhengjy
 * @since 2020/07/16
 * Description:
 */
class MessageServiceProxy : ServiceConnection {

    companion object {
        /**
         * cmdId存储Map
         */
        val GLOBAL_CMD_ID_MAP = ConcurrentHashMap<String, Int>()
        /**
         * 消息处理器Map
         */
        val HANDLER_MAP = ConcurrentHashMap<Int, PushMessageHandler>()
        /**
         * 登录用户信息
         */
        var accountInfo: AccountInfo = AccountInfo()

        private var instance: MessageServiceProxy? = null
        private const val TAG = "MessageManager"
        private var gContext: Context? = null
        private var gPackageName: String? = null
        private var gClassName: String? = null
        private const val SERVICE_DEFAULT_CLASSNAME = "com.zjy.chat.MessageService"

        @JvmStatic
        fun init(context: Context, packageName: String? = null, provider: (() -> ServiceProfile)? = null) {
            gContext = context.applicationContext
            gPackageName = packageName ?: context.packageName
            gClassName = SERVICE_DEFAULT_CLASSNAME
            provider?.also {
                MessageService.factory = ChatServiceProfileFactory(it)
            }

            instance = MessageServiceProxy()
        }

        @JvmStatic
        fun send(taskWrapper: MarsTaskWrapper) {
            instance?.queue?.offer(taskWrapper)
        }

        @JvmStatic
        fun cancel(taskWrapper: MarsTaskWrapper) {
            instance?.cancelSpecifiedTaskWrapper(taskWrapper)
        }

        /**
         * 前后台切换
         */
        fun setForeground(isForeground: Boolean) = checkService {
            instance?.service?.setForeground(if (isForeground) 1 else 0)
        }

        /**
         * 重置[MessageServiceProxy]，清空所有数据和队列
         */
        fun reset() {
            accountInfo = AccountInfo()
            GLOBAL_CMD_ID_MAP.clear()
            HANDLER_MAP.clear()
            instance?.queue?.clear()
        }

        /**
         * 检查服务是否正在运行，如果在运行，则进行指定操作
         */
        private fun checkService(block: (() -> Unit)? = null) = tryWith {
            if (instance?.service == null) {
                Log.d(TAG, "try to bind remote mars service, packageName: %s, className: %s", gPackageName, gClassName)
                val i: Intent = Intent().setClassName(gPackageName!!, gClassName!!)
                gContext?.startService(i)
                if (!gContext!!.bindService(i, instance!!, Service.BIND_AUTO_CREATE)) {
                    Log.e(TAG, "remote mars service bind failed")
                    return@tryWith
                }
            }
            block?.invoke()
        }
    }

    private val worker: Worker
    private var service: MarsService? = null

    /**
     * 任务队列
     */
    private val queue: LinkedBlockingQueue<MarsTaskWrapper> = LinkedBlockingQueue()

    private val filter = object : MarsPushMessageFilter.Stub() {
        override fun onReceive(cmdId: Int, buffer: ByteArray): Boolean {
            val handler = HANDLER_MAP[cmdId]
            if (handler != null) {
                Log.i(TAG, "processing push message, cmdid = %d", cmdId)
                val message = PushMessage(cmdId, buffer)
                handler.process(message)
                return true
            } else {
                Log.i(TAG, "no push message listener set for cmdid = %d, just ignored", cmdId)
            }
            return false
        }
    }

    init {
        worker = Worker()
        worker.start()
    }

    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        Log.d(TAG, "remote mars service connected")
        try {
            service = MarsService.Stub.asInterface(binder)
            service?.registerPushMessageFilter(filter)
            service?.setAccountInfo(accountInfo.uin, accountInfo.userName)

        } catch (e: Exception) {
            service = null
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        tryWith { service?.unregisterPushMessageFilter(filter) }
        service = null
        Log.d(TAG, "remote mars service disconnected")
    }

    /**
     * 取消指定的任务
     */
    private fun cancelSpecifiedTaskWrapper(marsTaskWrapper: MarsTaskWrapper) {
        if (queue.remove(marsTaskWrapper)) {
            // Remove from queue, not exec yet, call MarsTaskWrapper::onTaskEnd
            try {
                marsTaskWrapper.onTaskEnd(-1, -1)
            } catch (e: RemoteException) {
                // Called in client, ignore RemoteException
                e.printStackTrace()
                Log.e(TAG, "cancel mars task wrapper in client, should not catch RemoteException")
            }
        } else {
            // Already sent to remote service, need to cancel it
            try {
                service?.cancel(marsTaskWrapper.properties.getInt(TaskProperty.OPTIONS_TASK_ID))
            } catch (e: RemoteException) {
                e.printStackTrace()
                Log.w(TAG, "cancel mars task wrapper in remote service failed, I'll make marsTaskWrapper.onTaskEnd")
            }
        }
    }

    /**
     * 从消息队列中取出任务，发送给[MarsService]
     */
    private fun continueProcessTaskWrappers() = checkService {
        val taskWrapper = queue.take() ?: return@checkService
        Log.d(TAG, "sending task = %s", taskWrapper)
        val cgiPath = taskWrapper.properties.getString(TaskProperty.OPTIONS_CGI_PATH)
        if (cgiPath != null) {
            val globalCmdID = GLOBAL_CMD_ID_MAP[cgiPath]
            if (globalCmdID != null) {
                taskWrapper.properties.putInt(TaskProperty.OPTIONS_CMD_ID, globalCmdID)
                Log.i(TAG, "overwrite cmdID with global cmdID Map: %s -> %d", cgiPath, globalCmdID)
            }
        }

        val taskID = service?.send(taskWrapper) ?: -1
        // NOTE: Save taskID to taskWrapper here
        taskWrapper.properties.putInt(TaskProperty.OPTIONS_CMD_ID, taskID)
    }

    private inner class Worker : Thread() {
        override fun run() {
            while (true) {
                instance?.continueProcessTaskWrappers()
                try {
                    sleep(20)
                } catch (e: InterruptedException) {
                    Log.e(TAG, "", e)
                }
            }
        }
    }

}