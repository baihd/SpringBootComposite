package com.composite.service.impl;

import com.composite.dao.UserDao;
import com.composite.service.TestUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TestUserServiceImpl implements TestUserService {
    @Autowired
    private UserDao userDao;

    @Override
    public int addUser(Map<String, Object> user) {
        return userDao.insert(user);
    }

    @Override
    public List<Map<String, Object>> findAllUser() {
        List<Map<String, Object>> mapList = new ArrayList<>();
        try {
            mapList = userDao.selectUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapList;
    }
}
