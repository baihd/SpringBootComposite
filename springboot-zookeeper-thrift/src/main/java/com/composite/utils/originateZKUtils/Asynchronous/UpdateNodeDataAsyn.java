package com.composite.utils.originateZKUtils.Asynchronous;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * 异步更新节点数据
 */
public class UpdateNodeDataAsyn implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    String path = "/zk-book";

    public void updateNodeData(){
        try {
            ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 5000, new UpdateNodeDataAsyn());
            connectedSemaphore.await();
            zk.create(path, "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("success create znode: " + path);
            zk.setData(path, "456".getBytes(), -1, new IStatCallBack(), null);
        }catch (Exception e){

        }
    }
    @Override
    public void process(WatchedEvent watchedEvent) {
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            if (Event.EventType.None == watchedEvent.getType() && null == watchedEvent.getPath()) {
                connectedSemaphore.countDown();
            }
        }
    }
}
