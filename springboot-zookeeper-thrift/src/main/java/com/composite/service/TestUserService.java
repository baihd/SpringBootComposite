package com.composite.service;

import java.util.List;
import java.util.Map;

public interface TestUserService {

    int addUser(Map<String, Object> user);

    List<Map<String,Object>> findAllUser();

}
