package com.composite.utils.originateZKUtils.Asynchronous;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * 异步创建节点
 */
public class CreateNodeAsyn implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public void createNode() {
        try {
            ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, new CreateNodeAsyn());
            System.out.println(zooKeeper.getState());
            connectedSemaphore.await();
            zooKeeper.create("/zk-test-ephemeral-", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
                    new IStringCallBack(), "I am context. ");

            zooKeeper.create("/zk-test-ephemeral-", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
                    new IStringCallBack(), "I am context. ");

            zooKeeper.create("/zk-test-ephemeral-", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,
                    new IStringCallBack(), "I am context. ");
            Thread.sleep(Integer.MAX_VALUE);
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
