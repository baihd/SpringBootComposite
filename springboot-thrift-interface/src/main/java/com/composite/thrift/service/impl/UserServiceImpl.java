package com.composite.thrift.service.impl;

import com.composite.dao.UserDao;
import com.composite.thrift.service.UserService;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UserServiceImpl implements UserService.Iface {

    @Autowired
    private UserDao userDao;

    @Override
    public String sayHello(String username) throws TException {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            List<Map<String, Object>> mapList = userDao.selectUsers();
            Map<String, Object> map = mapList.get(0);
            for (Map.Entry<String, Object> vo : map.entrySet()) {
                stringBuilder.append(vo.getKey());
                stringBuilder.append(":");
                stringBuilder.append(vo.getValue());
                stringBuilder.append("-");
            }
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "hello" + stringBuilder.toString() + username;
    }

    @Override
    public String getRandom() throws TException {
        return "random";
    }
}
