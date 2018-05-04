package com.composite.utils.originateZKUtils.Asynchronous;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.data.Stat;

public class IDataCallBack implements AsyncCallback.DataCallback {
    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        System.out.println("rc: " + rc + ", path: " + path + ", data: " + new String(data));
        System.out.println("czxID: " + stat.getCzxid() + ", mzxID: " + stat.getMzxid() + ", version: " + stat.getVersion());
    }
}
