package com.composite.utils.originateZKUtils.Asynchronous;

import org.apache.zookeeper.AsyncCallback;

public class IStringCallBack implements AsyncCallback.StringCallback {
    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        System.out.println("Create path result: [" + rc + ", " + path + ", " + ctx + ", real path name: " + name);
    }
}
