package com.composite.utils.originateZKUtils.Asynchronous;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * 异步获取子节点
 */
public class SelectNodeAsyn implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    String path = "/zk-book";

    public void selectNode() {
        try {
            ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, new SelectNodeAsyn());
            connectedSemaphore.await();
            zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("success create znode: " + path);

            zooKeeper.create(path + "/c1", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("success create znode: " + path + "/c1");

            zooKeeper.getChildren(path, true, new IChildren2CallBack(), null);

            zooKeeper.create(path + "/c2", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("success create znode: " + path + "/c2");
        } catch (Exception e) {

        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            if (Event.EventType.None == watchedEvent.getType() && null == watchedEvent.getPath()) {
                connectedSemaphore.countDown();
            } else if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
                try {
                    ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, new SelectNodeAsyn());
                    System.out.println("ReGet Child:" + zooKeeper.getChildren(watchedEvent.getPath(), true));
                } catch (Exception e) {
                }
            }
        }
    }
}
