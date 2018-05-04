package com.composite.utils.originateZKUtils.synchronous;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * 同步创建节点
 */
public class CreateNodeSyn implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public void createNode() {
        try {
            ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, new CreateNodeSyn());
            System.out.println(zooKeeper.getState());
            connectedSemaphore.await();
            String path1 = zooKeeper.create("/zk-test-ephemeral", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("Success create znode:" + path1);
            String path2 = zooKeeper.create("/zk-test-ephemeral-", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println("Success create znode: " + path2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            connectedSemaphore.countDown();
        }
    }
}