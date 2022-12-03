package com.springcloud.controller;


import com.springcloud.pojo.CommonResult;
import com.springcloud.pojo.Payment;
import com.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
public class PaymentControler {

    @Autowired
    private PaymentService paymentService;

    //作为@RequestMapping(value="/payment/create",method = RequestMethod.POST)的快捷方式。也就是可以简化成@PostMapping(value="/payment/create" )即可
    @PostMapping(value="/payment/create")
    public CommonResult create(@RequestBody Payment dept){

        int i = paymentService.create(dept);

        log.info("******插入成功***********"+i);
        if(i>0){
            return new CommonResult(200,"插入数据库成功",i);
        }else{
            return new CommonResult(444,"插入数据库失败",null);
        }

    }

    //如果方法上的@RequestMapping注解没有设置method属性，则get和post请求默认都可以访问
    @RequestMapping("/hello")
    public String hello(){
        System.out.println("hello");
        return "hello";
    }

    //作为@RequestMapping(value="/payment/get/{id}",method = RequestMethod.GET)的快捷方式。也就是可以简化成@PostMapping(value="/payment/get/{id}" )即可
    //@PathVariable 映射 URL 绑定的占位符,一般与@GetMapping一起使用
    @GetMapping("/payment/get/{id}")
    public CommonResult queryById(@PathVariable("id") Long id){
        Payment payment = paymentService.queryById(id);

        log.info("***************查询结果*********");
        if(payment != null){
            return new CommonResult(200,"查询成功",payment.getSerial());
        }else{
            return new CommonResult(444,"查询失败",null);
        }
    }


}
