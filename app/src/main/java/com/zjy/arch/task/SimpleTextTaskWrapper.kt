package com.zjy.arch.task

import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.tencent.mars.stn.StnLogic
import com.zjy.chat.data.proto.Main
import com.zjy.chat.task.AbstractTaskWrapper
import com.zjy.chat.task.TaskConfig

/**
 * @author zhengjy
 * @since 2020/07/17
 * Description:
 */
@TaskConfig(cmdID = Main.CmdID.CMD_ID_SEND_MESSAGE_VALUE)
class SimpleTextTaskWrapper(
    private val text: String
) : AbstractTaskWrapper() {

    private val request: JsonObject = JsonObject()
    private var response: JsonObject = JsonObject()

    override fun onTaskEnd(errType: Int, errCode: Int) {

        Log.i("SimpleTextTaskWrapper", "任务结束[${text}], errType:${errType}, errCode:${errCode}")
    }

    override fun buf2resp(buf: ByteArray?): Int {
        try {
            if (buf != null) {
                response = JsonParser.parseString(String(buf, Charsets.UTF_8)).asJsonObject
                return StnLogic.RESP_FAIL_HANDLE_NORMAL
            }
        } catch (e: Exception) {
            Log.e("SimpleTextTaskWrapper", "", e)
        }
        return StnLogic.RESP_FAIL_HANDLE_TASK_END
    }

    override fun req2buf(): ByteArray {
        request.addProperty("text", text)
        return request.toString().toByteArray()
    }

}