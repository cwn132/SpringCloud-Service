package com.springcloud.ribbon;


import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.RoundRobinRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//自定义负载均衡路由规则类
@Configuration
public class RobinRule {

    @Bean
    public IRule RobinRule() {
        // RoundRobinRule为轮询，RandomRule为随机
        return new RandomRule();
    }

}
