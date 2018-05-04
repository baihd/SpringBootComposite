package com.composite.utils.originateZKUtils.Asynchronous;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.data.Stat;

public class IStatCallBack implements AsyncCallback.StatCallback {
    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        System.out.println("rc: " + rc + ", path: " + path + ", stat: " + stat);
    }
}
