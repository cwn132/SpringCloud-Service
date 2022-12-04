package com.springcloud;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
//@EnableEurekaClient 只对 Eureka 注册中心有效,
//而 @EnableDiscoveryClient 对 Eureka、Zookeeper、Consul 等注册中心都有效
@EnableEurekaClient
@EnableDiscoveryClient
//@ComponentScan({"com.springcloud.controller","com.springcloud.service"})
public class PaymentMain {

    public static void main(String[] args) {
        SpringApplication.run(PaymentMain.class,args);
    }

}
