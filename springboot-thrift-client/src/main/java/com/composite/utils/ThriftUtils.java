package com.composite.utils;

import com.composite.config.ThriftProperties;
import com.composite.thrift.service.UserService;
import com.composite.utils.springUtils.SpringContextUtils;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class ThriftUtils {

    private ThriftProperties tfProperties = SpringContextUtils.getBean(ThriftProperties.class);

    public String start(String username) {
        TSocket tSocket = new TSocket("127.0.0.1", tfProperties.getPort());
        //使用非阻塞方式，按块的大小进行传输，类似于Java中的NIO。记得调用close释放资源
        TTransport transport = new TFramedTransport(tSocket);
        //高效率的、密集的二进制编码格式进行数据传输协议
        TProtocol protocol = new TCompactProtocol(transport);

        UserService.Client client = new UserService.Client(protocol);
        open(transport);
        String result = null;
        try {
            result = client.sayHello(username);
        } catch (TException e) {
            e.printStackTrace();
        }
        close(transport);
        return result;
    }

    private void open(TTransport transport) {
        if (transport != null && !transport.isOpen()) {
            try {
                transport.open();
            } catch (TTransportException e) {
                e.printStackTrace();
            }
        }
    }

    private void close(TTransport transport) {
        if (transport != null && transport.isOpen()) {
            transport.close();
        }
    }


}
