package com.composite.config;

import com.composite.thrift.service.UserService;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ZooKeeperConfig {

    @Autowired
    private ZooKeeperProperties zkProperties;

    @Autowired
    private ThriftProperties tfProperties;

    ExecutorService executor = Executors.newSingleThreadExecutor();

    // thrift实例列表
    public static Map<String, UserService.Client> serviceMap = new HashMap<>();


    @PostConstruct
    private void init() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                startZooKeeper();
                try {
                    Thread.sleep(1000 * 60 * 60 * 24 * 360 * 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 注册服务
    private void startZooKeeper() {
        List<String> currChilds;
        //根节点路径
        String servicePath = "/" + zkProperties.getServiceName();
        ZkClient zkClient = new ZkClient(zkProperties.getServerList());
        boolean serviceExists = zkClient.exists(servicePath);
        if (serviceExists) {
            currChilds = zkClient.getChildren(servicePath);
        } else {
            throw new RuntimeException("service not exist!");
        }

        for (String instanceName : currChilds) {
            //没有该服务，建立该服务
            if (!serviceMap.containsKey(instanceName)) {
                serviceMap.put(instanceName, createUserService(instanceName));
            }
        }
        //注册事件监听
        zkClient.subscribeChildChanges(servicePath, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath,
                                          List<String> currentChilds) throws Exception {
                //实例(path)列表:当某个服务实例宕机，实例列表内会减去该实例
                for (String instanceName : currentChilds) {
                    //没有该服务，建立该服务
                    if (!serviceMap.containsKey(instanceName)) {
                        serviceMap.put(instanceName, createUserService(instanceName));
                    }
                }
                for (Map.Entry<String, UserService.Client> entry : serviceMap.entrySet()) {
                    //该服务已被移除
                    if (!currentChilds.contains(entry.getKey())) {
                        UserService.Client c = serviceMap.get(entry.getKey());
                        try {
                            c.getInputProtocol().getTransport().close();
                            c.getOutputProtocol().getTransport().close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        serviceMap.remove(entry.getKey());
                    }
                }
                System.out.println(parentPath + "事件触发");
            }
        });
    }

    //创建一个服务实例
    private UserService.Client createUserService(String serviceInstanceName) {
        String ip = serviceInstanceName.split("-")[1];
        TSocket tSocket = new TSocket(ip, tfProperties.getPort());
        //使用非阻塞方式，按块的大小进行传输，类似于Java中的NIO。记得调用close释放资源
        TTransport transport = new TFramedTransport(tSocket);
        //高效率的、密集的二进制编码格式进行数据传输协议
        TProtocol protocol = new TCompactProtocol(transport);

        UserService.Client client = new UserService.Client(protocol);
        try {
            transport.open();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
        return client;
    }


}