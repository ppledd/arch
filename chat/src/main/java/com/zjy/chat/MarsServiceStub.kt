package com.zjy.chat

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.RemoteException
import com.tencent.mars.BaseEvent
import com.tencent.mars.app.AppLogic
import com.tencent.mars.sdt.SdtLogic
import com.tencent.mars.stn.StnLogic
import com.tencent.mars.xlog.Log
import com.zjy.chat.config.ServiceProfile
import com.zjy.chat.remote.MarsPushMessageFilter
import com.zjy.chat.remote.MarsService
import com.zjy.chat.remote.MarsTaskWrapper
import com.zjy.chat.task.TaskProperty
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * @author zhengjy
 * @since 2020/07/16
 * Description:
 */
class MarsServiceStub(
        private val context: Context,
        private val profile: ServiceProfile
) : MarsService.Stub(), StnLogic.ICallBack, SdtLogic.ICallBack, AppLogic.ICallBack {

    companion object {
        private const val TAG = "Mars.MarsServiceStub"
        private val DEVICE_NAME = "${Build.MANUFACTURER}-${Build.MODEL}"
        private val DEVICE_TYPE = "android-${Build.VERSION.SDK_INT}"
        private val TASK_ID_TO_WRAPPER = ConcurrentHashMap<Int, MarsTaskWrapper>()
    }

    private val deviceInfo = AppLogic.DeviceInfo(DEVICE_NAME, DEVICE_TYPE)
    private val accountInfo = AppLogic.AccountInfo()

    private val filters = ConcurrentLinkedQueue<MarsPushMessageFilter>()

    override fun send(taskWrapper: MarsTaskWrapper, taskProperties: Bundle): Int {
        val _task = StnLogic.Task(StnLogic.Task.EShort, 0, "", ArrayList())

        // Set host & cgi path
        val host = taskProperties.getString(TaskProperty.OPTIONS_HOST)
        val cgiPath = taskProperties.getString(TaskProperty.OPTIONS_CGI_PATH)
        _task.shortLinkHostList.add(host)
        _task.cgi = cgiPath

        val shortSupport = taskProperties.getBoolean(TaskProperty.OPTIONS_CHANNEL_SHORT_SUPPORT, true)
        val longSupport = taskProperties.getBoolean(TaskProperty.OPTIONS_CHANNEL_LONG_SUPPORT, false)
        if (shortSupport && longSupport) {
            _task.channelSelect = StnLogic.Task.EBoth
        } else if (shortSupport) {
            _task.channelSelect = StnLogic.Task.EShort
        } else if (longSupport) {
            _task.channelSelect = StnLogic.Task.ELong
        } else {
            Log.e(TAG, "invalid channel strategy")
            throw RemoteException("Invalid Channel Strategy")
        }

        // Set cmdID if necessary
        val cmdID = taskProperties.getInt(TaskProperty.OPTIONS_CMD_ID, -1)
        if (cmdID != -1) {
            _task.cmdID = cmdID
        }

        TASK_ID_TO_WRAPPER[_task.taskID] = taskWrapper

        // Send
        Log.i(TAG, "now start task with id %d", _task.taskID)
        StnLogic.startTask(_task)
        if (StnLogic.hasTask(_task.taskID)) {
            Log.i(TAG, "stn task started with id %d", _task.taskID)
        } else {
            Log.e(TAG, "stn task start failed with id %d", _task.taskID)
        }

        return _task.taskID
    }

    override fun cancel(taskID: Int) {
        Log.d(TAG, "cancel wrapper with taskID=%d using stn stop", taskID)
        StnLogic.stopTask(taskID)
        TASK_ID_TO_WRAPPER.remove(taskID) // TODO: check return
    }

    override fun registerPushMessageFilter(filter: MarsPushMessageFilter?) {
        filters.remove(filter)
        filters.add(filter)
    }

    override fun unregisterPushMessageFilter(filter: MarsPushMessageFilter?) {
        filters.remove(filter)
    }

    override fun setAccountInfo(uin: Long, userName: String?) {
        accountInfo.uin = uin
        accountInfo.userName = userName
    }

    override fun setForeground(isForeground: Int) {
        BaseEvent.onForeground(isForeground == 1)
    }

    override fun makesureAuthed(host: String?): Boolean {
        //
        // Allow you to block all tasks which need to be sent before certain 'AUTHENTICATED' actions
        // Usually we use this to exchange encryption keys, sessions, etc.
        //
        return true
    }

    override fun onNewDns(host: String?): Array<String>? {
        // No default new dns support
        return null
    }

    override fun onPush(cmdid: Int, data: ByteArray?) {
        for (filter in filters) {
            try {
                if (filter.onReceive(cmdid, data)) {
                    break
                }
            } catch (e: RemoteException) {
                Log.e(TAG, "", e)
            }
        }
    }

    override fun trafficData(send: Int, recv: Int) {
        // onPush(BaseConstants.FLOW_CMDID, String.format("%d,%d", send, recv).getBytes(Charset.forName("UTF-8")));
    }

    override fun reportConnectInfo(status: Int, longlinkstatus: Int) {

    }

    override fun getLongLinkIdentifyCheckBuffer(identifyReqBuf: ByteArrayOutputStream?, hashCodeBuffer: ByteArrayOutputStream?, reqRespCmdID: IntArray?): Int {
        // Send identify request buf to server
        // identifyReqBuf.write();

        return StnLogic.ECHECK_NEVER
    }

    override fun onLongLinkIdentifyResp(buffer: ByteArray?, hashCodeBuffer: ByteArray?): Boolean {
        return false
    }

    override fun requestDoSync() {

    }

    override fun requestNetCheckShortLinkHosts(): Array<String> {
        return arrayOf()
    }

    override fun isLogoned(): Boolean {
        return false
    }

    override fun onTaskEnd(taskID: Int, userContext: Any?, errType: Int, errCode: Int): Int {
        val wrapper = TASK_ID_TO_WRAPPER.remove(taskID)
        if (wrapper == null) {
            Log.w(TAG, "stn task onTaskEnd callback may fail, null wrapper, taskID=%d", taskID)
            return 0
        }

        try {
            wrapper.onTaskEnd(errType, errCode)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

        return 0
    }

    override fun req2Buf(taskID: Int, userContext: Any?, reqBuffer: ByteArrayOutputStream?, errCode: IntArray?, channelSelect: Int, host: String?): Boolean {
        val wrapper = TASK_ID_TO_WRAPPER[taskID]
        if (wrapper == null) {
            Log.e(TAG, "invalid req2Buf for task, taskID=%d", taskID)
            return false
        }

        try {
            reqBuffer?.write(wrapper.req2buf())
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, "task wrapper req2buf failed for short, check your encode process")
        } catch (e: RemoteException) {
            e.printStackTrace()
            Log.e(TAG, "task wrapper req2buf failed for short, check your encode process")
        }

        return false
    }

    override fun buf2Resp(taskID: Int, userContext: Any?, respBuffer: ByteArray?, errCode: IntArray?, channelSelect: Int): Int {
        val wrapper = TASK_ID_TO_WRAPPER[taskID]
        if (wrapper == null) {
            Log.e(TAG, "buf2Resp: wrapper not found for stn task, taskID=%", taskID)
            return StnLogic.RESP_FAIL_HANDLE_TASK_END
        }

        try {
            return wrapper.buf2resp(respBuffer)
        } catch (e: RemoteException) {
            Log.e(TAG, "remote wrapper disconnected, clean this context, taskID=%d", taskID)
            TASK_ID_TO_WRAPPER.remove(taskID)
        }
        return StnLogic.RESP_FAIL_HANDLE_TASK_END
    }

    override fun reportTaskProfile(taskString: String?) {
        // onPush(BaseConstants.CGIHISTORY_CMDID, reportString.getBytes(Charset.forName("UTF-8")));
    }

    override fun reportSignalDetectResults(resultsJson: String?) {
        // onPush(BaseConstants.SDTRESULT_CMDID, reportString.getBytes(Charset.forName("UTF-8")));
    }

    override fun getAppFilePath(): String? {
        try {
            val file = context.filesDir
            if (!file.exists()) {
                file.createNewFile()
            }
            return file.toString()
        } catch (e: Exception) {
            Log.e(TAG, "", e)
        }

        return null
    }

    override fun getAccountInfo(): AppLogic.AccountInfo {
        return accountInfo
    }

    override fun getClientVersion(): Int {
        return profile.clientVersion()
    }

    override fun getDeviceType(): AppLogic.DeviceInfo {
        return deviceInfo
    }
}