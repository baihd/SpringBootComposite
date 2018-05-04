package com.composite.utils.originateZKUtils.synchronous;

import org.apache.zookeeper.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 同步获取子节点
 */
public class SelectNodeSyn implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    String path = "/zk-book-1";

    public void selectNode() {
        try {
            ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, new SelectNodeSyn());
            connectedSemaphore.await();

            zooKeeper.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("success create znode: " + path);

            zooKeeper.create(path + "/c1", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("success create znode: " + path + "/c1");

            List<String> childrenList = zooKeeper.getChildren(path, true);
            System.out.println(childrenList);

            zooKeeper.create(path + "/c2", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("success create znode: " + path + "/c2");

            zooKeeper.create(path + "/c3", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("success create znode: " + path + "/c3");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Watcher通知是一次性的，即一旦触发一次通知后，该Watcher就失效了，因此客户端需要反复注册Watcher，
     * 即程序中在process里面又注册了Watcher，否则，将无法获取c3节点的创建而导致子节点变化的事件。
     * @param watchedEvent
     */
    @Override
    public void process(WatchedEvent watchedEvent) {

        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            if (Event.EventType.None == watchedEvent.getType() && null == watchedEvent.getPath()) {
                connectedSemaphore.countDown();
            } else if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
                try {
                    ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, new SelectNodeSyn());
                    System.out.println("ReGet Child:" + zooKeeper.getChildren(watchedEvent.getPath(), true));
                } catch (Exception e) {
                }
            }
        }
    }
}
