package com.springcloud;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.Mapping;

@SpringBootApplication
//@EnableEurekaClient 只对 Eureka 注册中心有效,
//而 @EnableDiscoveryClient 对 Eureka、Zookeeper、Consul 等注册中心都有效
@EnableEurekaClient
//@ComponentScan({"com.springcloud.controller","com.springcloud.service"})
public class PayMentMain {

    public static void main(String[] args) {
        SpringApplication.run(PayMentMain.class,args);
    }

}
