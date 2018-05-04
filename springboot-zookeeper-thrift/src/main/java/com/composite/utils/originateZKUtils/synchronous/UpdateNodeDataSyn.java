package com.composite.utils.originateZKUtils.synchronous;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * 同步更新节点数据
 */
public class UpdateNodeDataSyn implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    String path = "/zk-book";


    public void updateNodeData() {
        try {
            ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 5000, new UpdateNodeDataSyn());
            connectedSemaphore.await();

            zk.create(path, "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("success create znode: " + path);
            zk.getData(path, true, null);

            Stat stat = zk.setData(path, "456".getBytes(), -1);
            System.out.println("czxID: " + stat.getCzxid() + ", mzxID: " + stat.getMzxid() + ", version: " + stat.getVersion());
            Stat stat2 = zk.setData(path, "456".getBytes(), stat.getVersion());
            System.out.println("czxID: " + stat2.getCzxid() + ", mzxID: " + stat2.getMzxid() + ", version: " + stat2.getVersion());
            try {
                zk.setData(path, "456".getBytes(), stat.getVersion());
            } catch (KeeperException e) {
                System.out.println("Error: " + e.code() + "," + e.getMessage());
            }

        } catch (Exception e) {

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
