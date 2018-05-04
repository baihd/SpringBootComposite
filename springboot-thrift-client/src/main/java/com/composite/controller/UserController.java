package com.composite.controller;

import com.composite.provider.UserServiceProvider;
import com.composite.utils.ThriftUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    @Autowired
    UserServiceProvider userServiceProvider;

    @ResponseBody
    @RequestMapping(value = "/hello")
    public String hello() {
        ThriftUtils thriftUtils = new ThriftUtils();
        String result =thriftUtils.start("test");
        return "[ " + result + "]" + "[" + "]";
    }


}
