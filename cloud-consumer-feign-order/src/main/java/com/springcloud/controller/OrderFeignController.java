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
//    @HystrixCommand(fallbackMethod="error",commandProperties={@HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="3500")})
    public CommonResult<Payment> getPaymentById(@PathVariable long id){
            return paymentService.queryById(id);
    }

//    @GetMapping("/consumer/feign/timeout")
//    public String PaymentFeignTimeOut() throws InterruptedException{
//        return paymentService.PaymentFeignTimeOut();
//    }


//    /**
//     * 熔断的回调方法，也就是降级方法
//     * @HystrixCommand方法级的熔断处理需要当前方法和回调方法的返回类型和参数要一致
//     * @return
//     */
//    public CommonResult<Payment> error(long id) {
//        System.out.println("服务器异常");
//        //访问远程服务失败，该如何处理？这些处理逻辑就可以写在该方法中
//        return new CommonResult(500,"服务器异常",id);
//    }




}
