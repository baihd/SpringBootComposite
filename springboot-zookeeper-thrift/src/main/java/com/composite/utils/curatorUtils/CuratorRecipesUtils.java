package com.composite.utils.curatorUtils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.EnsurePath;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class CuratorRecipesUtils {
    static String path = "/zk-book/nodecache";
    static CuratorFramework client = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
            .sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
    static String path1 = "/zk-book";
    static CuratorFramework client1 = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
            .retryPolicy(new ExponentialBackoffRetry(1000, 3)).sessionTimeoutMs(5000).build();

    public void monitorNode() {
        try {
            client.start();
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, "init".getBytes());
            final NodeCache cache = new NodeCache(client, path, false);
            cache.start(true);
            cache.getListenable().addListener(new NodeCacheListener() {
                public void nodeChanged() throws Exception {
                    System.out.println("Node data update, new data: " + new String(cache.getCurrentData().getData()));
                }
            });
            client.setData().forPath(path, "u".getBytes());
            client.delete().deletingChildrenIfNeeded().forPath(path);
        } catch (Exception e) {

        }
    }

    public void monitorChildrenNode() {
        try {
            client1.start();
            PathChildrenCache cache = new PathChildrenCache(client1, path1, true);
            cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            cache.getListenable().addListener(new PathChildrenCacheListener() {
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    switch (event.getType()) {
                        case CHILD_ADDED:
                            System.out.println("CHILD_ADDED," + event.getData().getPath());
                            break;
                        case CHILD_UPDATED:
                            System.out.println("CHILD_UPDATED," + event.getData().getPath());
                            break;
                        case CHILD_REMOVED:
                            System.out.println("CHILD_REMOVED," + event.getData().getPath());
                            break;
                        default:
                            break;
                    }
                }
            });
            client1.create().withMode(CreateMode.PERSISTENT).forPath(path1);
            client1.create().withMode(CreateMode.PERSISTENT).forPath(path1 + "/c1");
            client1.delete().forPath(path1 + "/c1");
            client1.delete().forPath(path1);

        } catch (Exception e) {

        }
    }

    static String master_path = "/curator_recipes_master_path";
    static CuratorFramework client2 = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
            .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

    /**
     * 以下结果会反复循环，并且当一个应用程序完成Master逻辑后，另外一个应用程序的相应方法才会被调用，
     * 即当一个应用实例成为Master后，其他应用实例会进入等待，直到当前Master挂了或者推出后才会开始选举Master。
     */
    public void masterSelect() {
        client2.start();
        LeaderSelector selector = new LeaderSelector(client2, master_path, new LeaderSelectorListenerAdapter() {
            public void takeLeadership(CuratorFramework client) throws Exception {
                System.out.println("成为Master角色");
                Thread.sleep(3000);
                System.out.println("完成Master操作，释放Master权利");
            }
        });
        selector.autoRequeue();
        selector.start();
    }

    public void noDistributedLock() {
        final CountDownLatch down = new CountDownLatch(1);
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        down.await();
                    } catch (Exception e) {
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
                    String orderNo = sdf.format(new Date());
                    System.err.println("生成的订单号是 : " + orderNo);
                }
            }).start();
        }
        down.countDown();
    }

    static String lock_path = "/curator_recipes_lock_path";
    static CuratorFramework client3 = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
            .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

    public void distributedLock() {
        try {
            client3.start();
            final InterProcessMutex lock = new InterProcessMutex(client3, lock_path);
            final CountDownLatch down = new CountDownLatch(1);
            for (int i = 0; i < 30; i++) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            down.await();
                            lock.acquire();
                        } catch (Exception e) {
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
                        String orderNo = sdf.format(new Date());
                        System.out.println("生成的订单号是 : " + orderNo);
                        try {
                            lock.release();
                        } catch (Exception e) {
                        }
                    }
                }).start();
            }
            down.countDown();
        } catch (Exception e) {

        }
    }

    static String distatomicint_path = "/curator_recipes_distatomicint_path";
    static CuratorFramework client5 = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
            .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

    public void distributedAtomicInt() {
        try {
            client5.start();
            DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(client5, distatomicint_path,
                    new RetryNTimes(3, 1000));
            AtomicValue<Integer> rc = atomicInteger.add(8);
            System.out.println("Result: " + rc.succeeded());
        } catch (Exception e) {

        }
    }
    static String path6 = "/curator_zkpath_sample";
    static CuratorFramework client6 = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
            .sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

    public void test(){
        try {
            client6.start();
            ZooKeeper zookeeper = client6.getZookeeperClient().getZooKeeper();

            System.out.println(ZKPaths.fixForNamespace(path6, "sub"));
            System.out.println(ZKPaths.makePath(path6, "sub"));
            System.out.println(ZKPaths.getNodeFromPath("/curator_zkpath_sample/sub1"));

            ZKPaths.PathAndNode pn = ZKPaths.getPathAndNode("/curator_zkpath_sample/sub1");
            System.out.println(pn.getPath());
            System.out.println(pn.getNode());

            String dir1 = path6 + "/child1";
            String dir2 = path6 + "/child2";
            ZKPaths.mkdirs(zookeeper, dir1);
            ZKPaths.mkdirs(zookeeper, dir2);
            System.out.println(ZKPaths.getSortedChildren(zookeeper, path6));

            ZKPaths.deleteChildren(client6.getZookeeperClient().getZooKeeper(), path6, true);
        }catch (Exception e){

        }
    }

    static String path7 = "/zk-book/c1";
    static CuratorFramework client7 = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181")
            .sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

    public void ensurePathDemo(){
        try {
            client7.start();
            client7.usingNamespace("zk-book");

            EnsurePath ensurePath = new EnsurePath(path7);
            ensurePath.ensure(client7.getZookeeperClient());
            ensurePath.ensure(client7.getZookeeperClient());

            EnsurePath ensurePath2 = client7.newNamespaceAwareEnsurePath("/c1");
            ensurePath2.ensure(client7.getZookeeperClient());
        }catch (Exception e){

        }
    }

}
