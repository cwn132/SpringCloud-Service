package com.springcloud.controller;



import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.springcloud.generator.OrderCodeGenerator;
import com.springcloud.pojo.CommonResult;
import com.springcloud.pojo.Payment;
import com.springcloud.proxy.PaymentJDKProxy;
import com.springcloud.service.PaymentService;
import com.springcloud.util.RedisUtil;
import com.springcloud.zookeeper.lock.ZkLock;


import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;


@RestController
@Slf4j
public class PaymentController {

    //调用仓库服务端的ip+端口号
    public static final  String STOCK_URL = "http://mcroservice-stock";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private PaymentService paymentService;

    //注入服务发现的注解
    @Autowired
    private DiscoveryClient discoveryClient;

//    @Autowired
//    private ZkLock zkLock;

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

    @GlobalTransactional(name = "mcroservice-payment", rollbackFor = Exception.class)
    //作为@RequestMapping(value="/payment/create",method = RequestMethod.POST)的快捷方式。也就是可以简化成@PostMapping(value="/payment/create" )即可
    @PostMapping(value="/payment/create/{productId}") //创建订单
    public CommonResult create(@RequestBody Payment payment,@PathVariable("productId") int productId){

        try{
            if(Integer.valueOf(payment.getPaymentNum()) instanceof Integer && Integer.valueOf(productId) instanceof Integer){
                log.info("******输入有效******");
            }
        }catch (Exception e){
            return new CommonResult(444,"输入不正确",e.getMessage());
        }

        //判断订单数量为空
        if(String.valueOf(payment.getPaymentNum()) == null){
            return new CommonResult(444,"订单数量为空",null);
        }

        //查仓库
        Long stockNum = restTemplate.getForObject(STOCK_URL+"/stock/getStockNum/"+productId,Long.class);
        log.info("******库存:***********"+stockNum);
        if (stockNum.intValue() <= 0){
            return new CommonResult(444,"库存为0",0);
        }

        //判断库存是否满足订单数量
        if(payment.getPaymentNum() > stockNum.intValue()){
            return new CommonResult(444,"库存数量不足",stockNum);
        }

            //计算总价格
            if(payment.getPaymentPrice() != null){
                payment.setPaymentTotalPrice(new BigDecimal(payment.getPaymentPrice().doubleValue()*payment.getPaymentNum()).setScale(2, BigDecimal.ROUND_HALF_UP));
            }

            //更新库存
            Long updateStockNum = restTemplate.getForObject(STOCK_URL+"/stock/updateStockByOrder/"+productId+"/"+payment.getPaymentNum(),Long.class);
            log.info("******更新仓库成功***********"+updateStockNum);

            payment.setPaymentSerial(OrderCodeGenerator.generatorOrderCode());
            int id = paymentService.create(payment);

            if(id>0){
                log.info("******插入数据库成功***********"+payment.getPaymentSerial());
                //将对象转成Jason
                String paymentJson = JSONObject.toJSONString(payment);
                redisUtil.set(String.valueOf(payment.getPaymentSerial()),paymentJson,CACHE_TIMEOUT, TimeUnit.SECONDS);
                log.info("******插入缓存成功***********"+payment.getPaymentSerial());

                return new CommonResult(200,"新建订单成功",paymentJson);

            }else{
                log.info("******插入数据库失败***********"+payment.getPaymentSerial());
                return new CommonResult(444,"新建订单失败",null);
            }

    }

//    @PostMapping(value="/payment/create/{productId}") //创建订单
//    public CommonResult create(@RequestBody Payment payment,@PathVariable("productId") int productId){
//
//        try{
//            if(Integer.valueOf(payment.getPaymentNum()) instanceof Integer && Integer.valueOf(productId) instanceof Integer){
//                log.info("******输入有效******");
//            }
//        }catch (Exception e){
//            return new CommonResult(444,"输入不正确",e.getMessage());
//        }
//
//        //判断库存数量
//        if(String.valueOf(payment.getPaymentNum()) == null){
//            return new CommonResult(444,"订单数量为空",null);
//        }
//
//        //查仓库
//        Long stockNum = restTemplate.getForObject(STOCK_URL+"/stock/getStockNum/"+productId,Long.class);
//        log.info("******库存:***********"+stockNum);
//        if (stockNum.intValue() <= 0){
//            return new CommonResult(444,"库存为0",0);
//        }
//
//        //判断库存是否满足订单数量
//        if(payment.getPaymentNum() > stockNum.intValue()){
//            return new CommonResult(444,"库存数量不足",stockNum);
//        }
//
//        //Zookeeper锁 - 创建临时节点
////        try {
////            try {
////                zkLock.lock();
////
////                //新建订单编号
////                String orderSerial =OrderCodeGenerator.generatorOrderCode();
////                log.info("orderSerial:"+orderSerial);
////                payment.setPaymentSerial(orderSerial);
////            }catch (Exception e){
////                log.error(e.getMessage());
////            }finally {
////                zkLock.unlock();
////            }
//        //新建订单编号
//        String orderSerial =OrderCodeGenerator.generatorOrderCode();
//
//        //计算总价格
//        if(payment.getPaymentPrice() != null){
//            payment.setPaymentTotalPrice(new BigDecimal(payment.getPaymentPrice().doubleValue()*payment.getPaymentNum()).setScale(2, BigDecimal.ROUND_HALF_UP));
//        }
//
//        int id = paymentService.create(payment);
//
//        if(id>0){
//            log.info("******插入数据库成功***********"+payment.getPaymentSerial());
//            //将对象转成Jason
//            String paymentJson = JSONObject.toJSONString(payment);
//            redisUtil.set(String.valueOf(payment.getPaymentSerial()),paymentJson,CACHE_TIMEOUT, TimeUnit.SECONDS);
//            log.info("******插入缓存成功***********"+payment.getPaymentSerial());
//
//            //更新库存
//            Long updateStockNum = stockNum - payment.getPaymentNum();
//            updateStockNum = restTemplate.getForObject(STOCK_URL+"/stock/updateStockNum/"+productId+"/"+updateStockNum,Long.class);
//            log.info("******更新仓库成功***********"+updateStockNum);
//
//            return new CommonResult(200,"新建订单成功",paymentJson);
//
//        }else{
//            log.info("******插入数据库失败***********"+payment.getPaymentSerial());
//            return new CommonResult(444,"新建订单失败",null);
//        }
////        }catch (Exception e){
////            log.error(e.getMessage());
////        }
//
////        return null;
//
//    }


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
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public CommonResult updateById(@RequestBody Payment payment){

        Payment pm = paymentService.queryById(payment.getPaymentId());

        //计算总价格
        if(payment.getPaymentPrice() != null){
            payment.setPaymentTotalPrice(new BigDecimal(payment.getPaymentPrice().doubleValue()*payment.getPaymentNum()).setScale(2, BigDecimal.ROUND_HALF_UP));
        }

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
        //JDK动态代理
        PaymentJDKProxy proxyHandler = new PaymentJDKProxy(paymentService);
        PaymentService payProxy = (PaymentService) proxyHandler.getProxy();
        log.info("payProxy:" + payProxy.queryById(15L));

        log.info("serverPort:" + serverPort);
        return this.serverPort;
    }


