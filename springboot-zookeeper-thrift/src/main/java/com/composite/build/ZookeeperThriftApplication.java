package com.composite.build;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//自动引入当前包下的service,component....
@ComponentScan("com.composite")
//开启对计划任务的支持
@EnableScheduling
public class ZookeeperThriftApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZookeeperThriftApplication.class, args);
	}
}
