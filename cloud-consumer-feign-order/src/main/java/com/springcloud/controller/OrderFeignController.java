package com.springcloud.controller;


import com.springcloud.pojo.CommonResult;
import com.springcloud.pojo.Payment;
import com.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class OrderFeignController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/consumer/payment/create")
    public CommonResult create(@RequestBody Payment payment){

        int i = paymentService.create(payment);
        if(i>0){
            return new CommonResult(200,"插入成功",i);
        }else {
            return new CommonResult(444,"插入失败",i);
        }

    }

    @GetMapping("/consumer/payment/get/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable long id){
            return paymentService.queryById(id);
    }

    @GetMapping("/consumer/feign/timeout")
    public String PaymentFeignTimeOut() throws InterruptedException{
        return paymentService.PaymentFeignTimeOut();
    }

}
