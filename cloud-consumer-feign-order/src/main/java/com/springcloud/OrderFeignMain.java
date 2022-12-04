package com.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients //激活Feign的注解  需要加到主启动类上
@EnableEurekaClient
public class OrderFeignMain {

    public static void main(String[] args) {
        SpringApplication.run(OrderFeignMain.class,args);
    }

}
