package com.springcloud;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.Mapping;

@SpringBootApplication
//@ComponentScan({"com.springcloud.controller","com.springcloud.service"})
public class PayMentMain {

    public static void main(String[] args) {
        SpringApplication.run(PayMentMain.class,args);
    }

}
