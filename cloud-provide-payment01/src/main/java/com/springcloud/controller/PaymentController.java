package com.springcloud.controller;



import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.springcloud.dao.PaymentDao;
import com.springcloud.pojo.CommonResult;
import com.springcloud.pojo.Payment;
import com.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Slf4j
public class PaymentController {



    @Autowired
    private PaymentService paymentService;


    //注入服务发现的注解
    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${server.port}")
    private int serverPort;

    //获取服务信息
    @GetMapping("/payment/discovery")
    public Object discovery(){
        List<String> services = discoveryClient.getServices();
        for (String s : services){
            log.info("********注册到eureka中的服务中有:"+services);
        }
        //根据服务Id获取对应的服务实例集合
        List <ServiceInstance> instances = discoveryClient.getInstances("MCROSERVICE-PAYMENT");
        for (ServiceInstance s: instances) {
            log.info("当前服务的实例有"+s.getServiceId()+"\t"+s.getHost()+"\t"+s.getPort()+"\t"+s.getUri());
        }
        return this.discoveryClient;
    }


    //作为@RequestMapping(value="/payment/create",method = RequestMethod.POST)的快捷方式。也就是可以简化成@PostMapping(value="/payment/create" )即可
    @PostMapping(value="/payment/create") //创建订单
    public CommonResult create(@RequestBody Payment dept){

        int i = paymentService.create(dept);

        log.info("******插入成功***********"+i);
        if(i>0){
            return new CommonResult(200,"插入数据库成功",i);
        }else{
            return new CommonResult(444,"插入数据库失败",null);
        }

    }

    @GetMapping(value="/payment/delete/{id}") //删除订单
    public CommonResult deleteById(@PathVariable("id") Integer id){

        Payment payment = paymentService.queryById(id);

        if(payment == null){
            return new CommonResult(200,"数据不存在",id);
        }else {
            paymentService.deleteById(id);
            return new CommonResult(200,"删除成功",id);
        }

    }

    @PostMapping(value="/payment/update") //更新订单
    public CommonResult updateById(@RequestBody Payment dept){

        Payment payment = paymentService.queryById(dept.getId());

        if(payment == null){
            return new CommonResult(200,"数据不存在",dept.getId());
        }else {
            paymentService.updateById(dept);
            return new CommonResult(200,"更新成功",dept.getId());
        }

    }

    //如果方法上的@RequestMapping注解没有设置method属性，则get和post请求默认都可以访问
    @RequestMapping("/hello")
    public String hello(){
        System.out.println("hello");
        return "hello";
    }

    //如果方法上的@RequestMapping注解没有设置method属性，则get和post请求默认都可以访问
    @RequestMapping("/payment/lb")
    public int getLB(){
        System.out.println(serverPort);
        return this.serverPort;
    }

    //作为@RequestMapping(value="/payment/get/{id}",method = RequestMethod.GET)的快捷方式。也就是可以简化成@PostMapping(value="/payment/get/{id}" )即可
    //@PathVariable 映射 URL 绑定的占位符,一般与@GetMapping一起使用
    @GetMapping("/payment/get/{id}")
    //该方法会发生异常，从而调用 @HystrixCommand 方法所配置的降级方法
    @HystrixCommand(fallbackMethod = "getErrorFallback", commandProperties = {
            //想要查找可以设置的 HystrixProperty 值，可以按两下 shift 键，
            //输入 HystrixCommandProperties，然后查看 HystrixCommandProperties 类的构造方法
            //设置Hystrix的超时时间，默认1秒
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"),
            //设置熔断监控时间 默认5秒
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000"),
            //设置导致熔断的失败次数。默认20次
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
            //设置导致熔断的失败率 默认50%
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "80")
    })
    public CommonResult queryById(@PathVariable("id") Long id){
        Payment payment = paymentService.queryById(id);

        log.info("***************查询结果*********");
        if(payment != null){
            return new CommonResult(200,"查询成功",payment.getSerial());
        }else{
            return new CommonResult(444,"查询失败",null);
        }
    }

    //定义 GetError 的降级方法
    //方法的返回值、参数列表、参数类型，要与原方法保持一致
    public CommonResult getErrorFallback(Long id) {
        return new CommonResult(500,"服务端异常",id);
    }


}
