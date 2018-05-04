package com.composite.utils;

import com.composite.provider.UserServiceProvider;
import com.composite.thrift.service.UserService;
import com.composite.utils.springUtils.SpringContextUtils;

public class CommonServiceUtils {

    private static UserServiceProvider userServiceProvider = SpringContextUtils.getBean(UserServiceProvider.class);

    private static UserService.Client userServiceClient;

    static {
        userServiceClient = userServiceProvider.getBalanceUserService();
    }

    public static String hello() {
        String str = null;
        try {
            str = userServiceClient.sayHello("testUserName");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "hi " + str;
    }
}
