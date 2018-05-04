package com.composite.utils.curatorUtils;

import com.composite.config.ConfigUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CuratorClientUtils {
    private static String connectString;
    private static int sessionTimeout;
    private static int connectionTimeout;
    private static CuratorFramework client;

    private static CountDownLatch semaphore = new CountDownLatch(2);
    private static ExecutorService tp = Executors.newFixedThreadPool(2);

    /**
     * 创建会话
     */
    static {
        ConfigUtils configUtils = new ConfigUtils();
        connectString = configUtils.getZk_connectString();
        sessionTimeout = configUtils.getZk_sessionTimeOut();
        connectionTimeout = configUtils.getZk_connectionTimeout();
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder().connectString(connectString)
                .sessionTimeoutMs(sessionTimeout).connectionTimeoutMs(connectionTimeout).retryPolicy(retryPolicy).build();
        //该会话含有隔离命名空间，即客户端对Zookeeper上数据节点的任何操作都是相对/base目录进行的，这有利于实现不同的Zookeeper的业务之间的隔离。
        /*curatorFramework = CuratorFrameworkFactory.builder().connectString(connectString)
                .sessionTimeoutMs(sessionTimeout).connectionTimeoutMs(connectionTimeout).retryPolicy(retryPolicy).namespace("base").build();*/
        client.start();
    }

    /**
     * 创建节点
     */
    public void createNode(String path) {
        try {
            path = "/zk-book/c1";
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, "init".getBytes());
        } catch (Exception e) {
        }
    }


    /**
     * 异步创建节点
     */
    public void createNodeAsyn(String path) {
        try {
            System.out.println("Main thread: " + Thread.currentThread().getName());

            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
                public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                    System.out.println("event[code: " + event.getResultCode() + ", type: " + event.getType() + "]" + ", Thread of processResult: " + Thread.currentThread().getName());
                    System.out.println();
                    semaphore.countDown();
                }
            }, tp).forPath(path, "init".getBytes());

            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
                public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                    System.out.println("event[code: " + event.getResultCode() + ", type: " + event.getType() + "]" + ", Thread of processResult: " + Thread.currentThread().getName());
                    semaphore.countDown();
                }
            }).forPath(path, "init".getBytes());

            semaphore.await();
            tp.shutdown();

        } catch (Exception e) {

        }
    }

    /**
     * 删除节点
     */
    public void deleteNode(String path) {
        try {
            path = "/zk-book/c1";
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, "init".getBytes());
            Stat stat = new Stat();
            System.out.println(new String(client.getData().storingStatIn(stat).forPath(path)));
            client.delete().deletingChildrenIfNeeded().withVersion(stat.getVersion()).forPath(path);
            System.out.println("success delete znode " + path);
        } catch (Exception e) {
        }
    }


    /**
     * 获取节点数据
     */
    public void selectNodeData(String path) {
        try {
            path = "/zk-book";
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, "init".getBytes());
            Stat stat = new Stat();
            System.out.println(new String(client.getData().storingStatIn(stat).forPath(path)));
        } catch (Exception e) {

        }
    }

    /**
     * 更新节点数据
     */
    public void updateNodeData(String path) {
        try {
            path = "/zk-book";
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, "init".getBytes());
            Stat stat = new Stat();
            client.getData().storingStatIn(stat).forPath(path);
            System.out.println("Success set node for : " + path + ", new version: "
                    + client.setData().withVersion(stat.getVersion()).forPath(path).getVersion());
            try {
                client.setData().withVersion(stat.getVersion()).forPath(path);
            } catch (Exception e) {
                //当携带数据版本不一致时，无法完成更新操作。
                System.out.println("Fail set node due to " + e.getMessage());
            }
        } catch (Exception e) {

        }
    }


}
