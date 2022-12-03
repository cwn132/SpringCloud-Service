package com.springcloud.config;


import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration //声明一个类为配置类，用于取代bean.xml配置文件注册bean对象。
public class ApplicationContextConfig {


//    @Component注解作用于类上，而@Bean注解作用于配置类中的某一个方法上；
//    @Component表明告知Spring为当前的这个类创建一个bean，而@Bean表明告知Spring此方法将会返回一个对象，将返回的对象注入到容器中。
    @Bean //使用@Bean注解来注入组件
    @LoadBalanced //RestTemplate 的负载均衡能力
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
