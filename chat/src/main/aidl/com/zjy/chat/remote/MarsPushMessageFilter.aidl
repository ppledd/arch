// MarsRecvCallBack.aidl
package com.zjy.chat.remote;

// Declare any non-default types here with import statements

interface MarsPushMessageFilter {

    // returns processed ?
    boolean onReceive(int cmdId, inout byte[] buffer);

}
