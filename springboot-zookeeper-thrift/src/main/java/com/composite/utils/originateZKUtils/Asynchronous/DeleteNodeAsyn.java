package com.composite.utils.originateZKUtils.Asynchronous;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * 异步删除节点
 */
public class DeleteNodeAsyn implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    String path = "/zk-book";

    public void deleteNode() {
        try {
            ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, new CreateNodeAsyn());
            connectedSemaphore.await();
            zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("success create znode: " + path);
            zooKeeper.create(path + "/c1", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("success create znode: " + path + "/c1");

            zooKeeper.delete(path, -1, new IVoidCallBack(), null);
            zooKeeper.delete(path + "/c1", -1, new IVoidCallBack(), null);
            zooKeeper.delete(path, -1, new IVoidCallBack(), null);

        } catch (Exception e) {
            e.printStackTrace();
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
