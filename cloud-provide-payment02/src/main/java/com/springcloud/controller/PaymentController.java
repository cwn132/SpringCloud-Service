package com.springcloud.controller;



import com.alibaba.fastjson.JSONObject;
import com.springcloud.pojo.CommonResult;
import com.springcloud.pojo.Payment;
import com.springcloud.service.PaymentService;
import com.springcloud.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
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
    public CommonResult create(@RequestBody Payment payment){

        try {
            int id = paymentService.create(payment);

            if(id>0){
                log.info("******插入数据库成功***********"+id);
                //将对象转成Jason
                String paymentJason = JSONObject.toJSONString(payment);
                redisUtil.set(String.valueOf(id),paymentJason,CACHE_TIMEOUT, TimeUnit.SECONDS);
                log.info("******插入缓存成功***********"+id);
                return new CommonResult(200,"插入数据库成功",id);
            }else{
                log.info("******插入数据库失败***********"+id);
                return new CommonResult(444,"插入数据库失败",null);
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }

        return null;

    }

    @GetMapping(value="/payment/delete/{paymentId}") //删除订单
    public CommonResult deleteById(@PathVariable("paymentId") Long paymentId){

        Payment payment = paymentService.queryById(paymentId);

        if(payment == null){
            log.info("******数据不存在***********"+paymentId);
            return new CommonResult(200,"数据不存在",paymentId);
        }else {
            try{
                //根据id删除数据库记录
                paymentService.deleteById(paymentId);
                log.info("******删除数据库记录成功***********"+paymentId);
                //根据id删除缓存
                redisUtil.delete(paymentId.toString());
                log.info("******删除缓存成功***********"+paymentId);
                return new CommonResult(200,"删除成功",paymentId);
            }catch (Exception e){
                log.error(e.getMessage());
            }

            return null;
        }

    }

    @PostMapping(value="/payment/update") //更新订单
    public CommonResult updateById(@RequestBody Payment payment){

        Payment pm = paymentService.queryById(payment.getPaymentId());

        if(pm == null){
            log.info("******数据不存在***********"+payment.getPaymentId());
            return new CommonResult(200,"数据不存在",payment.getPaymentId());
        }else {
            try {
                //根据id更新数据库
                paymentService.updateById(payment);
                //将对象转成Jason
                String paymentJason = JSONObject.toJSONString(payment);
                //根据id更新缓存
                redisUtil.update(payment.getPaymentId().toString(),paymentJason);
                log.info("******更新成功***********" + payment.getPaymentId());
                return new CommonResult(200, "更新成功", paymentJason);
            }catch (Exception e){
                log.error(e.getMessage());
            }
            return null;
        }

    }

    //如果方法上的@RequestMapping注解没有设置method属性，则get和post请求默认都可以访问
    @RequestMapping("/payment/lb")
    public int getLB(){
        log.info("serverPort:" + serverPort);
        return this.serverPort;
    }

    //作为@RequestMapping(value="/payment/get/{id}",method = RequestMethod.GET)的快捷方式。也就是可以简化成@PostMapping(value="/payment/get/{id}" )即可
    //@PathVariable 映射 URL 绑定的占位符,一般与@GetMapping一起使用
    @GetMapping("/payment/get/{paymentId}")
    public CommonResult queryById(@PathVariable("paymentId") Long paymentId){

        log.info("paymentId:"+paymentId);
        //先查缓存
        String paymentOrder = redisUtil.get(paymentId.toString());
        if(paymentOrder != null && !"null".equals(paymentOrder)){
            log.info("******查询缓存成功***********" + paymentOrder);
            return new CommonResult(200,"查询成功",paymentOrder);
        }

        //缓存没有再查数据库
        Payment payment = paymentService.queryById(paymentId);
        //将对象转成Jason
        String paymentJason = JSONObject.toJSONString(payment);

        log.info("***************查询结果*********");
        if(payment != null){
            //写入缓存，每次查询续期24小时
            redisUtil.set(payment.getPaymentId().toString(),paymentJason,CACHE_TIMEOUT, TimeUnit.SECONDS);
            return new CommonResult(200,"查询成功",paymentJason);
        }else{
            return new CommonResult(444,"查询失败",null);
        }
    }

    //定义 GetError 的降级方法
    //方法的返回值、参数列表、参数类型，要与原方法保持一致
    public CommonResult getErrorFallback(Long paymentId) {
        return new CommonResult(500,"服务端异常",paymentId);
    }


}
