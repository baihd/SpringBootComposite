package com.composite.config;

import com.composite.thrift.service.UserService;
import com.composite.thrift.service.impl.UserServiceImpl;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThriftConfig {
    ExecutorService executor = Executors.newSingleThreadExecutor();

    @Autowired
    private ThriftProperties properties;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @PostConstruct
    public void init() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //服务端开启服务
                tServer().serve();
            }
        });
    }

    @Bean
    public TServer tServer() {
        TServer server = null;
        try {
            // 非阻塞式的，配合TFramedTransport使用
            TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(properties.getPort());
            // 关联处理器与Service服务的实现
            TProcessor processor = new UserService.Processor<UserService.Iface>(userServiceImpl);
            // 目前Thrift提供的最高级的模式，可并发处理客户端请求
            TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(serverTransport);
            args.processor(processor);
            // 设置协议工厂，高效率的、密集的二进制编码格式进行数据传输协议
            args.protocolFactory(new TCompactProtocol.Factory());
            // 设置传输工厂，使用非阻塞方式，按块的大小进行传输，类似于Java中的NIO
            args.transportFactory(new TFramedTransport.Factory());
            // 设置处理器工厂,只返回一个单例实例
            args.processorFactory(new TProcessorFactory(processor));
            // 多个线程，主要负责客户端的IO处理
            args.selectorThreads(2);
            // 工作线程池
            ExecutorService pool = Executors.newFixedThreadPool(3);
            args.executorService(pool);
            server = new TThreadedSelectorServer(args);
        } catch (TTransportException e) {
            e.printStackTrace();
        }
        return server;
    }
}

