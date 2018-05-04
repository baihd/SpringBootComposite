package com.composite.utils.originateZKUtils.Asynchronous;

import org.apache.zookeeper.AsyncCallback;

public class IVoidCallBack implements AsyncCallback.VoidCallback {
    @Override
    public void processResult(int rc, String path, Object ctx) {
        System.out.println(rc + ", " + path + ", " + ctx);

    }
}
