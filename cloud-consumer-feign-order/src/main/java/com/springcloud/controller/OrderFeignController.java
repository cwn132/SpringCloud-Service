package com.springcloud.controller;


import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.springcloud.pojo.CommonResult;
import com.springcloud.pojo.Payment;
import com.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class OrderFeignController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/consumer/payment/create") //创建订单
    public CommonResult create(@RequestBody Payment payment){

        int i = paymentService.create(payment);
        if(i>0){
            return new CommonResult(200,"插入成功",i);
        }else {
            return new CommonResult(444,"插入失败",i);
        }

    }

    @GetMapping("/consumer/payment/get/{id}") //根据ID获取订单
    public CommonResult<Payment> getPaymentById(@PathVariable long id){
            return paymentService.queryById(id);
    }

    @GetMapping(value="/consumer/payment/delete/{id}") //删除订单
    public CommonResult<Payment> deleteById(@PathVariable("id") Integer id){
        return paymentService.deleteById(id);
    }

    @PostMapping(value="/consumer/payment/update") //更新订单
    public CommonResult updateById(@RequestBody Payment dept){
        return paymentService.updateById(dept);
    }





}
