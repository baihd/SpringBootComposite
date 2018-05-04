package com.composite.utils.originateZKUtils.synchronous;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * 同步删除节点
 */
public class DeleteNodeSyn implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    String path = "/zk-book";

    public void deleteNode() {
        try {
            ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, new DeleteNodeSyn());
            connectedSemaphore.await();
            zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("success create znode: " + path + "/c1");
            //若节点有子节点，则无法将其删除，必须先删除其所有子节点。
            try {
                zooKeeper.delete(path, -1);
            } catch (Exception e) {
                System.out.println("fail to delete znode: " + path);
            }
            zooKeeper.delete(path + "/c1", -1);
            System.out.println("success delete znode: " + path + "/c1");
            zooKeeper.delete(path, -1);
            System.out.println("success delete znode: " + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void process(WatchedEvent event) {
        if (Event.KeeperState.SyncConnected == event.getState()) {
            if (Event.EventType.None == event.getType() && null == event.getPath()) {
                connectedSemaphore.countDown();
            }
        }
    }
}
