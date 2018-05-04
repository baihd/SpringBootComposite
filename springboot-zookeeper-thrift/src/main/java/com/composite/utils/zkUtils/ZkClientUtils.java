package com.composite.utils.zkUtils;

import com.composite.config.ConfigUtils;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;

public class ZkClientUtils {

    private static String zkServers;
    private static int connectionTimeout;
    private static ZkClient zkClient;

    /**
     * 创建会话
     */
    static {
        ConfigUtils configUtils = new ConfigUtils();
        zkServers = configUtils.getZk_connectString();
        connectionTimeout = configUtils.getZk_connectionTimeout();
        zkClient = new ZkClient(zkServers, connectionTimeout);
    }


    /**
     * 创建节点
     * 通过ZkClient可递归先创建父节点，再创建子节点
     */
    public void createNode(String path, boolean createParents) {
        path = "/zk-book/c1";
        createParents = true;
        zkClient.createPersistent(path, createParents);
    }

    /**
     * 删除节点
     * 通过ZkClient可递归先删除子节点，再删除父节点
     */
    public void deleteNode(String path) {
        path = "/zk-book";
        zkClient.deleteRecursive(path);
    }


    /**
     * 获取子节点
     */
    public void selectNode(String path) {
        path = "/zk-book";
        zkClient.subscribeChildChanges(path, new IZkChildListener() {
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println(parentPath + " 's child changed, currentChilds:" + currentChilds);
            }
        });
        zkClient.createPersistent(path);
        zkClient.createPersistent(path + "/c1");
        zkClient.delete(path + "/c1");
        zkClient.delete(path);
    }

    /**
     * 获取节点数据
     */
    public void selectNodeData(String path) {
        path = "/zk-book";
        zkClient.createEphemeral(path, "123");
        zkClient.subscribeDataChanges(path, new IZkDataListener() {
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("Node " + dataPath + " deleted.");
            }

            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println("Node " + dataPath + " changed, new data: " + data);
            }
        });
        System.out.println(zkClient.readData(path));
        zkClient.writeData(path, "456");
        zkClient.delete(path);
    }

    /**
     * 检测节点是否存在
     */
    public boolean checkNodeIsExist(String path) {
        path = "/zk-book";
        return zkClient.exists(path);
    }
}
