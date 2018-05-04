package com.composite.utils.originateZKUtils.synchronous;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * 同步获取节点数据
 */
public class SelectNodeDataSyn implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk = null;
    private static Stat stat = new Stat();
    String path = "/zk-book";

    public void selectNode() {
        try {
            ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 5000, new SelectNodeDataSyn());
            connectedSemaphore.await();
            zk.create(path, "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("success create znode: " + path);

            System.out.println("the data of znode " + path + " is : " + new String(zk.getData(path, true, stat)));
            System.out.println("czxID: " + stat.getCzxid() + ", mzxID: " + stat.getMzxid() + ", version: " + stat.getVersion());

            zk.setData(path, "123".getBytes(), -1);
        } catch (Exception e) {

        }

    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            if (Event.EventType.None == watchedEvent.getType() && null == watchedEvent.getPath()) {
                connectedSemaphore.countDown();
            } else if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
                try {
                    System.out.println("the data of znode " + watchedEvent.getPath() + " is : " + new String(zk.getData(watchedEvent.getPath(), true, stat)));
                    System.out.println("czxID: " + stat.getCzxid() + ", mzxID: " + stat.getMzxid() + ", version: " + stat.getVersion());
                } catch (Exception e) {
                }
            }
        }
    }
}
