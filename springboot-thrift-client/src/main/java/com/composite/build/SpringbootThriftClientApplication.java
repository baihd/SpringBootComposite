package com.composite.build;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.composite")
public class SpringbootThriftClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootThriftClientApplication.class, args);
	}
}
