package com.composite.utils.originateZKUtils.Asynchronous;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

public class CheckNodeAsy implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    String path = "/zk-book";

    public void checkNode() {
        try {
            ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, new CheckNodeAsy());
            connectedSemaphore.await();

            zooKeeper.exists(path, true, new IIStatCallBack(), null);

            zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            zooKeeper.setData(path, "123".getBytes(), -1);

            zooKeeper.create(path + "/c1", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("success create znode: " + path + "/c1");

            zooKeeper.delete(path + "/c1", -1);
            zooKeeper.delete(path, -1);

            Thread.sleep(Integer.MAX_VALUE);

        } catch (Exception e) {

        }

    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, new CheckNodeAsy());
            if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                if (Event.EventType.None == watchedEvent.getType() && null == watchedEvent.getPath()) {
                    connectedSemaphore.countDown();
                } else if (Event.EventType.NodeCreated == watchedEvent.getType()) {
                    System.out.println("success create znode: " + watchedEvent.getPath());
                    zooKeeper.exists(watchedEvent.getPath(), true, new IIStatCallBack(), null);
                } else if (Event.EventType.NodeDeleted == watchedEvent.getType()) {
                    System.out.println("success delete znode: " + watchedEvent.getPath());
                    zooKeeper.exists(watchedEvent.getPath(), true, new IIStatCallBack(), null);
                } else if (Event.EventType.NodeDataChanged == watchedEvent.getType()) {
                    System.out.println("data changed of znode: " + watchedEvent.getPath());
                    zooKeeper.exists(watchedEvent.getPath(), true, new IIStatCallBack(), null);
                }
            }
        } catch (Exception e) {
        }
    }
}
