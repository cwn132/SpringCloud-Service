package com.springcloud.service;


import com.springcloud.pojo.CommonResult;
import com.springcloud.pojo.Payment;
import feign.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@ComponentScan
@FeignClient(value ="mcroservice-payment",fallback = PaymentHystrixFallbackService.class)//使用Feign
public interface PaymentService {

    @PostMapping("/payment/create")
    public int create(Payment payment);

    @GetMapping("/payment/get/{id}")
    public CommonResult queryById(@PathVariable("id") Long id);

    @PostMapping("/payment/update")
    public CommonResult updateById(Payment payment);

    @GetMapping("/payment/delete/{id}")
    public CommonResult deleteById(@PathVariable("id") Integer id);

    //调用生产者微服务名称为mcroservice-payment下边的模拟超时的接口
    @GetMapping("/payment/feign/timeout")
    public String PaymentFeignTimeOutDefault() throws InterruptedException;

}
