package com.springcloud.zookeeper.config;


import com.springcloud.zookeeper.serializer.MyZkSerializer;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class zkClientConifg {

    @Value("${zookeeper.address}")
    private String address;


    @Value("${zookeeper.timeout}")
    private int timeout;

    @Bean
    public ZkClient zkClient() {
        ZkClient zkClient = new ZkClient(address);
        zkClient.setZkSerializer(new MyZkSerializer());
        return zkClient;
    }

}
