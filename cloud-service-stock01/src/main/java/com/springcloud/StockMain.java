package com.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
//@EnableEurekaClient 只对 Eureka 注册中心有效,
//而 @EnableDiscoveryClient 对 Eureka、Zookeeper、Consul 等注册中心都有效
@EnableEurekaClient
@EnableDiscoveryClient
@EnableCircuitBreaker
public class StockMain {

    public static void main(String[] args) {
        SpringApplication.run(StockMain.class,args);
    }

}
