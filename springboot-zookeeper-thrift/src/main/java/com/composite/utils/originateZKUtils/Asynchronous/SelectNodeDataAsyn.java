package com.composite.utils.originateZKUtils.Asynchronous;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

public class SelectNodeDataAsyn implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    String path = "/zk-book";
    public void selectNodeData(){
        try{
            ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, new SelectNodeDataAsyn());
            connectedSemaphore.await();

            zooKeeper.create(path, "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("success create znode: " + path);

            zooKeeper.getData(path, true, new IDataCallBack(), null);

            zooKeeper.setData(path, "123".getBytes(), -1);
        }catch (Exception e){

        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            if (Event.EventType.None == watchedEvent.getType() && null == watchedEvent.getPath()) {
                connectedSemaphore.countDown();
            } else if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
                try {
                    ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, new SelectNodeDataAsyn());
                    zooKeeper.getData(watchedEvent.getPath(), true, new IDataCallBack(), null);
                } catch (Exception e) {
                }
            }
        }
    }
}
