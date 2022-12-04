package com.springcloud.controller;


import com.springcloud.dao.PaymentDao;
import com.springcloud.pojo.CommonResult;
import com.springcloud.pojo.Payment;
import com.springcloud.service.PaymentService;
import com.springcloud.util.RedisUtil;
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
import java.util.concurrent.TimeUnit;


@RestController
@Slf4j
public class PaymentController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private PaymentService paymentService;


    //注入服务发现的注解
    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${server.port}")
    private int serverPort;

    //设置缓存过期时间为24小时
    public static final Integer CACHE_TIMEOUT = 60 * 60 * 24;

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

        try {
            int i = paymentService.create(dept);

            if(i>0){
                log.info("******插入数据库成功***********"+i);
                return new CommonResult(200,"插入数据库成功",i);
            }else{
                log.info("******插入数据库失败***********"+i);
                return new CommonResult(444,"插入数据库失败",null);
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }

        return null;

    }

    @GetMapping(value="/payment/delete/{id}") //删除订单
    public CommonResult deleteById(@PathVariable("id") Integer id){

        Payment payment = paymentService.queryById(id);

        if(payment == null){
            log.info("******数据不存在***********"+id);
            return new CommonResult(200,"数据不存在",id);
        }else {
            try{
                //根据id删除数据库记录
                paymentService.deleteById(id);
                //根据id删除缓存
                redisUtil.delete(id.toString());
                log.info("******删除成功***********"+id);
                return new CommonResult(200,"删除成功",id);
            }catch (Exception e){
                log.error(e.getMessage());
            }

            return null;
        }

    }

    @PostMapping(value="/payment/update") //更新订单
    public CommonResult updateById(@RequestBody Payment dept){

        Payment payment = paymentService.queryById(dept.getId());

        if(payment == null){
            log.info("******数据不存在***********"+dept.getId());
            return new CommonResult(200,"数据不存在",dept.getId());
        }else {
            try {
                //根据id更新数据库
                paymentService.updateById(dept);
                //根据id更新缓存
                redisUtil.update(dept.getId().toString(),dept.getSerial());
                log.info("******更新成功***********" + dept.getId());
                return new CommonResult(200, "更新成功", dept.getId());
            }catch (Exception e){
                log.error(e.getMessage());
            }
            return null;
        }

    }


    //如果方法上的@RequestMapping注解没有设置method属性，则get和post请求默认都可以访问
    @RequestMapping("/payment/lb")
    public int getLB(){
        return this.serverPort;
    }

    //作为@RequestMapping(value="/payment/get/{id}",method = RequestMethod.GET)的快捷方式。也就是可以简化成@PostMapping(value="/payment/get/{id}" )即可
    //@PathVariable 映射 URL 绑定的占位符,一般与@GetMapping一起使用
    @GetMapping("/payment/get/{id}")
    public CommonResult queryById(@PathVariable("id") Long id){

        //先查缓存
        String order = redisUtil.get(id.toString());
        if(order != null && !"null".equals(order)){
            log.info("******查询缓存成功***********" + order);
            return new CommonResult(200,"查询成功",order);
        }

        //缓存没有再查数据库
        Payment payment = paymentService.queryById(id);
        log.info("***************查询结果*********");
        if(payment != null){
            //写入缓存，每次查询续期24小时
            redisUtil.set(payment.getId().toString(),payment.getSerial(),CACHE_TIMEOUT, TimeUnit.SECONDS);
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