    //该方法会发生异常，从而调用 @HystrixCommand 方法所配置的降级方法
    @HystrixCommand(fallbackMethod = "getErrorFallbackById", commandProperties = {
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
    //作为@RequestMapping(value="/payment/get/{id}",method = RequestMethod.GET)的快捷方式。也就是可以简化成@PostMapping(value="/payment/get/{id}" )即可
    //@PathVariable 映射 URL 绑定的占位符,一般与@GetMapping一起使用
    @GetMapping("/payment/get/id/{paymentId}")
    public CommonResult queryById(@PathVariable("paymentId") Long paymentId){

        log.info("paymentId:"+paymentId);
        //先查缓存
        String paymentOrder = redisUtil.get(paymentId.toString());
        if(paymentOrder != null ){
            log.info("******查询缓存成功***********" + paymentOrder);
            return new CommonResult(200,"查询成功",paymentOrder);
        }

        //缓存没有再查数据库
        Payment payment = paymentService.queryById(paymentId);
        //将对象转成Json
        String paymentJson = JSONObject.toJSONString(payment);

        log.info("***************查询结果*********");
        if(payment != null){
            //写入缓存，每次查询续期24小时
            redisUtil.set(payment.getPaymentId().toString(),paymentJson,CACHE_TIMEOUT, TimeUnit.SECONDS);
            return new CommonResult(200,"查询成功",paymentJson);
        }else{
            return new CommonResult(444,"查询失败",null);
        }
    }


    //该方法会发生异常，从而调用 @HystrixCommand 方法所配置的降级方法
    @HystrixCommand(fallbackMethod = "getErrorFallbackByPaymentSerial", commandProperties = {
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
    //@PathVariable 映射 URL 绑定的占位符,一般与@GetMapping一起使用
    @GetMapping("/payment/get/{paymentSerial}")
    public CommonResult queryByPaymentSerial(@PathVariable("paymentSerial") String paymentSerial){

        log.info("paymentSerial:"+paymentSerial);
        //先查缓存
        String paymentOrder = redisUtil.get(paymentSerial);
        if(paymentOrder != null ){
            log.info("******查询缓存成功***********" + paymentOrder);
            return new CommonResult(200,"查询成功",paymentOrder);
        }

        //缓存没有再查数据库
        Payment payment = paymentService.queryByPaymentSerial(paymentSerial);
        //将对象转成Json
        String paymentJson = JSONObject.toJSONString(payment);

        log.info("***************查询结果*********");
        if(payment != null){
            //写入缓存，每次查询续期24小时
            redisUtil.set(paymentSerial,paymentJson,CACHE_TIMEOUT, TimeUnit.SECONDS);
            return new CommonResult(200,"查询成功",paymentJson);
        }else{
            return new CommonResult(444,"查询失败",null);
        }
    }

    //定义 GetError 的降级方法
    //方法的返回值、参数列表、参数类型，要与原方法保持一致
    public CommonResult getErrorFallbackById(Long paymentId) {
        return new CommonResult(500,"服务端异常",paymentId);
    }

    public CommonResult getErrorFallbackByPaymentSerial(String paymentSerial) {
        return new CommonResult(500,"服务端异常",paymentSerial);
    }

}
