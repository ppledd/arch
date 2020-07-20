// MarsService.aidl
package com.zjy.chat.remote;

// Declare any non-default types here with import statements
import com.zjy.chat.remote.MarsTaskWrapper;
import com.zjy.chat.remote.MarsPushMessageFilter;

interface MarsService {

    int send(MarsTaskWrapper taskWrapper);

    void cancel(int taskID);

    void registerPushMessageFilter(MarsPushMessageFilter filter);

    void unregisterPushMessageFilter(MarsPushMessageFilter filter);

    void setAccountInfo(in long uin, in String userName);

    void setForeground(in int isForeground);
}
